package softwise.mechatronics.truBlueMonitor.myapp;

import android.app.Application;
import android.os.StrictMode;

import rx.android.BuildConfig;

public class BluetoothApplication extends Application {

    public BluetoothApplication(){
        if(BuildConfig.DEBUG)
            StrictMode.enableDefaults();
    }
}
