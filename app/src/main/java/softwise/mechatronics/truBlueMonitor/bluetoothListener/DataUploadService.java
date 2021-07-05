package softwise.mechatronics.truBlueMonitor.bluetoothListener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.google.gson.JsonArray;
import com.softwise.trumonitor.R;

import softwise.mechatronics.truBlueMonitor.database.DatabaseClient;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import softwise.mechatronics.truBlueMonitor.database.SensorTempTime;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.implementer.SensorPresenter;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanListener;

import java.util.List;

import softwise.mechatronics.truBlueMonitor.database.ISensorTempCallback;
import softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants;

public class DataUploadService extends JobIntentService {
    private static final String TAG = "MyJobIntentService";
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 1;
    Context mContext;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, DataUploadService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        mContext = this;
        checkDataIsPendingForUpload(intent);
    }

    private void checkDataIsPendingForUpload(Intent intent) {
        DatabaseClient.getInstance(mContext).getAllSensorTemp(new ISensorTempCallback() {
            @Override
            public void loadTemperature(List<SensorTempTime> sensorTempTimeList) {
                if (sensorTempTimeList != null && sensorTempTimeList.size() > 0) {
                    // check time completed 24 hour
                    // upload that data on server
                    final int[] count = {0};
                    for (SensorTempTime st : sensorTempTimeList) {
                        int hour = MethodHelper.getTimeDifference(st.getTime());
                        if (st.isFlag() && hour >= 24) {
                            // delete data from local
                            DatabaseClient.getInstance(mContext).deleteSensorTempId(st.getId());
                        } else if (!st.isFlag() || st.isUploadPending()) {
                            Log.e("Pending upload ",st.getTime()+" "+st.getSensor_id());
                            EntitySensor entitySensor = MethodHelper.createSingleEntitySensor(getApplicationContext(), st.getSensor_id(),
                                    String.valueOf(st.getTemp_value()), st.getUnit(), st.getTime(), st.getStatus(), st.getAssets_id());
                            JsonArray jsonArray = MethodHelper.getJsonArray(getApplicationContext(), entitySensor, false, true);
                            new SensorPresenter(getApplicationContext()).uploadData(String.valueOf(st.getSensor_id()), jsonArray, new IBooleanListener() {
                                @Override
                                public void callBack(boolean result) {
                                    count[0] = count[0] + 1;
                                    DatabaseClient.getInstance(mContext).deleteSensorTempId(st.getId());
                                    if (count[0] == sensorTempTimeList.size()) {
                                        stopService(intent);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    stopSelf();
                }
            }
        });
    }
}
