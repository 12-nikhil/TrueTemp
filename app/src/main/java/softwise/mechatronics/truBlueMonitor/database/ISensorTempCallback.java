package softwise.mechatronics.truBlueMonitor.database;

import java.util.List;

public interface ISensorTempCallback {
    void loadTemperature(List<SensorTempTime> sensorTempTimeList);
}
