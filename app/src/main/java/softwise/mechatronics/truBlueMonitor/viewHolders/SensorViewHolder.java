package softwise.mechatronics.truBlueMonitor.viewHolders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.softwise.trumonitor.R;


public class SensorViewHolder extends RecyclerView.ViewHolder {

    public TextView txtAlarmLow;
    public TextView txtAlarmHigh;
    public TextView txtTemperature;
    public TextView txtSensor;
    public TextView txtStatus;
    public CardView cardParent;
    public LinearLayout linParent;

    public SensorViewHolder(View view) {
        super(view);
        txtSensor = view.findViewById(R.id.txt_sensor_id);
        txtAlarmLow = view.findViewById(R.id.txt_alarm_low);
        txtAlarmHigh = view.findViewById(R.id.txt_alarm_high);
        txtTemperature = view.findViewById(R.id.txt_temperature);
        cardParent = view.findViewById(R.id.card_parent);
        txtStatus = view.findViewById(R.id.txt_sensor_status);
        linParent = view.findViewById(R.id.lin_parent);
    }

   /* public void setupView(Context context,EntitySensor sensor, int sensorId, String temp, String unit, SensorsAdapter.OnSensorSelectListeners sensorSelectListeners) {
        this.txtSensor.setText(sensor.getSensor_name());
        this.txtAlarmLow.setText(String.valueOf(sensor.getAlarmLow()));
        this.txtAlarmHigh.setText(String.valueOf(sensor.getAlarmHigh()));
        Animation animation = null;
        if(sensor.getSensor_id()==sensorId)
        {
            sensor.setTemp_value(temp);
            sensor.setUnit(unit);
        }
        if(sensor.getTemp_value()!=null && sensor.getTemp_value().length()>0) {
            double temperature = Double.parseDouble(sensor.getTemp_value());
            String value = Math.round(temperature) + "\u00B0" + sensor.getUnit();
            this.txtTemperature.setText(value);
            // check temperature status like low high
            if ("alarm_low".equals(sensor.getStatus()) || "alarm_high".equals(sensor.getStatus()))
            {
                animation = AnimationUtils.loadAnimation(context,
                        R.anim.blink);
                //this.txtTemperature.startAnimation(animation);
                txtStatus.setText("Temperature not in range");
                txtStatus.setVisibility(View.VISIBLE);
            }
            else {
                txtStatus.setVisibility(View.GONE);
                if(animation!=null)
                {
                    this.txtTemperature.clearAnimation();
                    animation.cancel();
                }
            }
        }
        cardParent.setOnClickListener(v -> {
            sensorSelectListeners.onSensorSelect(sensor);
        });

    }*/

}
