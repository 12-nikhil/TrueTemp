package softwise.mechatronics.truBlueMonitor.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class gpsloc {
    @PrimaryKey(autoGenerate = true)
    private int gpsloc_id;
    @ColumnInfo(name = "user_id")
    private int user_id;
    @ColumnInfo(name = "latitude")
    private String latitude;
    @ColumnInfo(name = "longitude")
    private String longitude;

    public int getGpsloc_id() {
        return gpsloc_id;
    }

    public void setGpsloc_id(int gpsloc_id) {
        this.gpsloc_id = gpsloc_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
