package softwise.mechatronics.truBlueMonitor.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.implementer.SensorPresenter;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanListener;
import softwise.mechatronics.truBlueMonitor.models.Sensor;

import java.util.Objects;

public class DialogSensorLevel extends DialogFragment {

    int alarmLow, alarmHigh, warningLow, warningHigh;
    private Context mContext;
    private Sensor mEntitySensor;
    private OnAddSensorLevelListeners mOnAddSensorLevelListeners;
    private EditText edtAlarmLow, edtAlarmHigh, edtWarningLow, edtWarningHigh;
    private EditText edtHour1, edtHour2, edtMin1, edtMin2, edtSec1, edtSec2;
    private TextView txtSave, txtCancel;
    private TextView txtAlarmLow, txtAlarmHigh, txtWarningLow, txtWarningHigh, txtAlarmProgress, txtWarningProgress;
    private SeekBar seekbarAlarm, seekbarWarning;
    private TextView txtSensorId;
    private Button btnAlarmSet, btnWarningSet;

    public DialogSensorLevel(Context context, Sensor entitySensor, OnAddSensorLevelListeners onAddSensorLevelListeners) {
        this.mContext = context;
        this.mEntitySensor = entitySensor;
        this.mOnAddSensorLevelListeners = onAddSensorLevelListeners;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MY_DIALOG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_sensor_level_unit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Add Sensor Data");
        initView(view);
        clickListeners();
    }

    private void initView(View view) {
        txtSensorId = view.findViewById(R.id.txt_sensor_id);
        edtAlarmLow = view.findViewById(R.id.edt_alarm_low);
        edtAlarmHigh = view.findViewById(R.id.edt_alarm_high);
        edtWarningLow = view.findViewById(R.id.edt_warning_low);
        edtWarningHigh = view.findViewById(R.id.edt_warning_high);

        txtAlarmLow = view.findViewById(R.id.txt_alarm_low);
        txtAlarmHigh = view.findViewById(R.id.txt_alarm_high);
        txtWarningLow = view.findViewById(R.id.txt_warning_low);
        txtWarningHigh = view.findViewById(R.id.txt_warning_high);
        edtHour1 = view.findViewById(R.id.edt_hour1);
        edtHour2 = view.findViewById(R.id.edt_hour2);
        edtMin1 = view.findViewById(R.id.edt_min1);
        edtMin2 = view.findViewById(R.id.edt_min2);
        edtSec1 = view.findViewById(R.id.edt_sec1);
        edtSec2 = view.findViewById(R.id.edt_sec2);
        txtSave = view.findViewById(R.id.txt_save);
        txtCancel = view.findViewById(R.id.txt_cancel);
        seekbarAlarm = view.findViewById(R.id.seekBar_alarm);
        seekbarWarning = view.findViewById(R.id.seekBar_warning);
        btnAlarmSet = view.findViewById(R.id.btn_alarm_set);
        btnWarningSet = view.findViewById(R.id.btn_warning_set);
        txtAlarmProgress = view.findViewById(R.id.txt_alarm_progress);
        txtWarningProgress = view.findViewById(R.id.txt_warning_progress);
        txtSensorId.setText(mEntitySensor.getSensor_name());
    }

    private void clickListeners() {
        seekbarAlarm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 50) {
                    txtAlarmProgress.setText(String.valueOf(progress - 50));
                } else {
                    txtAlarmProgress.setText(String.valueOf(50 - progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbarWarning.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 50) {
                    txtWarningProgress.setText(String.valueOf(progress - 50));
                } else {
                    txtWarningProgress.setText(String.valueOf(50 - progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        btnAlarmSet.setOnClickListener(v -> {
            int progress = Integer.parseInt(txtAlarmProgress.getText().toString());
            if (seekbarAlarm.getProgress() > 50) {
                alarmHigh = progress;
                txtAlarmHigh.setText("High : " + progress);
            } else {
                alarmLow = progress;
                txtAlarmLow.setText("Low : " + progress);
            }
            txtAlarmProgress.setText("0");
            seekbarAlarm.setProgress(50);
        });
        btnWarningSet.setOnClickListener(v -> {
            int progress = Integer.parseInt(txtWarningProgress.getText().toString());
            if (seekbarWarning.getProgress() > 50) {
                warningHigh = progress;
                txtWarningHigh.setText("High : " + progress);
            } else {
                warningLow = progress;
                txtWarningLow.setText("Low : " + progress);
            }
            txtWarningProgress.setText("0");
            seekbarWarning.setProgress(50);
        });
        txtSave.setOnClickListener(v -> {
            if (alarmLow < warningLow && alarmLow < warningHigh && alarmLow < alarmHigh) {
                if (warningLow < warningHigh && warningLow < alarmHigh) {
                    saveData();
                }
                else {
                    MethodHelper.showToast(getContext(), "Warning levels should be less than Alarm levels");
                }
            } else {
                MethodHelper.showToast(getContext(), "Alarm/Warning condition does not match");
            }
        });
        txtCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void saveData() {
        mEntitySensor.setAlarmLow(alarmLow);
        mEntitySensor.setAlarmHigh(alarmHigh);
        mEntitySensor.setWarningLow(warningLow);
        mEntitySensor.setWarningHigh(warningHigh);
        String hour1 = edtHour1.getText().toString().trim();
        String hour2 = edtHour2.getText().toString().trim();
        String min1 = edtMin1.getText().toString().trim();
        String min2 = edtMin2.getText().toString().trim();
        String sec1 = edtSec1.getText().toString().trim();
        String sec2 = edtSec2.getText().toString().trim();

        String updateFrequency = hour1 + hour2 + ":" + min1 + min2 + ":" + sec1 + sec2;
        mEntitySensor.setUpdateFrequency(updateFrequency);
        new SensorPresenter(mContext).saveUpdateSensorLevelToServer(mContext, mEntitySensor, new IBooleanListener() {
            @Override
            public void callBack(boolean result) {
                mOnAddSensorLevelListeners.onAddSensorLevel(mEntitySensor);
                dismiss();
            }
        });
    }

    public interface OnAddSensorLevelListeners {
        void onAddSensorLevel(Sensor entitySensor);
    }
}
