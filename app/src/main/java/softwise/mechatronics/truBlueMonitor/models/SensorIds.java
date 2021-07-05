package softwise.mechatronics.truBlueMonitor.models;

public class SensorIds {
    private int ble_asset_id;
    private int[] sensor_ids;

    public SensorIds() {

    }

    public int[] getSensor_ids() {
        return sensor_ids;
    }

    public void setSensor_ids(int[] sensor_ids) {
        this.sensor_ids = sensor_ids;
    }

    public SensorIds(int asset,int[] ids)
    {
        this.ble_asset_id = asset;
        this.sensor_ids = ids;
    }

    public int getBle_asset_id() {
        return ble_asset_id;
    }

    public void setBle_asset_id(int ble_asset_id) {
        this.ble_asset_id = ble_asset_id;
    }
}
