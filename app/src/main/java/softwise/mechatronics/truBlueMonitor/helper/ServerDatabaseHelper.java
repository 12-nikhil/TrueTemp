package softwise.mechatronics.truBlueMonitor.helper;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import softwise.mechatronics.truBlueMonitor.activity.SensorTemperatureActivity;
import softwise.mechatronics.truBlueMonitor.bluetoothListener.MyService;
import softwise.mechatronics.truBlueMonitor.database.DatabaseClient;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import softwise.mechatronics.truBlueMonitor.database.SensorTempTime;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ISensorTempCallback;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ISingleSensorData;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.SensorDatabaseCallback;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanListener;
import softwise.mechatronics.truBlueMonitor.listeners.IObserveEntitySensorListener;
import softwise.mechatronics.truBlueMonitor.models.Sensor;
import softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants;
import softwise.mechatronics.truBlueMonitor.utils.ConnectionUtils;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;

import java.util.List;

import static softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants.ALARM_UPDATE_FREQUENCY;


public class ServerDatabaseHelper {
    private static ServerDatabaseHelper mInstance;
    private static Context mCtx;
    private static MediaPlayer mediaPlayer;
    // private DatabaseClient databaseClient;
    private FusedLocationProviderClient fusedLocationClient;
    private IObserveEntitySensorListener mIObserveEntitySensorListener;

    public ServerDatabaseHelper(Context context) {
        //databaseClient = new DatabaseClient(context);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mCtx = context;
    }

