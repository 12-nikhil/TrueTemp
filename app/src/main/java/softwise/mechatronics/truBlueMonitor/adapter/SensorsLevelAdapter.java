package softwise.mechatronics.truBlueMonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.models.Sensor;
import softwise.mechatronics.truBlueMonitor.viewHolders.SensorLevelViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SensorsLevelAdapter extends RecyclerView.Adapter<SensorLevelViewHolder> {
    private Context mContext;
    private OnSensorLevelSelectListeners mOnSensorLevelSelectListeners;
    private List<Sensor> mSensorList = new ArrayList<>();

    public SensorsLevelAdapter(Context context, List<Sensor> sensorList, OnSensorLevelSelectListeners onSensorSelectListeners) {
        this.mSensorList = sensorList;
        this.mContext = context;
        this.mOnSensorLevelSelectListeners = onSensorSelectListeners;
    }

    @NotNull
    @Override
    public SensorLevelViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new SensorLevelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sensor_level_unit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull SensorLevelViewHolder holder, int position) {
        Sensor sensor = mSensorList.get(position);
        holder.txtSensor.setText(sensor.getSensor_name());
        holder.txtAlarmLow.setText(String.valueOf(sensor.getAlarmLow()));
        holder.txtAlarmHigh.setText(String.valueOf(sensor.getAlarmHigh()));
        holder.txtWarningLow.setText(String.valueOf(sensor.getWarningLow()));
        holder.txtWarningHigh.setText(String.valueOf(sensor.getWarningHigh()));
        if(sensor.getUpdateFrequency()!=null) {
            holder.txtFrequency.setText(String.valueOf(sensor.getUpdateFrequency())+" s");
        }
        holder.cardParent.setOnClickListener(v -> {
            mOnSensorLevelSelectListeners.onSensorLevelSelect(sensor);
        });
    }

    @Override
    public int getItemCount() {
        return mSensorList.size();
    }


    public interface OnSensorLevelSelectListeners {
        void onSensorLevelSelect(Sensor sensor);
    }
}
