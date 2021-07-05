package softwise.mechatronics.truBlueMonitor.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import softwise.mechatronics.truBlueMonitor.database.dbListeners.GpslocDao;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.SensorDao;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.SensorTempTimeDao;


@Database(entities = {EntitySensor.class,SensorTempTime.class,gpsloc.class},version = 2,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SensorDao sensorDao();
    public abstract GpslocDao gpslocDao();
    public abstract SensorTempTimeDao sensorTempTimeDao();
}

