package softwise.mechatronics.truBlueMonitor.bluetoothListener;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.JsonArray;
import com.softwise.trumonitor.R;

import softwise.mechatronics.truBlueMonitor.activity.SensorTemperatureActivity;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.implementer.SensorPresenter;

public class MyService extends Service {

    private Handler h;
    private Runnable r;

    int counter = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification updateNotification() {
        counter++;
        String info = counter + "";

        Context context = getApplicationContext();

        PendingIntent action = PendingIntent.getActivity(context,
                0, new Intent(context, SensorTemperatureActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT); // Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            String CHANNEL_ID = "blue_true_monitor";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "BlueTruMonitorChannel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Bluetooth TruMonitor channel description");
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }
        else
        {
            builder = new NotificationCompat.Builder(context);
        }
        return builder.setContentIntent(action)
                .setContentTitle("BluetoothTruMonitor")
                .setTicker("Temperature recording is on")
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentIntent(action)
                .setOngoing(true).build();

       /* return builder.setContentIntent(action)
                .setContentTitle("BluetoothTruMonitor")
                .setTicker(info)
                .setContentText(info)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentIntent(action)
                .setOngoing(true).build();*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().contains("start")) {
            h = new Handler();
            r = new Runnable() {
                @Override
                public void run() {
                    startForeground(101, updateNotification());
                    int frequnecy = intent.getIntExtra("frequency",0);
                    //if(MethodHelper.count==frequnecy) {
                        startTempUpload(intent);
                    //}
                   // h.postDelayed(this, 1000);
                }
            };

            h.post(r);
        } else {
            h.removeCallbacks(r);
            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }
    public void startTempUpload(Intent intent) {
        int sensorId = Integer.parseInt(intent.getStringExtra("sensorId"));
        boolean isFromMemory = intent.getBooleanExtra("isFromMemory", false);
        EntitySensor entitySensor = (EntitySensor) intent.getExtras().get("entitySensor");
        JsonArray jsonArray = MethodHelper.getJsonArray(this, entitySensor, isFromMemory, false);
        new SensorPresenter(getApplicationContext()).uploadData(String.valueOf(sensorId), jsonArray, result -> {
            if (result) {
                stopSelf();
            }
        });
    }
}
