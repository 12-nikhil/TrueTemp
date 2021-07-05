package softwise.mechatronics.truBlueMonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.RecyclerView;


import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import softwise.mechatronics.truBlueMonitor.viewHolders.SensorViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SensorsAdapter extends RecyclerView.Adapter<SensorViewHolder> {
    private final Context mContext;
    private final OnSensorTempSelectListeners mOnSensorSelectListeners;
    //private List<Sensor> mSensorList = new ArrayList<>();
    private List<EntitySensor> mSensorList = new ArrayList<>();
    private int mSensorId;
    private String mTemp, mUnit;

    public SensorsAdapter(Context context, List<EntitySensor> sensorList, OnSensorTempSelectListeners onSensorSelectListeners) {
        this.mSensorList = sensorList;
        this.mContext = context;
        this.mOnSensorSelectListeners = onSensorSelectListeners;
    }

    @NotNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new SensorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sensor_unit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NotNull SensorViewHolder holder, int position) {
        EntitySensor sensor = mSensorList.get(position);
        holder.txtSensor.setText(sensor.getSensor_name());
        holder.txtAlarmLow.setText(String.valueOf(sensor.getAlarm_low()));
        holder.txtAlarmHigh.setText(String.valueOf(sensor.getAlarm_high()));
        Animation animation = null;
        if(sensor.getBle_sensor_id()==mSensorId)
        {
            sensor.setTemp_value(mTemp);
            sensor.setUnit(mUnit);
        }
        if(sensor.getTemp_value()!=null && sensor.getTemp_value().length()>0) {
            double temperature = Double.parseDouble(sensor.getTemp_value());
            String value = Math.round(temperature) + "\u00B0" + sensor.getUnit();
            holder.txtTemperature.setText(value);
            // check temperature status like low high
            if ("alarm_low".equals(sensor.getStatus()) || "alarm_high".equals(sensor.getStatus()))
            {
                animation = AnimationUtils.loadAnimation(mContext,
                        R.anim.blink);
                //this.txtTemperature.startAnimation(animation);
                holder.txtStatus.setText("Temperature not in range");
                holder.txtStatus.setVisibility(View.VISIBLE);
            }
            else {
                holder.txtStatus.setVisibility(View.GONE);
                if(animation!=null)
                {
                    holder.txtTemperature.clearAnimation();
                    animation.cancel();
                }
            }
        }
        holder.linParent.setOnClickListener(v -> {
            mOnSensorSelectListeners.onSensorSelect(sensor);
        });
       // holder.setupView(mContext, entitySensor, mSensorId, mTemp, mUnit, mOnSensorSelectListeners);
    }

    @Override
    public int getItemCount() {
        return mSensorList.size();
    }

    public void updateList(List<EntitySensor> sensorList) {
        this.mSensorList.clear();
        this.mSensorList.addAll(sensorList);
        notifyDataSetChanged();
    }

    public void updateEntitySensor(EntitySensor sensor) {
        if(mSensorList.size()>0)
        {
            for(EntitySensor entitySensor:mSensorList)
            {
                if(entitySensor.getBle_sensor_id()==sensor.getBle_sensor_id())
                {
                    int pos = mSensorList.indexOf(entitySensor);
                    mSensorList.remove(pos);
                    mSensorList.add(sensor);
                   // entitySensor.setTemp_value(sensor.getTemp_value());
                   // entitySensor.setUnit(sensor.getUnit());
                }
            }
        }else {
            mSensorList.add(sensor);
        }
        notifyDataSetChanged();
    }

    public void updateTemp(int sensorId, String temp, String unit) {
        this.mSensorId = sensorId;
        this.mTemp = temp;
        this.mUnit = unit;
        notifyDataSetChanged();
    }

    public interface OnSensorTempSelectListeners {
        void onSensorSelect(EntitySensor entitySensor);
    }
}
