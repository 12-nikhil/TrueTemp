package softwise.mechatronics.truBlueMonitor.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;


public class BluetoothConnectionReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer =null;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        // raised alarm
                        raisedAlarm(context);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer.reset();
                        }
                        break;
                }
        }
    }

    private void raisedAlarm(Context context) {
        try {
            if(SPTrueTemp.getLoginStatus(context)) {
                mediaPlayer = new MediaPlayer();
                AssetFileDescriptor afd = context.getAssets().openFd("bluetooth_disconnect.mp3");
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
