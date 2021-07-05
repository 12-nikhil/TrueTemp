package softwise.mechatronics.truBlueMonitor.activity;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.adapter.AssetsAdapter;
import softwise.mechatronics.truBlueMonitor.adapter.SensorsLevelAdapter;
import softwise.mechatronics.truBlueMonitor.database.DatabaseClient;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import com.softwise.trumonitor.databinding.ActivityAssetsInfoBinding;
import softwise.mechatronics.truBlueMonitor.dialogs.DialogSensorLevel;
import softwise.mechatronics.truBlueMonitor.helper.DialogHelper;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.helper.ServerDatabaseHelper;
import softwise.mechatronics.truBlueMonitor.implementer.SensorPresenter;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanListener;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanWithDialogListener;
import softwise.mechatronics.truBlueMonitor.listeners.IObserveDataListener;
import softwise.mechatronics.truBlueMonitor.listeners.SerialListener;
import softwise.mechatronics.truBlueMonitor.models.AssetAndSensorInfo;
import softwise.mechatronics.truBlueMonitor.models.RefreshTokenResponse;
import softwise.mechatronics.truBlueMonitor.models.Sensor;
import softwise.mechatronics.truBlueMonitor.models.SensorIds;
import softwise.mechatronics.truBlueMonitor.serverUtils.ApiClients;
import softwise.mechatronics.truBlueMonitor.serverUtils.ServiceListeners.APIService;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;
import softwise.mechatronics.truBlueMonitor.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants.BEARER;
import static softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants.CONTENT_TYPE;

