package softwise.mechatronics.truBlueMonitor.database.dbListeners;


import softwise.mechatronics.truBlueMonitor.database.EntitySensor;

import java.util.List;

public interface ISensorDataCallback {
    void onLoadAllSensor(List<EntitySensor> entitySensors);
    //void onLoadSensor(EntitySensor entitySensors);
}
