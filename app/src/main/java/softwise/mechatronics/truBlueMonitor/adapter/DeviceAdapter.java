package softwise.mechatronics.truBlueMonitor.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.viewHolders.DeviceViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
    private BluetoothDevice[] deviceList = new BluetoothDevice[0];
    private final OnDeviceSelectListeners mOnDeviceSelectListeners;

    public DeviceAdapter(OnDeviceSelectListeners onDeviceSelectListeners) {
        this.mOnDeviceSelectListeners = onDeviceSelectListeners;
    }

    @NotNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull DeviceViewHolder holder, int position) {
        holder.setupView(deviceList[position]);
        holder.layout.setOnClickListener(v -> {
            mOnDeviceSelectListeners.deviceSelect(deviceList[position]);
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.length;
    }

    public void updateList(Collection<BluetoothDevice> deviceList) {
        this.deviceList = deviceList.toArray(new BluetoothDevice[0]);
        notifyDataSetChanged();
    }

    public interface OnDeviceSelectListeners {
        void deviceSelect(BluetoothDevice bluetoothDevice);
    }
}
