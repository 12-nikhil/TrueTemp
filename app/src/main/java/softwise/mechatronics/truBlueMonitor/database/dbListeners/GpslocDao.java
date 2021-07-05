package softwise.mechatronics.truBlueMonitor.database.dbListeners;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import softwise.mechatronics.truBlueMonitor.database.gpsloc;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface GpslocDao {

    //Insert the object in database @param Gpsloc, object to be inserted
    @Insert
    void insert(gpsloc gpsloc);
    //Update the object in database @param Gpsloc, object to be update
    @Insert
    void update(gpsloc gpsloc);
    //get all Gpsloc data
    @Query("SELECT * FROM gpsloc")
    List<gpsloc> getAllGpsLoc();

    //get Gpsloc data
    @Query("SELECT * FROM gpsloc where user_id =:userId")
    Maybe<gpsloc> getGpsLocByUserId(int userId);
}
