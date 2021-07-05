package softwise.mechatronics.truBlueMonitor.database.dbListeners;


import softwise.mechatronics.truBlueMonitor.database.SensorTempTime;

public interface ISingleSensorTempCallback {
    void onSensorTempLoad(SensorTempTime sensorTempTimes);
}
