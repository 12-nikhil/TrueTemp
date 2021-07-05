package softwise.mechatronics.truBlueMonitor.database.dbListeners;

public interface SensorDatabaseCallback {

    void onSensorAdded();

    void onSensorAddedFailed();
}
