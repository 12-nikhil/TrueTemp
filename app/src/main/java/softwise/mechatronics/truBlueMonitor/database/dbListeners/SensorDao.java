package softwise.mechatronics.truBlueMonitor.database.dbListeners;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import softwise.mechatronics.truBlueMonitor.database.EntitySensor;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

//DAOs define all methods to access database, annotated with @Dao annotation.
// The DAO acts as a contract to perform CRUD operations on data within a database.
@Dao
public interface SensorDao {

     //Insert the object in database @param sensor, object to be inserted
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EntitySensor EntitySensor);

    @Insert
    Completable insertSensorData(List<EntitySensor> sensors);

    @Update
        //update the object in database @param sensor, object to be inserted
    void update(EntitySensor EntitySensor);
    //Delete the object in database @param sensor, object to e deleted
    @Delete
    void delete(EntitySensor EntitySensor);

    @Query("DELETE FROM entitysensor WHERE id = :id")
    void deleteById(int id);

    //get all sensor data
    @Query("SELECT * FROM entitysensor")
    Flowable<List<EntitySensor>> getAllSensor();

    //get all sensor data
    @Query("SELECT * FROM entitysensor where flag=:flagResult")
    Flowable<List<EntitySensor>> getAllSensorData(boolean flagResult);

    //get sensor data
    @Query("SELECT * FROM entitysensor where ble_sensor_id=:sensorId")
    Flowable<EntitySensor> getSensorById(int sensorId);

    /**
     * Updating only temp,unit
     * By sensor id
     */
    @Query("UPDATE entitysensor SET temp_value=:temp,unit=:unit WHERE ble_sensor_id = :id")
    void updateTemp(String temp,String unit,int id);

    /**
     * Updating only flag
     * By sensor id
     */
    @Query("UPDATE entitysensor SET flag=:flag WHERE ble_sensor_id = :id")
    void updateNetworkFlag(boolean flag, int id);

    @Query("SELECT * FROM entitysensor WHERE ble_sensor_id = :id")
    int isDataExist(int id);

    @Query("DELETE FROM entitysensor")
    public void deleteSensorTable();

    @Query("SELECT * FROM entitysensor where flag=:mFlag")
    Flowable<EntitySensor> getSensorByFlag(boolean mFlag);
}