    public static synchronized ServerDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ServerDatabaseHelper(context);
        }
        mCtx = context;
        return mInstance;
    }


    public void saveSensorDataFromMemoryToServer(Context context, String receiveData, int dataPoint, IObserveEntitySensorListener iObserveEntitySensorListener) {
        try {
            this.mIObserveEntitySensorListener = iObserveEntitySensorListener;
            // temp = "1,26.00C@-06/11/2020,13:29:00_";
            // temp = "1,25.56C@!0,10.00,!12/1/2021,14:49:42_";
           /* dataPoints = 3;
            receiveData = "1,25.81C@!1,0.00,21/1/2021,12:39:43_ 1,25.81C@!1,0.00,21/1/2021, 12:40:47_ 1,25.75C@!1,0.00,21/1/2021,12:41:51_";*/
            if (receiveData.contains("_")) {
                String[] memorySensorArray = receiveData.split("_");
                if (memorySensorArray.length > 0) {
                    for (int i = 0; i < memorySensorArray.length; i++) {
                        String[] sensorAndDateArray = memorySensorArray[i].trim().split("!");
                        String[] tempArray = sensorAndDateArray[0].split("@");
                        String[] assetsDateTimeArray = sensorAndDateArray[1].split(",");
                        int assetId = Integer.parseInt(assetsDateTimeArray[0]);
                        String battery = assetsDateTimeArray[1];
                        SPTrueTemp.saveBatteryLevel(context, battery);
                        //17,0.00,16/3/2021,10:48:59
                        String date = assetsDateTimeArray[2];
                        String time = assetsDateTimeArray[3];
                        for (int j = 0; j < tempArray.length; j++) {
                            String[] valueArray = tempArray[j].split(",");
                            String sensorId = valueArray[0];
                            String unit = valueArray[1].substring(valueArray[1].length() - 1);
                            String tempValue = valueArray[1].replace(unit, "".trim());
                            String status = valueArray[2];
                            // data received from memory
                            // no need to save in local db
                            // directly upload on server
                            if (dataPoint > 0) {
                           // if(isFromMemory){
                                EntitySensor entitySensor = MethodHelper.createSingleEntitySensor(context, Integer.parseInt(sensorId), tempValue, unit, date + " " + time, status, assetId);
                                //getSensorDataFromDB(context, Integer.parseInt(sensorId),assetId, tempValue, unit, sensorAndDateArray[1], true);
                                //get location
                                getCurrentLocation(entitySensor, true, 0);
                            } else {
                                getSensorDataFromDB(context, Integer.parseInt(sensorId), assetId, tempValue, unit, status, date + " " + time, false);
                            }
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getSensorDataFromDB(Context context, int sensorId, int asset_id, String temp, String unit, String status, String time, boolean isFromMemory) {
        // get sensor data from local db
        //EntitySensor entitySensor = DatabaseClient.getInstance(mCtx).getEntitySensor(sensorId);
        DatabaseClient.getInstance(mCtx).getEntitySensorData(sensorId, new ISingleSensorData() {
            @Override
            public void onLoadSensor(EntitySensor entitySensor) {
                if (entitySensor != null) {
                    //if(!temp.equals(entitySensor.getTemp_value())) {
                    entitySensor.setBle_sensor_id(sensorId);
                    entitySensor.setAsset_id(asset_id);
                    entitySensor.setTemp_value(temp);
                    entitySensor.setUnit(unit);
                    entitySensor.setTime(time);
                    entitySensor.setStatus(MethodHelper.sensorStatusVale(status));
                    //entitySensor.setStatus(MethodHelper.checkAlarmWaningValue(Float.parseFloat(temp), entitySensor));
                    if ("ST".equals(SPTrueTemp.getCallingType(mCtx))) {
                        try {
                            Intent i = new Intent("sensorTemp");
                            i.putExtra("msg", entitySensor); //EDIT: this passes a parameter to the receiver
                            LocalBroadcastManager.getInstance(mCtx).sendBroadcast(i);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (isFromMemory) {
                        getCurrentLocation(entitySensor, isFromMemory, 0);
                    } else {
                        startTimeForFrequency(entitySensor, isFromMemory);
                    }
                }
            }
        });
    }

    private void startTimeForFrequency(EntitySensor entitySensor, boolean isFromMemory) {
        if (entitySensor.isFlag()) {
            if (!isFromMemory) {
                String frequency = null;
                if ("alarm_low".equals(entitySensor.getStatus()) || "alarm_high".equals(entitySensor.getStatus())) {
                    // update data in every one min(00:01:00)
                    frequency = ALARM_UPDATE_FREQUENCY;
                    //MethodHelper.raisedAlarm(mCtx, 1);
                }
                // safe ,warning low ,warning high
                else {
                    // update data as per frequency set
                    frequency = entitySensor.getUpdate_frequency();
                    // if alarm warning safe then stop siren
                    MethodHelper.stopAlarm();
                }
                checkFrequencyUpdateTime(entitySensor, frequency);
            } else {
                // if data comes form memory then no need to check alarm / warning conditions
                // save data in sensor temp table
                // upload on server
                getCurrentLocation(entitySensor, isFromMemory, 0);
            }
        }
    }

    private void checkFrequencyUpdateTime(EntitySensor entitySensor, String updateFrequency) {
        int frequency = MethodHelper.convertStringTimeToSec(updateFrequency);
        boolean result = MethodHelper.getUpdateTime(mCtx, updateFrequency);
        if (result) {
            DatabaseClient.getInstance(mCtx).updateSensorData(entitySensor);
            getCurrentLocation(entitySensor, false, frequency);
            mIObserveEntitySensorListener.getEntitySensor(entitySensor);
        }
    }


    private void getCurrentLocation(EntitySensor entitySensor, boolean isFromMemory, int frequency) {
        if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                try {
                    // Got last known location. In some rare situations this can be null.
                    String lat = "0.00", lng = "0.00";
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (task.getResult().getLatitude() > 0) {
                            lat = String.valueOf(task.getResult().getLatitude());
                            SPTrueTemp.saveLatitude(mCtx, lat);
                        }
                        if (task.getResult().getLongitude() > 0) {
                            lng = String.valueOf(task.getResult().getLongitude());
                            SPTrueTemp.saveLongitude(mCtx, lng);
                        }
                        Log.e("LOCATION ", lat + "," + lng);
                    } else {
                        // this condition is for
                        // if lat lng get null then fetch lat lng from Shared preference
                        lat = SPTrueTemp.getLatitude(mCtx);
                        lng = SPTrueTemp.getLongitude(mCtx);
                    }
                    saveAndUploadDataOnSever(entitySensor, lat, lng, isFromMemory, frequency);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }


    private void saveAndUploadDataOnSever(EntitySensor entitySensor, String lat, String lng, boolean isFromMemory, int frequency) {
        entitySensor.setLat(lat);
        entitySensor.setLng(lng);
        SensorTempTime sensorTempTime = MethodHelper.createSensorTempTime(entitySensor, lat, lng, isFromMemory);
        if (ConnectionUtils.getConnectivityStatusString(mCtx)) {
            sensorTempTime.setFlag(true);
            sensorTempTime.setAssets_id(entitySensor.getAsset_id());
            entitySensor.setFlag(true);
            // upload temp data on server
            if (!isMyServiceRunning(mCtx, MyService.class)) {
                Log.e("Upload Temp Service ", "Stopped");
                sensorTempTime.setUploadPending(true);
                onStartJobIntentService(mCtx, String.valueOf(entitySensor.getBle_sensor_id()), entitySensor, isFromMemory, frequency);
            } else {
                Log.e("Upload Temp Service ", "Running");
                onStartJobIntentService(mCtx, String.valueOf(entitySensor.getBle_sensor_id()), entitySensor, isFromMemory, frequency);
            }
        }
        // save temperature data in local database
        if(!isFromMemory) {
            DatabaseClient.getInstance(mCtx).addSensorTemp(sensorTempTime);
        }
    }

    private void uploadDataOnSever(EntitySensor entitySensor, boolean isFromMemory, int frequency, SensorTempTime sensorTempTime) {

       /* JsonArray jsonArray = MethodHelper.getJsonArray(mCtx, entitySensor,isFromMemory,false);
        if (jsonArray != null) {

            //onStartJobIntentService(mCtx, String.valueOf(entitySensor.getBle_sensor_id()),jsonArray.toString());
            // remove below code comment when api working properly
            new SensorPresenter(mCtx).uploadData(String.valueOf(entitySensor.getBle_sensor_id()), jsonArray, new IBooleanListener() {
                @Override
                public void callBack(boolean result) {
                    if (isFromMemory && result) {
                        MethodHelper.count=0;
                        //DatabaseClient.getInstance(mCtx).updateSensorData(entitySensor);
                    }
                }
            });
        }*/
    }

    public void stopService(Context context) {
        try {
            Intent stopIntent = new Intent(context, MyService.class);
            stopIntent.setAction(BluetoothConstants.ACTION.STOP_ACTION);
            context.startService(stopIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void onStartJobIntentService(Context context, String sensorId, EntitySensor entitySensor, boolean isFromMemory, int frequency) {
        Intent mIntent = new Intent(context, MyService.class);
        mIntent.setAction(BluetoothConstants.ACTION.START_ACTION);
        mIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        mIntent.putExtra("sensorId", sensorId);
        mIntent.putExtra("isFromMemory", isFromMemory);
        mIntent.putExtra("entitySensor", entitySensor);
        mIntent.putExtra("frequency", frequency);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(mIntent);
        } else {
            context.startService(mIntent);
        }
    }

    public void saveSensorListInLocalDB(Context context, List<Sensor> sensorList, IBooleanListener iBooleanListener) {
        DatabaseClient dbClient = DatabaseClient.getInstance(context);
        List<EntitySensor> entitySensorList = MethodHelper.generateEntitySensor(context, sensorList);
        dbClient.deleteSensorTable();
        dbClient.saveEntitySensorList(entitySensorList, new SensorDatabaseCallback() {
            @Override
            public void onSensorAdded() {
                iBooleanListener.callBack(true);
            }

            @Override
            public void onSensorAddedFailed() {
                iBooleanListener.callBack(false);
            }
        });
    }

    // sensor temperature data
    public void getSensorTemperature(int sensor_id, ISensorTempCallback iSensorTempCallback) {
        DatabaseClient.getInstance(mCtx).getSensorTemp(sensor_id, new softwise.mechatronics.truBlueMonitor.database.ISensorTempCallback() {
            @Override
            public void loadTemperature(List<SensorTempTime> sensorTempTimeList) {
                iSensorTempCallback.onTempLoad(sensorTempTimeList);
            }
        });
    }
}
