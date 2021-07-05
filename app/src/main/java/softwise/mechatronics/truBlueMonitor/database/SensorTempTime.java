package softwise.mechatronics.truBlueMonitor.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SensorTempTime {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "ble_sensor_id")
    private  int sensor_id;
    @ColumnInfo(name = "ble_assets_id")
    private  int assets_id;
    @ColumnInfo(name = "assets_name")
    private  String assets_name;
    @ColumnInfo(name = "temp_value")
    private float temp_value;
    @ColumnInfo(name = "unit")
    private String unit;
    @ColumnInfo(name = "time")
    private String time;
    @ColumnInfo(name = "memory")
    private boolean tempFromMemory;
    // flag - to check internet was connected or not
    @ColumnInfo(name = "flag")
    private boolean flag;
    @ColumnInfo(name = "lat")
    private String lat;
    @ColumnInfo(name = "lng")
    private String lng;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "isPending")
    private boolean isUploadPending;
    public SensorTempTime()
    {

    }
    public SensorTempTime(String time, float temp) {
        this.time = time;
        this.temp_value = temp;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(int sensor_id) {
        this.sensor_id = sensor_id;
    }

    public float getTemp_value() {
        return temp_value;
    }

    public void setTemp_value(float temp_value) {
        this.temp_value = temp_value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isTempFromMemory() {
        return tempFromMemory;
    }

    public void setTempFromMemory(boolean tempFromMemory) {
        this.tempFromMemory = tempFromMemory;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAssets_name() {
        return assets_name;
    }

    public void setAssets_name(String assets_name) {
        this.assets_name = assets_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAssets_id() {
        return assets_id;
    }

    public void setAssets_id(int assets_id) {
        this.assets_id = assets_id;
    }

    public boolean isUploadPending() {
        return isUploadPending;
    }

    public void setUploadPending(boolean uploadPending) {
        isUploadPending = uploadPending;
    }
}