public class AssetsInfoActivity extends ConnectivityActivity implements AssetsAdapter.OnAssetsSelectListeners,
        IObserveDataListener, SensorsLevelAdapter.OnSensorLevelSelectListeners, DialogSensorLevel.OnAddSensorLevelListeners, SerialListener {
    ActivityAssetsInfoBinding mBinding;
    List<Sensor> sensorList = new ArrayList<>();
    private List<AssetAndSensorInfo> mAssetAndSensorInfoList = new ArrayList<>();
    private AssetsAdapter mAssetsAdapter;
    private SensorsLevelAdapter mSensorsAdapter;
    private SearchView searchView;
    private AssetAndSensorInfo mAssetAndSensorInfo;
    private StringBuilder receivedMessage;
    private String newline = TextUtil.newline_crlf;
    private IntentFilter filter;
    private BroadcastReceiver receiveData;
    private int backCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAssetsInfoBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Assets info");
        // mAssetAndSensorInfoList = MethodHelper.getAssetsFromArray(getApplicationContext());
        // initRecyclerView();
        /*mAssetsAdapter.notifyDataSetChanged();
        mBinding.inc.prbLoad.setVisibility(View.GONE);*/
        getAssetsInFoFromSever(getApplicationContext());
        clickListeners();
        filter = new IntentFilter();
        filter.addAction("sensorData");
        receiveData = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras().get("msg") != null) {
                    onReceiveSensorData(String.valueOf(intent.getExtras().get("msg")));
                }
            }
        };
        registerReceiver(receiveData, filter);
    }


    // Call Back method  to get the Message form other Activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            onReceiveSensorData(data.getStringExtra("MESSAGE"));
        }
    }

    public void onReceiveSensorData(String data) {
        if ("date time receive successfully".equals(data)) {
            // open dialog box
            DialogHelper.dismissProgressDialog();
            DialogHelper.conformationDialogCallBack(AssetsInfoActivity.this, getString(R.string.msg_app_ready_to_use), new IBooleanWithDialogListener() {
                @Override
                public void callBack(boolean result, Dialog dialog) {
                    try {
                        if (result) {
                            dialog.dismiss();
                            Intent intent = new Intent(AssetsInfoActivity.this, SensorTemperatureActivity.class);
                            intent.putExtra("asset_id", mAssetAndSensorInfo.getAssetId());
                            startActivity(intent);
                            finish();
                        } else {
                            deallocateSensorFromAssets();
                        }
                    }catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    private void deallocateSensorFromAssets() {
        try {
            new SensorPresenter(getApplicationContext()).deallocateSensorFromAsset(getApplicationContext(), mAssetAndSensorInfo.getAssetId(), new IBooleanListener() {
                @Override
                public void callBack(boolean result) {
                    // clear preference
                    if (result) {
                        SPTrueTemp.clearConnectedAddress(getApplicationContext());
                        // clear activity stack
                        Intent i = new Intent(AssetsInfoActivity.this, LauncherActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void getAssetsInFoFromSever(Context context) {
        //String org = String.valueOf(SPTrueTemp.getToken(context));
        String org = String.valueOf(SPTrueTemp.getUserOrg(context));
        String userId = String.valueOf(SPTrueTemp.getUserId(context));
        String levels = SPTrueTemp.getUserLevel(context);
        String token = SPTrueTemp.getToken(getApplicationContext());
        APIService apiService = ApiClients.getRetrofitInstance(false).create(APIService.class);
        Observable<List<AssetAndSensorInfo>> observable = apiService.getAllAssetsData(BEARER+" "+token,CONTENT_TYPE, org).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<List<AssetAndSensorInfo>>() {
            @Override
            public void onCompleted() {
                mBinding.inc.prbLoad.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mBinding.inc.prbLoad.setVisibility(View.GONE);
                // finish();
                if ("HTTP 401 Unauthorized".equals(e.getMessage())) {
                    callRefreshToken(getApplicationContext());
                }
            }

            @Override
            public void onNext(List<AssetAndSensorInfo> assetInfoList) {
                if (assetInfoList != null && assetInfoList.size() > 0) {
                    mAssetAndSensorInfoList.addAll(assetInfoList);
                    mBinding.inc.recyclerAssets.setVisibility(View.VISIBLE);
                    mBinding.inc.recyclerSensor.setVisibility(View.GONE);
                    mBinding.inc.txtNoData.setVisibility(View.GONE);
                    //mAssetsAdapter.notifyDataSetChanged();
                    initRecyclerView();
                } else {
                    mBinding.inc.recyclerAssets.setVisibility(View.GONE);
                    mBinding.inc.recyclerSensor.setVisibility(View.GONE);
                    mBinding.inc.prbLoad.setVisibility(View.GONE);
                    mBinding.inc.txtNoData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initRecyclerView() {
        mBinding.inc.recyclerAssets.setLayoutManager(new LinearLayoutManager(this));
        mBinding.inc.recyclerAssets.setItemAnimator(new DefaultItemAnimator());
        mAssetsAdapter = new AssetsAdapter(this, mAssetAndSensorInfoList, this::assetsSelect);
        mBinding.inc.recyclerAssets.setAdapter(mAssetsAdapter);
    }

    private void initRecyclerViewForSensor(List<Sensor> entitySensorList) {
        mBinding.inc.recyclerSensor.setLayoutManager(new LinearLayoutManager(this));
        mBinding.inc.recyclerSensor.setItemAnimator(new DefaultItemAnimator());
        mSensorsAdapter = new SensorsLevelAdapter(this, entitySensorList, this::onSensorLevelSelect);
        mBinding.inc.recyclerSensor.setAdapter(mSensorsAdapter);
    }

    private void clickListeners() {
        mBinding.inc.btnContinue.setOnClickListener(v -> {
            if (mAssetAndSensorInfo != null) {
                if (sensorList.size() > 0) {
                    // save sensor data in local db
                    ServerDatabaseHelper.getInstance(getApplicationContext()).saveSensorListInLocalDB(getApplicationContext(), sensorList, new IBooleanListener() {
                        @Override
                        public void callBack(boolean result) {
                            if (result) {
                                DialogHelper.showProgressDialog(AssetsInfoActivity.this, "Please wait");
                                String sensorData = MethodHelper.createSensorDataString(sensorList, mAssetAndSensorInfo.getAssetId());
                                send(sensorData);
                            } else {
                                MethodHelper.showToast(getApplicationContext(), getString(R.string.msg_something_went_wrong));
                            }
                        }
                    });

                }
            }
        });
    }

    private void callRefreshToken(Context context) {
        String id = SPTrueTemp.getUserId(context);
        APIService apiService = ApiClients.getRetrofitInstance(false).create(APIService.class);
        apiService.refreshToken(Integer.parseInt(id)).enqueue(new Callback<RefreshTokenResponse>() {
            @Override
            public void onResponse(Call<RefreshTokenResponse> call, Response<RefreshTokenResponse> response) {
                if (response != null) {
                    RefreshTokenResponse refreshTokenResponse = response.body();
                    SPTrueTemp.saveToken(context, refreshTokenResponse.getMessage());
                    getAssetsInFoFromSever(context);
                }
            }

            @Override
            public void onFailure(Call<RefreshTokenResponse> call, Throwable t) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.mn_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                search(query);
                return false;
            }
        });
        return true;
    }

    private void search(String query) {
        if (mAssetAndSensorInfoList.size() > 0) {
            if ("".equals(query)) {
                mAssetsAdapter.updateList(mAssetAndSensorInfoList);
            } else {
                mAssetsAdapter.getFilter().filter(query);
            }
        }
    }

    private void exitFromScreen() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        Intent intent = new Intent(AssetsInfoActivity.this, PairedDeviceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        System.exit(0);
        finishAffinity();
        /*DialogHelper.conformationDialog(AssetsInfoActivity.this, getString(R.string.msg_exit_screen), result -> {
            if (result) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    return;
                }
                Intent intent = new Intent(AssetsInfoActivity.this, PairedDeviceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                System.exit(0);
                finishAffinity();
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mn_search) {
            return true;
        }
        if (id == android.R.id.home) {
            if (backCount > 0) {
                mBinding.inc.recyclerAssets.setVisibility(View.VISIBLE);
                mBinding.inc.recyclerSensor.setVisibility(View.GONE);
                backCount = 0;
            } else {
                exitFromScreen();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (backCount > 0) {
            mBinding.inc.recyclerAssets.setVisibility(View.VISIBLE);
            mBinding.inc.recyclerSensor.setVisibility(View.GONE);
            backCount = 0;

        } else {
            exitFromScreen();
        }

        //super.onBackPressed();
    }

    @Override
    public void assetsSelect(AssetAndSensorInfo assetAndSensorInfo) {
        backCount++;
        mAssetAndSensorInfo = new AssetAndSensorInfo();
        mAssetAndSensorInfo = assetAndSensorInfo;
        mBinding.inc.prbLoad.setVisibility(View.VISIBLE);
        mBinding.inc.txtAssetName.setVisibility(View.VISIBLE);
        mBinding.inc.txtAssetName.setText(assetAndSensorInfo.getAssetName());
        String sensorData = SPTrueTemp.getSensorId(getApplicationContext());
        //sensorData = "sensor_id:[1,2]";
        SensorIds sensorId = MethodHelper.setOrCreateEntitySensorList(assetAndSensorInfo.getAssetId(), sensorData);
        // send sensorIdAndAssetId to the server
        if (sensorId != null) {
            new SensorPresenter(getApplicationContext(), this).sendAssetAndSensorToServer(getApplicationContext(), sensorId);
        }
    }


    @Override
    public void dataOnComplete() {

    }

    @Override
    public void errorException(Throwable ex) {

    }

    @Override
    public void nextDataLoad(AssetAndSensorInfo data) {
        if (data != null) {
            // save sensor data in local db
            // check data already available or nor
            sensorList.clear();
            sensorList = data.getSensors();
            if (sensorList != null && sensorList.size() > 0) {
                initRecyclerViewForSensor(sensorList);
                mBinding.inc.prbLoad.setVisibility(View.GONE);
                mBinding.inc.btnContinue.setVisibility(View.VISIBLE);
                mBinding.inc.recyclerAssets.setVisibility(View.GONE);
                mBinding.inc.recyclerSensor.setVisibility(View.VISIBLE);
               /* ServerDatabaseHelper.getInstance(getApplicationContext()).saveSensorListInLocalDB(getApplicationContext(), sensorList, new IBooleanListener() {
                    @Override
                    public void callBack(boolean result) {
                        // startActivity(new Intent(AssetsInfoActivity.this,SensorTemperatureActivity.class));
                    }
                });*/
            }
        }
    }

    @Override
    public void onSensorLevelSelect(Sensor entitySensor) {
        FragmentManager manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(null);
        DialogSensorLevel dialogSensorLevel = new DialogSensorLevel(getApplicationContext(), entitySensor, this);
        dialogSensorLevel.setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
        dialogSensorLevel.show(manager, "Edit Fragment");
    }

    @Override
    public void onAddSensorLevel(Sensor entitySensor) {
        for (Sensor sensor : sensorList) {
            if (entitySensor.getSensorId() == sensor.getSensorId()) {
                int index = sensorList.indexOf(sensor);
                sensorList.get(index).setUpdateFrequency(entitySensor.getUpdateFrequency());
                sensorList.get(index).setAlarmLow(entitySensor.getAlarmLow());
                sensorList.get(index).setAlarmHigh(entitySensor.getAlarmHigh());
                sensorList.get(index).setWarningLow(entitySensor.getWarningLow());
                sensorList.get(index).setWarningHigh(entitySensor.getWarningHigh());
                mSensorsAdapter.notifyDataSetChanged();
                // update data in local db
                EntitySensor eSensor = MethodHelper.getSingleEntitySensor(getApplicationContext(),entitySensor);
                DatabaseClient.getInstance(getApplicationContext()).updateSensorData(eSensor);
            }
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiveData);
        super.onDestroy();
    }
}