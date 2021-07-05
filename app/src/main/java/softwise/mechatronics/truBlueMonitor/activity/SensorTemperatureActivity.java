package softwise.mechatronics.truBlueMonitor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.adapter.SensorsAdapter;
import softwise.mechatronics.truBlueMonitor.bluetoothListener.MyService;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import com.softwise.trumonitor.databinding.ActivitySensorTemperatureBinding;
import softwise.mechatronics.truBlueMonitor.helper.DialogHelper;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.implementer.SensorPresenter;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanListener;
import softwise.mechatronics.truBlueMonitor.listeners.IObserveEntitySensorListener;
import softwise.mechatronics.truBlueMonitor.models.SensorTempViewModel;
import softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;
import softwise.mechatronics.truBlueMonitor.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

public class SensorTemperatureActivity extends AppCompatActivity implements SensorsAdapter.OnSensorTempSelectListeners, IObserveEntitySensorListener {

    ActivitySensorTemperatureBinding mBinding;
    private SensorsAdapter mSensorsAdapter;
    private List<EntitySensor> entitySensorsList;
    private int assetId;
    private BluetoothAdapter mBluetoothAdapter;
    private int callType = 0;
    private EntitySensor entitySensor;

    private final BroadcastReceiver receiveData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getExtras() != null) {
                    if (intent.getExtras().get("msg") != null) {
                        //setSensorTempData(String.valueOf(intent.getExtras().get("msg")));
                        entitySensor = (EntitySensor) intent.getParcelableExtra("msg");
                        setTempData(entitySensor);
                    }
                    if (intent.getExtras().get("error") != null) {
                        showErrorDialog();
                    }
                } else {
                    Log.e("Extra", "Intent getExtras null");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    private SensorPresenter sensorPresenter;
    private boolean hexEnabled = false;
    private String newline = TextUtil.newline_crlf;
    private SensorTempViewModel mSensorTempViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySensorTemperatureBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            setTitle("Temperature");
            SPTrueTemp.saveCallingType(getApplicationContext(), "ST");
            receiveTempDataByReceiver();
            entitySensorsList = new ArrayList<>();
            sensorPresenter = new SensorPresenter(this);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (getIntent().getExtras() != null) {
                assetId = Integer.parseInt(String.valueOf(getIntent().getExtras().get("asset_id")));
            }
            initRecyclerViewForSensor();
            initViewModel();
            mBinding.refreshView.setOnRefreshListener(this::getDataFromLocalDB);
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void receiveTempDataByReceiver() {
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiveData,
                new IntentFilter("sensorTemp"));
    }

    private void initViewModel() {
        try {
            mSensorTempViewModel = new ViewModelProvider(SensorTemperatureActivity.this).get(SensorTempViewModel.class);
            // This method return false if there is an error, so if it does, we should close.
            if (!mSensorTempViewModel.setupViewModel(SensorTemperatureActivity.this)) {
                Log.e("View Model", "Issue in view model");
                finish();
            }
            getDataFromLocalDB();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getDataFromLocalDB() {
        if (mSensorTempViewModel != null) {
            mSensorTempViewModel.fetchSensorData().observe(this, this::loadTemperature);
        }
    }
    private void loadTemperature(List<EntitySensor> eSList){
        if (eSList.size() > 0) {
            callType++;
            entitySensorsList.clear();
            entitySensorsList.addAll(eSList);
            mSensorsAdapter.notifyDataSetChanged();
            mBinding.recyclerSensor.setVisibility(View.VISIBLE);
            mBinding.txtEmptyData.setVisibility(View.GONE);
            mBinding.prbLoad.setVisibility(View.GONE);
            if (callType == 1) {
                send("send temp data");
            }
        } else {
            mBinding.recyclerSensor.setVisibility(View.GONE);
            mBinding.txtEmptyData.setVisibility(View.VISIBLE);
            mBinding.prbLoad.setVisibility(View.GONE);
            mBinding.incBattery.cardParent.setVisibility(View.GONE);
            //finish();
        }
    }

    private void initRecyclerViewForSensor() {
        try {
            mBinding.recyclerSensor.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recyclerSensor.setItemAnimator(new DefaultItemAnimator());
            mSensorsAdapter = new SensorsAdapter(this, entitySensorsList, this::onSensorSelect);
            mBinding.recyclerSensor.setAdapter(mSensorsAdapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* Disconnect our own bluetooth and redirect to Paired device Activity
     * stop service
     * clear shared preference(only device name and address
     * open paired activity*/
    private void disconnectBluetooth() {
        try {
            String msg = String.valueOf(Html.fromHtml(getResources().getString(R.string.msg_disconnect)));
            DialogHelper.conformationDialog(SensorTemperatureActivity.this, msg, result -> {
                if (result) {
                    disableBluetooth();
                    openYourActivity(null);
                    finish();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* send request to server for deallocate assets with sensor
     * disconnect bluetooth
     * clear shared preference(only device name and address
     * open paired activity*/
    private void shutdownBluetooth() {
        try {
            if(entitySensorsList!=null && entitySensorsList.size()>0) {
                String msg = String.valueOf(Html.fromHtml(getResources().getString(R.string.msg_stop_reading)));
                DialogHelper.conformationDialog(SensorTemperatureActivity.this, msg, new IBooleanListener() {
                    @Override
                    public void callBack(boolean result) {
                        if (result) {
                            sensorPresenter.deallocateSensorFromAsset(getApplicationContext(), assetId, new IBooleanListener() {
                                @Override
                                public void callBack(boolean result) {
                                    if (result) {
                                        send("N");
                                        openYourActivity(null);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                });
            }else {
                openYourActivity(null);
                finish();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void disableBluetooth() {
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    /*
     * Before logout
     * Upload data on server (check the terms)
     * Disconnect the all background services
     * call logout API */
    // logout api call
    // but for testing only call splashScreen Activity
    private void logoutDeallocateSensors() {
        try {
            DialogHelper.conformationDialog(this, getResources().getString(R.string.msg_logout), result -> {
                if (result) {
                    try {
                        SensorPresenter sensorPresenter = new SensorPresenter(getApplicationContext());
                        send("N");
                        logoutFromApp(sensorPresenter);
                        /*sensorPresenter.deallocateSensorFromAsset(getApplicationContext(), assetId, new IBooleanListener() {
                            @Override
                            public void callBack(boolean result) {
                                if (result) {
                                    send("N");
                                    logoutFromApp(sensorPresenter);
                                } else {
                                    MethodHelper.showToast(getApplicationContext(), getString(R.string.msg_something_went_wrong));
                                }
                            }
                        });*/

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void logoutFromApp(SensorPresenter sensorPresenter) {
        try {
            sensorPresenter.logout(SensorTemperatureActivity.this,assetId, new IBooleanListener() {
                @Override
                public void callBack(boolean result) {
                    if (result) {
                        try {
                            SPTrueTemp.clearSharedPref(getApplicationContext());
                            openYourActivity("L");
                            finishAffinity();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        MethodHelper.showToast(getApplicationContext(), getString(R.string.msg_something_went_wrong));
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openYourActivity(String type) {
        try {
            stopService();
            Intent intent;
            ConnectivityActivity.service.disconnect();
            SPTrueTemp.clearConnectedAddress(getApplicationContext());
            MethodHelper.stopAlarm();
            if ("L".equals(type)) {
                intent = new Intent(SensorTemperatureActivity.this, LauncherActivity.class);
            } else {
                intent = new Intent(SensorTemperatureActivity.this, PairedDeviceActivity.class);
                // SPTrueTemp.clearConnectedAddress(getApplicationContext());
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showErrorDialog() {
        try {
            // 0 - represent esp32 device connection failed or connection lost
            MethodHelper.playAssetSound(getApplicationContext(), 0);
            String msg = getResources().getString(R.string.status_connection_failed);
            DialogHelper.messageDialog(SensorTemperatureActivity.this, "Warning", msg, result -> {
                if (result) {
                    if (ConnectivityActivity.service != null) {
                        openYourActivity(null);
                        finish();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            shutdownBluetooth();
        }
        if (id == R.id.mn_ble_disconnect) {
            disconnectBluetooth();
        }
        if (id == R.id.mn_logout) {
            logoutDeallocateSensors();
        }
        return super.onOptionsItemSelected(item);
    }

    public void send(String str) {
        try {
            String msg;
            byte[] data;

            if (hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                data = (str + newline).getBytes();
            }
            ConnectivityActivity.service.write(data);
            Log.e("Send Data ST", str);
        } catch (Exception e) {
            ConnectivityActivity.service.onSerialIoError(e);
        }
    }

    @Override
    public void onSensorSelect(EntitySensor entitySensor) {

        Intent intent = new Intent(SensorTemperatureActivity.this, SensorGraphActivity.class);
        intent.putExtra("sensor", (Parcelable) entitySensor);
        startActivity(intent);
        // finish();
    }

    private void displayBatteryValue(String battery) {
        try {
            if ("0.00".equals(battery) || ("00.00".equals(battery))) {
                mBinding.incBattery.cardParent.setVisibility(View.GONE);
            } else {
                mBinding.incBattery.cardParent.setVisibility(View.VISIBLE);
                mBinding.incBattery.progressBarFull.setProgress(100);
                String lable = SPTrueTemp.getConnectedBluetooth(getApplicationContext()) + " Battery Level";
                mBinding.incBattery.lblBattery.setText(lable);
                float bt = Float.parseFloat(battery);
                String value = Math.round(bt) + "%";
                mBinding.incBattery.txtBattery.setText(value);
                mBinding.incBattery.progressBar.setProgress(Math.round(bt));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setTempData(EntitySensor entitySensor) {
        try {
            displayBatteryValue(SPTrueTemp.getBatteryLevel(getApplicationContext()));
            for (EntitySensor sensor : entitySensorsList) {
                if (entitySensor.getBle_sensor_id() == sensor.getBle_sensor_id()) {
                    sensor.setTemp_value(entitySensor.getTemp_value());
                    sensor.setUnit(entitySensor.getUnit());
                    mSensorsAdapter.notifyDataSetChanged();
                    return;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiveData);
            Log.e("Ondestroy ", "destroy ST");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        shutdownBluetooth();
    }

    @Override
    public void getEntitySensor(EntitySensor entitySensor) {
    }


    public void stopService() {
        try {
            Intent stopIntent = new Intent(SensorTemperatureActivity.this, MyService.class);
            stopIntent.setAction(BluetoothConstants.ACTION.STOP_ACTION);
            startService(stopIntent);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}