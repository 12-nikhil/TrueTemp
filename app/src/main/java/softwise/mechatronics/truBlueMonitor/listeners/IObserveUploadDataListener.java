package softwise.mechatronics.truBlueMonitor.listeners;

import softwise.mechatronics.truBlueMonitor.models.AssetAndSensorInfo;

import java.util.List;

public interface IObserveUploadDataListener {
    void dataOnComplete();
    void errorException(Throwable ex);
    void nextDataLoad(List<AssetAndSensorInfo> data);
}
