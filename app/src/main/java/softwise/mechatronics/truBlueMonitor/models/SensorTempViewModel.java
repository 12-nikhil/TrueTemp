package softwise.mechatronics.truBlueMonitor.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import softwise.mechatronics.truBlueMonitor.database.DatabaseClient;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ISensorDataCallback;

import java.util.List;

public class SensorTempViewModel extends AndroidViewModel {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private boolean viewModelSetup = false;
    private final MutableLiveData<List<EntitySensor>> sensorLiveData = new MutableLiveData<List<EntitySensor>>();

    public SensorTempViewModel(@NonNull Application application) {
        super(application);
    }

    // Called in the activity's onCreate(). Checks if it has been called before, and if not, sets up the data.
    // Returns true if everything went okay, or false if there was an error and therefore the activity should finish.
    public boolean setupViewModel(Context context) {
        // Check we haven't already been called
        if (!viewModelSetup) {
            this.mContext = context;
            viewModelSetup = true;
        }
        getSensorDataFromLocalDB();
        // If we got this far, nothing went wrong, so return true
        return true;
    }

    public LiveData<List<EntitySensor>> fetchSensorData() {
        return sensorLiveData;
    }

    private void getSensorDataFromLocalDB() {
        try {
            new DatabaseClient(mContext).getAllSensor(new ISensorDataCallback() {
                @Override
                public void onLoadAllSensor(List<EntitySensor> entitySensors) {
                    Log.e("Size temp value", String.valueOf(entitySensors.size()));
                    sensorLiveData.postValue(entitySensors);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCleared() {
        Log.e("Model disconnect ", "call");
        super.onCleared();
    }

}
