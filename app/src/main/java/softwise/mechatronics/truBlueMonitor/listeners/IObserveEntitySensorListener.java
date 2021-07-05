package softwise.mechatronics.truBlueMonitor.listeners;

import softwise.mechatronics.truBlueMonitor.database.EntitySensor;

public interface IObserveEntitySensorListener {
    //void getEntitySensor(int sensorId,String temp,String unit);
    void getEntitySensor(EntitySensor entitySensor);
}
