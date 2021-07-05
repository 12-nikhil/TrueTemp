package softwise.mechatronics.truBlueMonitor.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntitySensor implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "ble_sensor_id")
    private  int ble_sensor_id;
    @ColumnInfo(name = "ble_asset_id")
    private  int asset_id;
    @ColumnInfo(name = "sensor_name")
    private String sensor_name;
    @ColumnInfo(name = "assets_name")
    private String assets_name;
    @ColumnInfo(name = "temp_value")
    private String temp_value;
    @ColumnInfo(name = "unit")
    private String unit;
    @ColumnInfo(name = "time")
    private String time;
    @ColumnInfo(name = "status")
    private String status;
    @ColumnInfo(name = "fre")
    private String update_frequency;
    @ColumnInfo(name = "lat")
    private String lat;
    @ColumnInfo(name = "lng")
    private String lng;
    @ColumnInfo(name = "flag")
    private boolean flag;
    @ColumnInfo(name = "aLow")
    private int alarm_low;
    @ColumnInfo(name = "aHigh")
    private int alarm_high;
    @ColumnInfo(name = "wLow")
    private int warning_low;
    @ColumnInfo(name = "wHigh")
    private int warning_high;


    protected EntitySensor(Parcel in) {
        id = in.readInt();
        ble_sensor_id = in.readInt();
        asset_id = in.readInt();
        sensor_name = in.readString();
        assets_name = in.readString();
        temp_value = in.readString();
        unit = in.readString();
        time = in.readString();
        status = in.readString();
        update_frequency = in.readString();
        lat = in.readString();
        lng = in.readString();
        flag = in.readByte() != 0;
        alarm_low = in.readInt();
        alarm_high = in.readInt();
        warning_low = in.readInt();
        warning_high = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(ble_sensor_id);
        dest.writeInt(asset_id);
        dest.writeString(sensor_name);
        dest.writeString(assets_name);
        dest.writeString(temp_value);
        dest.writeString(unit);
        dest.writeString(time);
        dest.writeString(status);
        dest.writeString(update_frequency);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeByte((byte) (flag ? 1 : 0));
        dest.writeInt(alarm_low);
        dest.writeInt(alarm_high);
        dest.writeInt(warning_low);
        dest.writeInt(warning_high);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EntitySensor> CREATOR = new Creator<EntitySensor>() {
        @Override
        public EntitySensor createFromParcel(Parcel in) {
            return new EntitySensor(in);
        }

        @Override
        public EntitySensor[] newArray(int size) {
            return new EntitySensor[size];
        }
    };

    public int getBle_sensor_id() {
        return ble_sensor_id;
    }

    public void setBle_sensor_id(int ble_sensor_id) {
        this.ble_sensor_id = ble_sensor_id;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }

    public String getTemp_value() {
        return temp_value;
    }

    public void setTemp_value(String temp_value) {
        this.temp_value = temp_value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EntitySensor() {
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUpdate_frequency() {
        return update_frequency;
    }

    public void setUpdate_frequency(String update_frequency) {
        this.update_frequency = update_frequency;
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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getAlarm_low() {
        return alarm_low;
    }

    public void setAlarm_low(int alarm_low) {
        this.alarm_low = alarm_low;
    }

    public int getAlarm_high() {
        return alarm_high;
    }

    public void setAlarm_high(int alarm_high) {
        this.alarm_high = alarm_high;
    }

    public int getWarning_low() {
        return warning_low;
    }

    public void setWarning_low(int warning_low) {
        this.warning_low = warning_low;
    }

    public int getWarning_high() {
        return warning_high;
    }

    public void setWarning_high(int warning_high) {
        this.warning_high = warning_high;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAssets_name() {
        return assets_name;
    }

    public void setAssets_name(String assets_name) {
        this.assets_name = assets_name;
    }

    public int getAsset_id() {
        return asset_id;
    }

    public void setAsset_id(int asset_id) {
        this.asset_id = asset_id;
    }
}
