package softwise.mechatronics.truBlueMonitor.models;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PairedDevViewModel extends AndroidViewModel {

    private BluetoothAdapter mBluetoothAdapter;

    // The paired devices list tha the activity sees
    private MutableLiveData<Collection<BluetoothDevice>> pairedDeviceList = new MutableLiveData<>();

    // A variable to help us not setup twice
    private boolean viewModelSetup = false;

    // Called by the system, this is just a constructor that matches AndroidViewModel.
    public PairedDevViewModel(@NotNull Application application) {
        super(application);
    }

    // Called in the activity's onCreate(). Checks if it has been called before, and if not, sets up the data.
    // Returns true if everything went okay, or false if there was an error and therefore the activity should finish.
    public boolean setupViewModel() {
        // Check we haven't already been called
        if (!viewModelSetup) {
            viewModelSetup = true;
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        // If we got this far, nothing went wrong, so return true
        return true;
    }

    // Called by the activity to request that we refresh the list of paired devices
    public void refreshPairedDevices() {
        pairedDeviceList.postValue(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
    }

    // Called when the activity finishes - clear up after ourselves.
    @Override
    protected void onCleared() {
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.cancelDiscovery();
    }

    // Getter method for the activity to use.
    public LiveData<Collection<BluetoothDevice>> getPairedDeviceList() {
        List<BluetoothDevice> deviceList = new ArrayList<>();
        if(mBluetoothAdapter != null) {
            for (BluetoothDevice device : mBluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
                if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE && device.getName().contains("ESP32test")) {
                    deviceList.add(device);
                }
                pairedDeviceList.setValue(deviceList);
            }
        }
        return pairedDeviceList;
    }
}