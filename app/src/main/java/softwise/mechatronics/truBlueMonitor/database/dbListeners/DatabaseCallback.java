package softwise.mechatronics.truBlueMonitor.database.dbListeners;

public interface DatabaseCallback {

    void onSensorDeleted();

    void onSensorAdded();

    void onSensorUpdated();

    void onDataNotAvailable();
}
