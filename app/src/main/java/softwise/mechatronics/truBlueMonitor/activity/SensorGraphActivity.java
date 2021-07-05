package softwise.mechatronics.truBlueMonitor.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.softwise.trumonitor.R;

import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import softwise.mechatronics.truBlueMonitor.database.SensorTempTime;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ISensorTempCallback;

import com.softwise.trumonitor.databinding.ActivitySensorGraphBinding;
import com.softwise.trumonitor.databinding.ActivitySensorGraphNewBinding;

import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.helper.ServerDatabaseHelper;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SensorGraphActivity extends AppCompatActivity {

    ActivitySensorGraphBinding mBinding;
    private LineChart lineChart;
    private TextView txtEmptyData;
    private EntitySensor entitySensor = new EntitySensor();
    public String normal = "Normal";
    public String alarm = "Alarm";

    ArrayList<Entry> normalTemp;
    ArrayList<Entry> alarmTemp;
    XAxis xl;
    //YAxis yL;
    private ProgressBar prLoad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySensorGraphBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        if (getIntent().getExtras() != null) {
            entitySensor = getIntent().getParcelableExtra("sensor");
            mBinding.txtSensorName.setText(entitySensor.getSensor_name());
            setContentView(view);
            lineChart = mBinding.lineChart;
            txtEmptyData = mBinding.txtEmptyData;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            setTitle(entitySensor.getSensor_name());
            initializeLineGraph();
            getSensorData();
            //getSensorData();
        } else
            finish();
    }

    private void initializeLineGraph()
    {
        normalTemp = new ArrayList<Entry>();
        alarmTemp = new ArrayList<Entry>();
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.getXAxis().setTextSize(15f);
        lineChart.getAxisLeft().setTextSize(15f);
//        lineChart.setMarkerView(mv);
        xl = lineChart.getXAxis();
        // yL = lineChart.getAxisLeft();
        xl.setAvoidFirstLastClipping(true);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setInverted(true);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend l = lineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }

    private void getSensorData() {
        ArrayList<Entry> entries = new ArrayList<>();
        final String[][] dateTimeArray = {null};
        //int sensorId = entitySensor.getBle_sensor_id();
        //int sensorId = 1;
        new ServerDatabaseHelper(getApplicationContext()).getSensorTemperature(1, new ISensorTempCallback() {
            @Override
            public void onTempLoad(List<SensorTempTime> sensorTempTimes) {
                if (sensorTempTimes != null && sensorTempTimes.size() > 0) {
                    dateTimeArray[0] = new String[sensorTempTimes.size()];
                    // loadLineGraph(sensorTempTimes);
                    loadData(sensorTempTimes);
                } else {
                    lineChart.setVisibility(View.GONE);
                    //txtEmptyData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadData(List<SensorTempTime> sensorTempTimes) {
        ArrayList<String> dateTimeFormatedArray = new ArrayList<>();
        for (int i = 0; i < sensorTempTimes.size(); i++) {
            SensorTempTime sensorTempTime = sensorTempTimes.get(i);
            String status = sensorTempTime.getStatus();
            float dateTime = Float.parseFloat(new DecimalFormat("##.##").format(MethodHelper.getNotedTimeLong(sensorTempTime.getTime())));
            String compareDate = null;
            if (sensorTempTime.getTime() != null) {
                String[] dateTimeArray = sensorTempTime.getTime().split(" ");
                compareDate = dateTimeArray[0];
            }
            //if (MethodHelper.getDate(System.currentTimeMillis()).equals(compareDate)) {
            if ("alarm_low".equals(status) || "alarm_high".equals(status)) {
                alarmTemp.add(new Entry(dateTime, sensorTempTime.getTemp_value()));
                Log.e("Alarm Time and Temp", MethodHelper.getHourMin(sensorTempTime.getTime()) + " " + sensorTempTime.getTemp_value());
                dateTimeFormatedArray.add(sensorTempTimes.get(i).getTime());
            } else {
                normalTemp.add(new Entry(dateTime,sensorTempTime.getTemp_value()));
                Log.e("Normal Time and Temp", MethodHelper.getHourMin(sensorTempTime.getTime()) + " " + sensorTempTime.getTemp_value());
                dateTimeFormatedArray.add(sensorTempTimes.get(i).getTime());
            }
            //}
        }

        xl.setValueFormatter(new DateAxisValueFormatter(dateTimeFormatedArray));
        // Set the xAxis position to bottom. Default is top
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        LineDataSet normalSet = new LineDataSet(normalTemp, normal);
        normalSet.setLineWidth(1.5f);
        normalSet.setCircleRadius(4f);
        normalSet.setColor(Color.parseColor("#3949AB"));
        normalSet.setCircleColor(Color.parseColor("#536DFE"));
        LineDataSet alarmSet = new LineDataSet(alarmTemp, alarm);
        alarmSet.setLineWidth(1.5f);
        alarmSet.setCircleRadius(4f);
        alarmSet.setColor(Color.parseColor("#F44336"));
        alarmSet.setCircleColor(Color.parseColor("#FF8A80"));
        lineDataSets.add(normalSet);
        lineDataSets.add(alarmSet);
        LineData data = new LineData(lineDataSets);
        lineChart.setData(data);
        lineChart.invalidate();
        prLoad.setVisibility(View.GONE);
    }


   /* private void getSensorData() {
        ArrayList<Entry> entries = new ArrayList<>();
        final String[][] dateTimeArray = {null};
        int sensorId = entitySensor.getBle_sensor_id();
       //int sensorId = 1;
        new ServerDatabaseHelper(getApplicationContext()).getSensorTemperature(sensorId, new ISensorTempCallback() {
            @Override
            public void onTempLoad(List<SensorTempTime> sensorTempTimes) {
                if(sensorTempTimes!=null && sensorTempTimes.size()>0) {
                    dateTimeArray[0] = new String[sensorTempTimes.size()];
                    for (SensorTempTime sensorTempTime : sensorTempTimes) {
                        String compareDate = null;
                        if (sensorTempTime.getTime() != null) {
                            String[] dateTimeArray = sensorTempTime.getTime().split(" ");
                            compareDate = dateTimeArray[0];
                        }
                        if (MethodHelper.getDate(System.currentTimeMillis()).equals(compareDate)) {
                            float dateTime = Float.parseFloat(new DecimalFormat("##.##").format(MethodHelper.getNotedTimeLong(sensorTempTime.getTime())));
                            float temp = Math.round(sensorTempTime.getTemp_value());
                            entries.add(new Entry(dateTime, temp));
                        }
                        //entries.add(new Entry(dateTime, Float.parseFloat(new DecimalFormat("##.##").format(sensorTempTime.getTemp_value()))));
                    }
                    loadGraph(entries,sensorTempTimes);
                }else {
                    lineChart.setVisibility(View.GONE);
                    txtEmptyData.setVisibility(View.VISIBLE);
                }
            }
        });
    }*/

   /* private void loadGraph(ArrayList<Entry> entries,List<SensorTempTime> sensorTempTimes)
    {
        if (entries.size() > 0) {
            lineChart.setVisibility(View.VISIBLE);
            txtEmptyData.setVisibility(View.GONE);
            Collections.sort(entries, new EntryXComparator()); // if negative array index occur this line overcome it ---->imp
            // LineDataSet thisSet = new LineDataSet(entries, "");

            LineDataSet dataSet = new LineDataSet(entries, "Temperature");
            //  LineDataSet dataSet1 = new LineDataSet(entries1, "Customized values");
            dataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            // dataSet1.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            //  dataSet1.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            //****
            // Controlling X axis
            XAxis xAxis = lineChart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            class DateAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
                private String[] mValues;

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

                public DateAxisValueFormatter(String[] values) {
                    this.mValues = values;
                }

                @Override
                public String getFormattedValue(float value) {
                    return sdf.format(new Date((long) value));
                }
            }
            String[] dateTime = new String[sensorTempTimes.size()];
            for (int i = 0; i < sensorTempTimes.size(); i++) {
                dateTime[i] = sensorTempTimes.get(i).getTime();
            }
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(new DateAxisValueFormatter(dateTime));
            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setEnabled(false);
            YAxis yAxisLeft = lineChart.getAxisLeft();
            yAxisLeft.setGranularity(1f);
            LineData data = new LineData(dataSet);
            lineChart.setData(data);
            lineChart.animateX(3500);

                  *//*  // limit the number of visible entries
                    lineChart.setVisibleXRangeMaximum(15);

                    // move to the latest entry
                    lineChart.moveViewToX(data.getEntryCount());*//*
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}