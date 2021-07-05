package softwise.mechatronics.truBlueMonitor.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sensor {
    @SerializedName("ble_sensor_id")
    @Expose
    private Integer sensorId;
    @SerializedName("sensor_name")
    @Expose
    private String sensor_name;
    @SerializedName("alarm_low")
    @Expose
    private Integer alarmLow;
    @SerializedName("warning_low")
    @Expose
    private Integer warningLow;
    @SerializedName("warning_high")
    @Expose
    private Integer warningHigh;
    @SerializedName("alarm_high")
    @Expose
    private Integer alarmHigh;
    @SerializedName("update_frequency")
    @Expose
    private String updateFrequency;

    private String temp;
    private String unit;

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public Integer getAlarmLow() {
        return alarmLow;
    }

    public void setAlarmLow(Integer alarmLow) {
        this.alarmLow = alarmLow;
    }

    public Integer getAlarmHigh() {
        return alarmHigh;
    }

    public void setAlarmHigh(Integer alarmHigh) {
        this.alarmHigh = alarmHigh;
    }

    public Integer getWarningLow() {
        return warningLow;
    }

    public void setWarningLow(Integer warningLow) {
        this.warningLow = warningLow;
    }

    public Integer getWarningHigh() {
        return warningHigh;
    }

    public void setWarningHigh(Integer warningHigh) {
        this.warningHigh = warningHigh;
    }

    public String getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(String updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSensor_name() {
        return sensor_name;
    }

    public void setSensor_name(String sensor_name) {
        this.sensor_name = sensor_name;
    }
}
