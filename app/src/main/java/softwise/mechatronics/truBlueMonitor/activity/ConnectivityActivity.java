package softwise.mechatronics.truBlueMonitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.bluetoothListener.SerialService;
import softwise.mechatronics.truBlueMonitor.bluetoothListener.SerialSocket;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import com.softwise.trumonitor.databinding.ActivityConnectivityBinding;
import softwise.mechatronics.truBlueMonitor.helper.DialogHelper;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.helper.ServerDatabaseHelper;
import softwise.mechatronics.truBlueMonitor.listeners.IObserveEntitySensorListener;
import softwise.mechatronics.truBlueMonitor.listeners.IReceiveDataListeners;
import softwise.mechatronics.truBlueMonitor.listeners.SerialListener;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;
import softwise.mechatronics.truBlueMonitor.utils.TextUtil;

public class ConnectivityActivity extends AppCompatActivity implements ServiceConnection, SerialListener, IReceiveDataListeners, IObserveEntitySensorListener {
    public static SerialService service = null;
    public boolean initialStart = true;
    ActivityConnectivityBinding mBinding;
    String deviceName = null, deviceAddress = null;
    private boolean hexEnabled = false;
    private String newline = TextUtil.newline_crlf;
    private StringBuilder receivedMessage;
    private String memoryCount;
    private String TAG = "ConnectivityActivity";
    private int dataPoints;
    private CountDownTimer countDownTimer;
    // private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityConnectivityBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        if (getIntent().getExtras() != null) {
            if ("ST".equals(SPTrueTemp.getCallingType(getApplicationContext()))) {
                finish();
            }else {
                deviceName = getIntent().getStringExtra("device_name");
                deviceAddress = getIntent().getStringExtra("device_mac");
                loadService();
                mBinding.btnConnectDisconnect.setOnClickListener(v -> {
                    if ("Connect".equals(mBinding.btnConnectDisconnect.getText().toString().trim())) {
                        setGIF();
                        loadService();
                    }
                });
                mBinding.btnCancel.setOnClickListener(v -> {
                    service.disconnect();
                    finish();
                });
            }
        }
    }

    private void loadService() {
        if (!isMyServiceRunning(SerialService.class)) {
            SPTrueTemp.saveCallingType(getApplicationContext(), null);
            Log.d(TAG, "Service active");
            startService();
        } else {
            Log.d(TAG, "Service Not active");
            if ("".equals(SPTrueTemp.getSensorId(getApplicationContext())) || !service.connected) {
                initialStart = true;
                stopService(new Intent(ConnectivityActivity.this, SerialService.class));
                setGIF();
                SPTrueTemp.saveCallingType(getApplicationContext(), null);
                startService();
            }
        }
    }

    private void startService() {
        getApplication().bindService(new Intent(getApplication(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /*
     * Serial + UI
     */
    public void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            SerialSocket socket = new SerialSocket(getApplication(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    public void send(String str) {
        Log.e("Send wala msg",str);
        try {
            String msg;
            byte[] data;
            clearReceiveData();
            if (hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                data = (str + newline).getBytes();
            }
            service.write(data);
            if ("send memory data".equals(str)) {
                final String[] messageReceive = {null};
                countDownTimer = new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        if (receivedMessage.toString().contains(")")) {
                            messageReceive[0] = "Z";
                            countDownTimer.cancel();
                            countDownTimer.onFinish();
                        }
                    }
                    public void onFinish() {
                        if (messageReceive[0] == null) {
                            resetButtonDialog();
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    public void receive(byte[] data) {
        if (service.isBluetoothConnected()) {
            String msg = new String(data);
            Log.e("Received data start", msg);
            if (!"".equals(msg) && msg.trim().length() > 0) {
                try {
                    receivedMessage.append(TextUtil.toCaretString(msg.trim(), newline.length() != 0));
                    if (receivedMessage.length() > 0) {
                        String last = String.valueOf(receivedMessage.toString().charAt(receivedMessage.toString().length() - 1));
                        if (receivedMessage.toString().contains("(")) {
                            String[] dataPointsAndDataArray = receivedMessage.toString().split("\\(");
                            dataPoints = 0;
                            if (!"".equals(dataPointsAndDataArray[0])) {
                                dataPoints = Integer.parseInt(dataPointsAndDataArray[0]);
                            }
                        }
                        switch (last) {
                            case ")":// receive data from memory
                                stopCountDownTimer();
                                // 12(data)
                                String[] dataPointsAndDataArray = receivedMessage.toString().split("\\(");
                                if (!"".equals(dataPointsAndDataArray[0])) {
                                    dataPoints = Integer.parseInt(dataPointsAndDataArray[0]);
                                }
                                Log.e("Data Points ", String.valueOf(dataPoints));
                                String memoryData = dataPointsAndDataArray[1].replace(")", "").trim();
                                //String memoryData = dataPointsAndDataArray[0].replace(")", "").trim();
                                ServerDatabaseHelper.getInstance(getApplicationContext()).saveSensorDataFromMemoryToServer(getApplicationContext(), memoryData, dataPoints, this::getEntitySensor);
                                send("send sensor id");
                                clearReceiveData();
                                dataPoints = 0;
                                break;
                            case "]":// sensor id
                                stopCountDownTimer();
                                DialogHelper.dismissProgressDialog();
                                SPTrueTemp.saveSensorId(getApplicationContext(), receivedMessage.toString());
                                // open assetInfo activity
                                Intent intent = new Intent(ConnectivityActivity.this, AssetsInfoActivity.class);
                                startActivity(intent);
                                clearReceiveData();
                                dataPoints = 0;
                                break;
                            case "_":
                                stopCountDownTimer();
                                //if (dataPoints == 0) {
                                    ServerDatabaseHelper.getInstance(getApplicationContext()).saveSensorDataFromMemoryToServer(getApplicationContext(), receivedMessage.toString(), 0, this::getEntitySensor);
                                  /*  Intent i = new Intent("sensorTemp");
                                    i.putExtra("msg", receivedMessage.toString()); //EDIT: this passes a parameter to the receiver
                                    sendBroadcast(i);*/
                                    send("Y");
                                    clearReceiveData();
                                //}
                                break;
                        }
                        if ("alarm warning level receive successfully".equals(receivedMessage.toString())) {
                            send(MethodHelper.getTimeInStringComma(System.currentTimeMillis()) + ",DateTime received");
                        }
                        if ("date time receive successfully".equals(receivedMessage.toString())) {
                            //new AssetsInfoActivity().onReceiveSensorData(receivedMessage.toString());
                            // this.onSerialRead(data);
                            //new AssetsInfoActivity().onSerialRead(data);
                            Intent i = new Intent("sensorData");
                            i.putExtra("msg", receivedMessage.toString()); //EDIT: this passes a parameter to the receiver
                            sendBroadcast(i);
                            clearReceiveData();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            Log.d(TAG, "Bluetooth disconnect");
        }
    }

    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void clearReceiveData() {
        receivedMessage = new StringBuilder();
    }

    private void setGIF() {
        mBinding.imgConnetcing.setVisibility(View.VISIBLE);
        try {
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.bluetooth_connecting) //or url
                    .into(mBinding.imgConnetcing);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if (initialStart) {
            mBinding.btnConnectDisconnect.setVisibility(View.GONE);
            mBinding.btnCancel.setVisibility(View.VISIBLE);
            Resources res = getResources();
            String text = String.format(res.getString(R.string.status_connecting), deviceName);
            mBinding.txtConnectionStatus.setText(text);
            mBinding.prbConnecting.setVisibility(View.VISIBLE);
            initialStart = false;
            setGIF();
            connect();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.e("Bluetooth ", "Disconnect");
        //mBinding.btnConnectDisconnect.setVisibility(View.VISIBLE);
        Resources res = getResources();
        String text = String.format(res.getString(R.string.status_disconnected), deviceName);
        mBinding.txtConnectionStatus.setText(text);
        mBinding.imgConnetcing.setVisibility(View.GONE);
        mBinding.prbConnecting.setVisibility(View.GONE);
        String msg = String.valueOf(Html.fromHtml(getResources().getString(R.string.msg_bluetooth_disconnected)));
        connectionFailedDialog(msg);
    }

    public void serviceDisConnect() {
        if (service != null) {
            service.disconnect();
            SPTrueTemp.clearConnectedAddress(getApplicationContext());
        }
    }

    @Override
    public void onSerialConnect() {
        // send data to true temp
        send("send memory data");
        Resources res = getResources();
        String text = String.format(res.getString(R.string.status_connected), deviceName);
        mBinding.txtConnectionStatus.setText(text);
        //mBinding.imgConnetcing.setVisibility(View.GONE);
    }

    @Override
    public void onSerialConnectError(Exception e) {
        Log.e("Bluetooth ", "Connection Error CC " + e.getMessage());
        mBinding.prbConnecting.setVisibility(View.GONE);
        // mBinding.btnConnectDisconnect.setVisibility(View.VISIBLE);
        mBinding.imgConnetcing.setVisibility(View.GONE);
        String msg = "read failed, socket might closed or timeout, read ret: -1";
        if (!msg.equals(e.getMessage())) {
            msg = String.valueOf(Html.fromHtml(getResources().getString(R.string.msg_reset_bluetooth)));
        } else {
            msg = getResources().getString(R.string.status_connection_failed);
        }
        connectionFailedDialog(msg);
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialReadString(String data) {
    }

    @Override
    public void onSerialIoError(Exception e) {
        Log.e("Bluetooth ", "IO Error CC" + e.getMessage());
        mBinding.prbConnecting.setVisibility(View.GONE);
        mBinding.imgConnetcing.setVisibility(View.GONE);
        String text = String.valueOf(Html.fromHtml(getResources().getString(R.string.msg_reset_bluetooth)));
        connectionFailedDialog(text);
        // mBinding.btnConnectDisconnect.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceiveData(byte[] data) {

    }

    private void connectionFailedDialog(String msg) {
        if(!"ST".equals(SPTrueTemp.getCallingType(getApplicationContext()))) {
            DialogHelper.messageDialog(ConnectivityActivity.this, "Warning", msg, result -> {
                if (result) {
                    if (service != null) {
                        serviceDisConnect();
                        stopService(new Intent(ConnectivityActivity.this, SerialService.class));
                        SPTrueTemp.clearConnectedAddress(ConnectivityActivity.this);
                        System.exit(0);
                        finish();
                    }
                    //loadService();
                }
            });
        }else {
            Intent i = new Intent("sensorTemp");
            i.putExtra("error", "error"); //EDIT: this passes a parameter to the receiver
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
        }
    }

    private void resetButtonDialog() {
        DialogHelper.messageDialog(ConnectivityActivity.this, "Warning", getString(R.string.msg_reset_button_pressed), result -> {
            if (result) {
                if (service != null) {
                    service.disconnect();
                    stopService(new Intent(ConnectivityActivity.this, SerialService.class));
                    SPTrueTemp.clearConnectedAddress(ConnectivityActivity.this);
                    finish();
                }
                //loadService();
            } else {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if (service != null) {
            service.disconnect();
        }*/
    }

    @Override
    public void getEntitySensor(EntitySensor entitySensor) {

    }
}