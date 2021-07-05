package softwise.mechatronics.truBlueMonitor.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.softwise.trumonitor.R;

import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import softwise.mechatronics.truBlueMonitor.database.SensorTempTime;
import softwise.mechatronics.truBlueMonitor.models.AssetAndSensorInfo;
import softwise.mechatronics.truBlueMonitor.models.LoginResponse;
import softwise.mechatronics.truBlueMonitor.models.Sensor;
import softwise.mechatronics.truBlueMonitor.models.SensorIds;
import softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants;
import softwise.mechatronics.truBlueMonitor.utils.ConnectionUtils;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MethodHelper {

    public static int count = 0;
    private static MediaPlayer mediaPlayer;
    private static AssetFileDescriptor afd;

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void jumpActivity(Context context, Class<? extends Activity> secondActivity) {
        Intent intent = new Intent(context, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void saveUserDataInSP(Context context, LoginResponse loginResponse) {
        SPTrueTemp.saveToken(context, loginResponse.getAccessToken());
        SPTrueTemp.saveUserId(context, loginResponse.getUser().getOperatorId());
        SPTrueTemp.saveUserMobile(context, loginResponse.getUser().getMobile());
        SPTrueTemp.saveUserEmail(context, loginResponse.getUser().getEmail());
        SPTrueTemp.saveUserLevel(context, loginResponse.getUser().getLevel());
        SPTrueTemp.saveUserOrg(context, loginResponse.getUser().getOrgId());
    }

    public static String createSensorDataString(List<Sensor> sensorList, int assetsId) {
        StringBuilder sensorData = new StringBuilder();
        for (Sensor sensor : sensorList) {
            Integer frequencyInSec = MethodHelper.convertStringTimeToSec(sensor.getUpdateFrequency());
            sensorData
                    .append(sensor.getAlarmLow()).append(",")
                    .append(sensor.getAlarmHigh()).append(",")
                    .append(sensor.getWarningLow()).append(",")
                    .append(sensor.getWarningHigh()).append(",")
                    .append(frequencyInSec).append(",")
                    .append(assetsId).append(",");
        }
        sensorData.append("alarm/warning level:");
        // sensorData.deleteCharAt(sensorData.toString().length() - 1);
        return sensorData.toString();
    }

    //  public static String checkAlarmWaningValue(Integer tempValue, Sensor sensor)
    public static String checkAlarmWaningValue(float tempValue, EntitySensor sensor) {
        String status = null;
        if (tempValue <= sensor.getAlarm_low()) {
            status = "alarm_low";
        }
        if (sensor.getAlarm_low() < tempValue && tempValue <= sensor.getWarning_low()) {
            status = "warning_low";
        }
        if (sensor.getWarning_low() < tempValue && tempValue < sensor.getWarning_high()) {
            status = "safe";
        }
        if (sensor.getWarning_high() <= tempValue && tempValue < sensor.getAlarm_high()) {
            status = "warning_high";
        }
        if (tempValue >= sensor.getAlarm_high()) {
            status = "alarm_high";
        }
        return status;
    }

    public static String sensorStatusVale(String statusCode) {
        String value = null;
        if (statusCode != null) {
            switch (statusCode) {
                case "AL":
                    value = "alarm_low";
                    break;
                case "AH":
                    value = "alarm_high";
                    break;
                case "WL":
                    value = "warning_low";
                    break;
                case "WH":
                    value = "warning_high";
                    break;
                case "SF":
                    value = "safe";
                    break;
                default:
                    value = statusCode;
                    break;
            }
        }
        return value;
    }

    public static JsonArray getJsonArray(Context context, EntitySensor entitySensor, boolean isFromMemory, boolean isHourComplete) {
        JsonArray ja = null;
        try {
            JsonObject jo = new JsonObject();
            jo.addProperty("type", "T");
            jo.addProperty("value", entitySensor.getTemp_value());
            jo.addProperty("unit", entitySensor.getUnit());
            if (isFromMemory) {
                jo.addProperty("time", changeDateFormat(entitySensor.getTime()));
            } else if (isHourComplete) {
                jo.addProperty("time", entitySensor.getTime());
            } else {
                jo.addProperty("time", changeDateFormat(entitySensor.getTime()));
            }
            jo.addProperty("status", entitySensor.getStatus());
            jo.addProperty("ble_asset_id", entitySensor.getAsset_id());
            jo.addProperty("ble_sensor_id", entitySensor.getBle_sensor_id());
            // fields lat ,lng,mobile number
            jo.addProperty("latitude", entitySensor.getLat());
            jo.addProperty("longitude", entitySensor.getLng());
            jo.addProperty("mobile", SPTrueTemp.getUserMobile(context));
            ja = new JsonArray();
            ja.add(jo);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ja;
        }
        return ja;
    }

    public static String changeDateFormat(String memorySaveDate) {
        // 15/3/2021,18:51:43 - dd/mm/yyyy,HH:mm:ss
        String formatedDate = null;
        Log.e("Memory save date", memorySaveDate);
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = null;
            date = dt.parse(memorySaveDate);
// 2021-03-21 18:51:43 - yyyy-mm-dd HH:mm:ss
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatedDate = dt1.format(Objects.requireNonNull(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatedDate;
    }

    public static long getNotedTimeLong(String noteTime) {
        long convertedTime = 0;
        try {
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = dateFormat.parse(noteTime);
            convertedTime = d.getTime();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return convertedTime;
    }

    public static String getHourMin(String memorySaveDate) {
        String hourMin = null;
        Log.e("Memory save date", memorySaveDate);
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            date = dt.parse(memorySaveDate);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat("HH:mm");
            hourMin = dt1.format(Objects.requireNonNull(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hourMin;
    }

    public static boolean getUpdateTime(Context context, String frequency) {
        if (frequency != null) {
            int updateFrequency = convertStringTimeToSec(frequency);
            if (count == updateFrequency) {
                count = 0;
                return true;
            }
           /* if (count == 0 || count == updateFrequency) {
                if (count == 0) {
                    count++;
                } else {
                    count = 0;
                }
                return true;
            }*/
            count++;
            Log.e("Count ", String.valueOf(count) + " frequency " + updateFrequency);
        }
        return false;
    }

    public static String getTimeInString(long milliSec) {
        // @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(milliSec);
    }

    public static String getTimeInStringComma(long milliSec) {
        // @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss");
        return df.format(milliSec);
    }

    public static String getDate(long milliSec) {
        // @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(milliSec);
    }

    public static int convertStringTimeToSec(String frequency) {
        int seconds = 0;
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date reference = dateFormat.parse("00:00:00");
            Date date = dateFormat.parse(frequency);
            seconds = (int) ((date.getTime() - reference.getTime()) / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return seconds;
    }

    public static List<EntitySensor> generateEntitySensor(Context context, List<Sensor> sensorList) {
        List<EntitySensor> entitySensorList = new ArrayList<>();
        for (Sensor sensor : sensorList) {
            EntitySensor entitySensor = new EntitySensor();
            entitySensor.setBle_sensor_id(sensor.getSensorId());
            entitySensor.setSensor_name(sensor.getSensor_name());
            entitySensor.setAlarm_low(sensor.getAlarmLow());
            entitySensor.setAlarm_high(sensor.getAlarmHigh());
            entitySensor.setWarning_low(sensor.getWarningLow());
            entitySensor.setWarning_high(sensor.getWarningHigh());
            entitySensor.setUpdate_frequency(sensor.getUpdateFrequency());
            boolean flag = false;
            if (ConnectionUtils.getConnectivityStatusString(context)) {
                flag = true;
            }
            entitySensor.setFlag(flag);
            entitySensorList.add(entitySensor);
        }
        return entitySensorList;
    }

    public static EntitySensor getSingleEntitySensor(Context context, Sensor sensor) {
        EntitySensor entitySensor = new EntitySensor();
        entitySensor.setBle_sensor_id(sensor.getSensorId());
        entitySensor.setSensor_name(sensor.getSensor_name());
        entitySensor.setAlarm_low(sensor.getAlarmLow());
        entitySensor.setAlarm_high(sensor.getAlarmHigh());
        entitySensor.setWarning_low(sensor.getWarningLow());
        entitySensor.setWarning_high(sensor.getWarningHigh());
        entitySensor.setUpdate_frequency(sensor.getUpdateFrequency());
        boolean flag = false;
        if (ConnectionUtils.getConnectivityStatusString(context)) {
            flag = true;
        }
        entitySensor.setFlag(flag);
        return entitySensor;
    }

    public static EntitySensor createSingleEntitySensor(Context context, int sensorId, String temp, String unit, String time, String statusCode, int assetId) {
        EntitySensor entitySensor = new EntitySensor();
        entitySensor.setBle_sensor_id(sensorId);
        entitySensor.setSensor_name(BluetoothConstants.SENSOR_NAME + sensorId);
        entitySensor.setTemp_value(temp);
        entitySensor.setUnit(unit);
        entitySensor.setTime(time);
        entitySensor.setStatus(sensorStatusVale(statusCode));
        entitySensor.setAsset_id(assetId);
        boolean flag = false;
        if (ConnectionUtils.getConnectivityStatusString(context)) {
            flag = true;
        }
        entitySensor.setFlag(flag);
        return entitySensor;
    }


    public static SensorTempTime createSensorTempTime(EntitySensor entitySensor, String lat, String lng, boolean isFromMemory) {
        SensorTempTime sensorTempTime = new SensorTempTime();
        sensorTempTime.setSensor_id(entitySensor.getBle_sensor_id());
        sensorTempTime.setTemp_value(Float.parseFloat(entitySensor.getTemp_value()));
        sensorTempTime.setUnit(entitySensor.getUnit());
        sensorTempTime.setTime(getTimeInString(System.currentTimeMillis()));
        sensorTempTime.setTempFromMemory(isFromMemory);
        sensorTempTime.setLat(lat);
        sensorTempTime.setLng(lng);
        sensorTempTime.setStatus(entitySensor.getStatus());
        return sensorTempTime;
    }

    public static int getTimeDifference(String sensorFetchTime) {
        int hours = 0;
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date sensorDate = dateFormat.parse(sensorFetchTime.replace(",", " ").trim());
            Date currentTime = new Date();//your time
            long diff = currentTime.getTime() - sensorDate.getTime();
            long diffHours = diff / (60 * 60 * 1000);
            long diffMinutes = diff / (60 * 1000) % 60;
            hours = (int) diffHours;
            //Log.e("Complete hours", String.valueOf(hours));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hours;
    }

    public static List<String> getDeviceNumberList(Context context) {
        List<String> numberList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            List<SubscriptionInfo> subscription = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
            for (int i = 0; i < subscription.size(); i++) {
                SubscriptionInfo info = subscription.get(i);
                if (info.getNumber() != null) {
                    numberList.add(info.getNumber());
                }
            }
            if (numberList.size() == 0) {
                TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                @SuppressLint("HardwareIds") String mPhoneNumber = tMgr.getLine1Number();
                numberList.add(mPhoneNumber);
            }
        }
        return numberList;
    }

    public static void playAssetSound(Context context, int type) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd("lost.mp3");
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mediaPlayer != null) {
                        playAssetSound(context, type);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            //Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void stopAlarm() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = null;
        }
    }

    public static String checkRoundFunction(double temp) {
        BigDecimal b = new BigDecimal(temp);
        b = b.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Log.e("Temperature in round", String.valueOf(b));
        return String.valueOf(b);
    }

    public static List<AssetAndSensorInfo> getAssetsFromArray(Context context) {
        List<AssetAndSensorInfo> mAssetsList = new ArrayList<>();
        String[] assetsArray = context.getResources().getStringArray(R.array.assets_array);
        for (int i = 0; i < assetsArray.length; i++) {
            AssetAndSensorInfo assetAndSensorInfo = new AssetAndSensorInfo();
            assetAndSensorInfo.setAssetId(i);
            assetAndSensorInfo.setAssetName(assetsArray[i]);
            mAssetsList.add(assetAndSensorInfo);
        }
        return mAssetsList;

    }

    public static SensorIds setOrCreateEntitySensorList(int assetId, String sensorId) {
        // List<EntitySensor> entitySensorList = new ArrayList<>();
        SensorIds senId = new SensorIds();

        if (sensorId.length() > 0 && sensorId.contains("]")) {
            String[] sensorArray = sensorId.split("sensor_id:");
            String sId = sensorArray[1].trim();
            JSONArray jsonArray = null;
            int[] outArr = new int[0];
            try {
                jsonArray = (JSONArray) new JSONObject(new JSONTokener("{data:" + sId + "}")).get("data");
                outArr = new int[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    //EntitySensor entitySensor = new EntitySensor();
                    if (jsonArray.get(i) != null && !jsonArray.get(i).equals("null")) {
                        outArr[i] = jsonArray.getInt(i);
                        /*entitySensor.setSensor_id(outArr[i]);
                        entitySensor.setSensor_name(BluetoothConstants.SENSOR_NAME+ outArr[i]);
                        entitySensorList.add(entitySensor);*/
                    }
                    senId = new SensorIds(assetId, outArr);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return senId;
        // return entitySensorList;
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    // Use like this:
    // boolean foregroud = new ForegroundCheckTask().execute(context).get();
}

