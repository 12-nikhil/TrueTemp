package softwise.mechatronics.truBlueMonitor.viewHolders;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.softwise.trumonitor.R;


public class DeviceViewHolder extends RecyclerView.ViewHolder {

   public RelativeLayout layout;
   public TextView text1;
   public TextView text2;

    public DeviceViewHolder(View view) {
        super(view);
        layout = view.findViewById(R.id.list_item);
        text1 = view.findViewById(R.id.list_item_text1);
        text2 = view.findViewById(R.id.list_item_text2);
    }

    public void setupView(BluetoothDevice device) {
        text1.setText(device.getName());
        text2.setText(device.getAddress());
    }
}
