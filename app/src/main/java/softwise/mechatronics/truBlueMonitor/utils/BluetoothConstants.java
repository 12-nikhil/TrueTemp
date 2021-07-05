package softwise.mechatronics.truBlueMonitor.utils;


import rx.android.BuildConfig;

public class BluetoothConstants {
    static String releaseUrl = "http://157.245.96.191";
    //static String debugUrl = "http://nodetest.trumonitor.tech";

    static String debugUrl = "http://128.199.26.79:8080";
    static String debugUrlForUploadTemp = "http://128.199.26.79:8888";
    //public static final String BASE_URL = BuildConfig.DEBUG ? debugUrl : releaseUrl;
    public static final String BASE_URL = debugUrl;
    public static final String BASE_URL_UPLOAD = debugUrlForUploadTemp;
    // values have to be globally unique
    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    public  static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    public static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".DeviceConnectionActivity";
    public static final String CONTENT_TYPE = "application/json";
    public static final String BEARER = "Bearer";

    // values have to be unique within each app
    public  static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    // pass value key constant
    public  static final String SENSOR_ID = "sensor_id";
    public  static final String SENSOR_NAME = "ble_sensor";
    public  static final String ALARM_UPDATE_FREQUENCY = "00:01:00";

    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 8466503;

    public static class ACTION {
        public static final String MAIN_ACTION = "test.action.main";
        public static final String START_ACTION = "test.action.start";
        public static final String STOP_ACTION = "test.action.stop";
    }

    public static class STATE_SERVICE {
        public static final int CONNECTED = 10;
        public static final int NOT_CONNECTED = 0;
    }

}
