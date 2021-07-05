package softwise.mechatronics.truBlueMonitor.listeners;

import softwise.mechatronics.truBlueMonitor.models.AssetAndSensorInfo;

public interface IGetAssetsInfoListener {
    void getAssetsSensorList(AssetAndSensorInfo assetAndSensorInfo);
}
