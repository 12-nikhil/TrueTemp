package softwise.mechatronics.truBlueMonitor.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.adapter.DeviceAdapter;
import softwise.mechatronics.truBlueMonitor.bluetoothListener.DataUploadService;
import com.softwise.trumonitor.databinding.ActivityPairedDeviceBinding;
import softwise.mechatronics.truBlueMonitor.helper.DialogHelper;
import softwise.mechatronics.truBlueMonitor.implementer.SensorPresenter;
import softwise.mechatronics.truBlueMonitor.models.PairedDevViewModel;
import softwise.mechatronics.truBlueMonitor.receiver.NetworkChangeReceiver;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;

public class PairedDeviceActivity extends AppCompatActivity implements DeviceAdapter.OnDeviceSelectListeners,
        NetworkChangeReceiver.NetworkChangeCallback {
    ActivityPairedDeviceBinding binding;
    DeviceAdapter adapter;
    View view;
    NetworkChangeReceiver networkChangeReceiver;
    String TAG = "PairedDeviceActivity";
    private PairedDevViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* FirebaseApp.initializeApp(getApplicationContext());
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);*/
        binding = ActivityPairedDeviceBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        networkChangeReceiver = new NetworkChangeReceiver(this);
        // Setup our ViewModel
        setUpViewModels();
        clickListeners();
        //setSensorTempData();
        refreshToken();
        onStartJobIntentService(view);
        //subScripbeTpopic();
    }

   /* private void subScripbeTpopic()
    {
        FirebaseMessaging.getInstance().subscribeToTopic("truTemp")
                .addOnCompleteListene
                r(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribed";
                        if (!task.isSuccessful()) {
                            msg = "msg_subscribe_failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(PairedDeviceActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
    public void onStartJobIntentService(View view) {
        Intent mIntent = new Intent(this, DataUploadService.class);
        mIntent.putExtra("maxCountValue", 1000);
        mIntent.putExtra("callingType","M");
        DataUploadService.enqueueWork(this, mIntent);
    }

    private void refreshToken()
    {
        new SensorPresenter(getApplicationContext()).callRefreshToken(this,null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetoothConnectivity();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    private void setUpViewModels() {
        viewModel = new ViewModelProvider(this).get(PairedDevViewModel.class);
        // This method return false if there is an error, so if it does, we should close.
        if (!viewModel.setupViewModel()) {
            finishAffinity();
        }
    }

    private void clickListeners() {
        binding.fab.setOnClickListener(v -> {
            startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
        });
    }

    private void checkBluetoothConnectivity() {
        // [#11] Ensures that the Bluetooth is available on this device before proceeding.
        boolean hasBluetooth = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if (!hasBluetooth) {
            DialogHelper.showMessageDialog(getApplicationContext(),"Warning", getString(R.string.bluetooth_not_available_message));
        }

        if (checkBluetoothAvailability()) {
            initRecyclerView();
            // Setup the SwipeRefreshLayout
            initRefreshView();
            // rt observing the data sent to us by the ViewModel
            viewModel.getPairedDeviceList().observe(PairedDeviceActivity.this, bluetoothDevices -> {
                if (bluetoothDevices.size() > 0) {
                    adapter.updateList(bluetoothDevices);
                    binding.txtEmptyData.setVisibility(View.GONE);
                } else {
                    binding.txtEmptyData.setVisibility(View.VISIBLE);
                    binding.txtEmptyData.setText(getString(R.string.enabling_bluetooth));
                }
            });
            //viewModel.getPairedDeviceList().observe(PairedDeviceActivity.this, adapter::updateList);
            // Immediately refresh the paired devices list
            viewModel.refreshPairedDevices();
        }

    }

    private void initRefreshView() {
        binding.mainSwiperefresh.setOnRefreshListener(() -> {
            viewModel.refreshPairedDevices();
            binding.mainSwiperefresh.setRefreshing(false);
        });
    }

    private boolean checkBluetoothAvailability() {
        //bluetooth
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bAdapter.isEnabled()) {
            // show dialog with callback
            //Snackbar.make(view, R.string.enabling_bluetooth, Snackbar.LENGTH_LONG).show();
            //enable bluetooth
            // bAdapter.enable();
            // startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
            // bluetooth.turnOnBluetoothAndScheduleDiscovery();
            binding.txtEmptyData.setVisibility(View.VISIBLE);
            binding.txtEmptyData.setText(getString(R.string.enabling_bluetooth));
            return false;
        }
        return true;
    }

    private void initRecyclerView() {
        // Setup the RecyclerView
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new DeviceAdapter(this::deviceSelect);
        binding.recycler.setAdapter(adapter);
    }

    private void jumpToActivity(String name, String address) {
        Intent intent = new Intent(this, ConnectivityActivity.class);
        intent.putExtra("device_name", name);
        intent.putExtra("device_mac", address);
        SPTrueTemp.saveConnectedBluetoothName(getApplicationContext(), name);
        SPTrueTemp.saveConnectedMacAddress(getApplicationContext(), address);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void deviceSelect(BluetoothDevice bluetoothDevice) {
        SPTrueTemp.saveCallingType(getApplicationContext(),null);
        jumpToActivity(bluetoothDevice.getName(), bluetoothDevice.getAddress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.getRoot().removeView(view);
    }

    @Override
    public void onNetworkChanged(boolean status) {
        Log.e("PairedActivity", "Network status " + status);
    }
}