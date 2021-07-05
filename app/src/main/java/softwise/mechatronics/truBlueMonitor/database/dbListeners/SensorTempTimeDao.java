package softwise.mechatronics.truBlueMonitor.database.dbListeners;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import softwise.mechatronics.truBlueMonitor.database.SensorTempTime;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

//DAOs define all methods to access database, annotated with @Dao annotation.
// The DAO acts as a contract to perform CRUD operations on data within a database.
@Dao
public interface SensorTempTimeDao {

     //Insert the object in database @param sensorTempTime, object to be inserted
    @Insert
    void insert(SensorTempTime sensorTempTime);

    //get all sensorTemp data
    @Query("SELECT * FROM sensortemptime")
    Maybe<List<SensorTempTime>> getAllSensorTemp();

    //get list of sensorTemp data by sensor id
    @Query("SELECT * FROM sensortemptime where ble_sensor_id=:sensor_id")
    Flowable<List<SensorTempTime>> getSensorTempById(int sensor_id);

    //get single sensorTemp data by sensor id
    @Query("SELECT * FROM sensortemptime where ble_sensor_id=:sensor_id")
    Flowable<SensorTempTime> getSingleSensorTempById(int sensor_id);

    //get list of sensorTemp data by sensor flag
    /* it means data which is not uploaded due to network connection failed*/
    @Query("SELECT * FROM sensortemptime where flag=:mFlag")
    Flowable<List<SensorTempTime>> getSingleSensorTempByFlag(boolean mFlag);

    @Query("DELETE FROM sensortemptime WHERE id = :id")
    void deleteSensorTempById(int id);
}
