package softwise.mechatronics.truBlueMonitor.viewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.softwise.trumonitor.R;

public class SensorTesterViewHolder extends RecyclerView.ViewHolder {

    public TextView txtSensorTemp;

    public SensorTesterViewHolder(View view) {
        super(view);
        txtSensorTemp = view.findViewById(R.id.list_item_text1);
    }

    public void setupView(String sensorData) {
        this.txtSensorTemp.setText(sensorData);
    }
}
