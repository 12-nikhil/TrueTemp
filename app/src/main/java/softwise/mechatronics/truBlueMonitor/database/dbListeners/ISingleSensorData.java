package softwise.mechatronics.truBlueMonitor.database.dbListeners;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;

public interface ISingleSensorData {
    void onLoadSensor(EntitySensor entitySensor);
}
