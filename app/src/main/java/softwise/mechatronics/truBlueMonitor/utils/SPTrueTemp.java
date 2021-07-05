package softwise.mechatronics.truBlueMonitor.utils;

import android.content.Context;

import softwise.mechatronics.truBlueMonitor.helper.HelperPreference;

public class SPTrueTemp {

    public static void saveFirstTimeLaunch(Context context, Boolean status)
    {
        HelperPreference.saveBoolean(context,"first_launch",status);
    }
    public static boolean getIsFirstTimeLaunch(Context context)
    {
        return HelperPreference.getBoolean(context,"first_launch");
    }
    public static void saveSensorId(Context context,String sensor_ids)
    {
        HelperPreference.saveString(context,"sensor_id",sensor_ids);
    }
    public static String getSensorId(Context context)
    {
        return HelperPreference.getString(context,"sensor_id");
    }
    public static void saveLoginStatus(Context context,Boolean status)
    {
        HelperPreference.saveBoolean(context,"Login_status",status);
    }
    public static boolean getLoginStatus(Context context)
    {
        return HelperPreference.getBoolean(context,"Login_status");
    }
    public static void saveConnectedMacAddress(Context context,String macAddress)
    {
        HelperPreference.saveString(context,"MAC",macAddress);
    }
    public static String getConnectedMacAddress(Context context)
    {
        return HelperPreference.getString(context,"MAC");
    }
    public static void saveConnectedBluetoothName(Context context,String bluetoothName)
    {
        HelperPreference.saveString(context,"b_name",bluetoothName);
    }
    public static String getConnectedBluetooth(Context context)
    {
        return HelperPreference.getString(context,"b_name");
    }
    public static void saveUserId(Context context,String userId)
    {
        HelperPreference.saveString(context,"user_id",userId);
    }
    public static String getUserId(Context context) {
        return HelperPreference.getString(context, "user_id");
    }

   /* public static void saveUserId(Context context,int userId)
    {
        HelperPreference.saveInt(context,"user_id",userId);
    }
    public static int getUserId(Context context) {
        return HelperPreference.getInteger(context, "user_id");
    }*/

    public static void saveUserEmail(Context context,String userEmail)
    {
        HelperPreference.saveString(context,"user_email",userEmail);
    }
    public static String getUserEmail(Context context) {
        return HelperPreference.getString(context, "user_email");
    }
    public static void saveUserMobile(Context context,String userMobile)
    {
        HelperPreference.saveString(context,"user_mobile",userMobile);
    }
    public static String getUserMobile(Context context) {
        return HelperPreference.getString(context, "user_mobile");
    }
    public static void saveUserLevel(Context context,String userLevel)
    {
        HelperPreference.saveString(context,"user_level",userLevel);
    }
    public static String getUserLevel(Context context) {
        return HelperPreference.getString(context, "user_level");
    }
    public static void saveUserOrg(Context context,int org)
    {
        HelperPreference.saveInt(context,"org",org);
    }
    public static Integer getUserOrg(Context context) {
        return HelperPreference.getInteger(context, "org");
    }
    public static void saveToken(Context context,String token)
    {
        HelperPreference.saveString(context,"token",token);
    }
    public static String getToken(Context context) {
        return HelperPreference.getString(context, "token");
    }
    public static void saveLatitude(Context context,String lat)
    {
        HelperPreference.saveString(context,"lat",lat);
    }
    public static String getLatitude(Context context) {
        return HelperPreference.getString(context, "lat");
    }
    public static void saveLongitude(Context context,String lng)
    {
        HelperPreference.saveString(context,"lng",lng);
    }
    public static String getLongitude(Context context) {
        return HelperPreference.getString(context, "lng");
    }

    public static void saveBatteryLevel(Context context,String batteryLevel)
    {
        HelperPreference.saveString(context,"battery",batteryLevel);
    }
    public static String getBatteryLevel(Context context) {
        return HelperPreference.getString(context, "battery");
    }
    public static void saveCallingType(Context context,String calledFrom)
    {
        HelperPreference.saveString(context,"callFrom",calledFrom);
    }
    public static String getCallingType(Context context) {
        return HelperPreference.getString(context, "callFrom");
    }
    /*  public static void saveLongitude(Context context,String lng)
      {
          HelperPreference.saveString(context,"lng",lng);
      }
      public static String getLongitude(Context context) {
          return HelperPreference.getString(context, "lng");
      }*/
    public static void clearSharedPref(Context context)
    {
        saveLoginStatus(context,false);
        //saveUserId(context,0);
        saveUserId(context,null);
        saveUserMobile(context,null);
        saveUserLevel(context,null);
        saveUserEmail(context,null);
        saveUserOrg(context,0);
        saveConnectedMacAddress(context,null);
        saveConnectedBluetoothName(context,null);
        saveToken(context,null);
        saveLatitude(context,null);
        saveLongitude(context,null);
        saveBatteryLevel(context,null);
    }

    public static void clearConnectedAddress(Context context)
    {
        //saveConnectedMacAddress(context,null);
        //saveConnectedBluetoothName(context,null);
        saveCallingType(context,null);
        saveSensorId(context,null);
        saveBatteryLevel(context,null);
        saveLatitude(context,null);
        saveLongitude(context,null);
    }

}
