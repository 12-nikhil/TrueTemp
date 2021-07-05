package softwise.mechatronics.truBlueMonitor.database.dbListeners;


import softwise.mechatronics.truBlueMonitor.database.SensorTempTime;

import java.util.List;

public interface ISensorTempCallback {
    void onTempLoad(List<SensorTempTime> sensorTempTimes);
}
