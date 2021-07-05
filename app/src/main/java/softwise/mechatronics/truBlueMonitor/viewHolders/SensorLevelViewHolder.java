package softwise.mechatronics.truBlueMonitor.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.softwise.trumonitor.R;


public class SensorLevelViewHolder extends RecyclerView.ViewHolder {

    public TextView txtAlarmLow;
    public TextView txtAlarmHigh;
    public TextView txtWarningLow;
    public TextView txtWarningHigh;
    public TextView txtFrequency;
    public TextView txtSensor;
    public CardView cardParent;
    public ImageView imgEdit;

    public SensorLevelViewHolder(View view) {
        super(view);
        txtSensor = view.findViewById(R.id.txt_sensor_id);
        txtAlarmLow = view.findViewById(R.id.txt_alarm_low);
        txtAlarmHigh = view.findViewById(R.id.txt_alarm_high);
        txtWarningLow = view.findViewById(R.id.txt_warning_low);
        txtWarningHigh = view.findViewById(R.id.txt_warning_high);
        txtFrequency = view.findViewById(R.id.txt_update_frequency);
        cardParent = view.findViewById(R.id.card_parent);
        imgEdit = view.findViewById(R.id.img_edit_sensor);
    }
}
