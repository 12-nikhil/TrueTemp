package softwise.mechatronics.truBlueMonitor.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssetAndSensorInfo {

    @SerializedName("ble_asset_id")
    @Expose
    private Integer assetId;
    @SerializedName("assetName")
    @Expose
    private String assetName;
    @SerializedName("sensors")
    @Expose
    private List<Sensor> sensors = null;

    public Integer getAssetId() {
        return assetId;
    }

    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
}
