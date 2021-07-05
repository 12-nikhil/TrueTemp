package softwise.mechatronics.truBlueMonitor.listeners;


import softwise.mechatronics.truBlueMonitor.models.AssetAndSensorInfo;

public interface IObserveDataListener {
    void dataOnComplete();
    void errorException(Throwable ex);
    void nextDataLoad(AssetAndSensorInfo data);
    /*void nextDataLoad(List<AssetAndSensorInfo> data);*/
}
