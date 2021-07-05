package softwise.mechatronics.truBlueMonitor.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.softwise.trumonitor.R;
import com.softwise.trumonitor.databinding.ActivitySensorGraphBinding;
import com.softwise.trumonitor.databinding.ActivitySensorGraphNewBinding;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import softwise.mechatronics.truBlueMonitor.database.EntitySensor;
import softwise.mechatronics.truBlueMonitor.database.SensorTempTime;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ISensorTempCallback;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.helper.ServerDatabaseHelper;

public class SensorGraphNewActivity extends AppCompatActivity {

    public String normal = "Normal";
    public String alarm = "Alarm";
    ActivitySensorGraphNewBinding mBinding;
    ArrayList<Entry> normalTemp;
    ArrayList<Entry> alarmTemp;
    XAxis xl;
    //YAxis yL;
    private LineChart lineChart;
    private ProgressBar prLoad;
    private TextView txtEmptyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySensorGraphNewBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        mBinding.txtSensorName.setText("Graph");
        setContentView(view);
        lineChart = mBinding.lineChart;
        prLoad = mBinding.prbLoad;
        txtEmptyData= mBinding.txtEmptyData;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Graph");
        initializeLineGraph();
        getSensorData();
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

    private void loadLineGraph(List<SensorTempTime> sensorTempTimes) {
        ArrayList<String> xAXES = new ArrayList<>();
        ArrayList<Entry> yAXESsin = new ArrayList<>();
        ArrayList<Entry> yAXEScos = new ArrayList<>();
        String[] xAxisArray = new String[sensorTempTimes.size()];
        String[] dateTimeFormatArray = new String[sensorTempTimes.size()];

        int count = 0;
        for (SensorTempTime sensorTempTime : sensorTempTimes) {
            String compareDate = null;
            if (sensorTempTime.getTime() != null) {
                String[] dateTimeArray = sensorTempTime.getTime().split(" ");
                compareDate = dateTimeArray[0];
            }
            float dateTime = Float.parseFloat(new DecimalFormat("##.##").format(MethodHelper.getNotedTimeLong(sensorTempTime.getTime())));
            Log.e("Date time ", String.valueOf(dateTime));
            float temp = Math.round(sensorTempTime.getTemp_value());
            if ("alarm_low".equals(sensorTempTime.getStatus()) || "alarm_high".equals(sensorTempTime.getStatus())) {
                yAXEScos.add(new Entry(dateTime, temp));
                dateTimeFormatArray[count] = sensorTempTimes.get(count).getTime();
            } else {
                yAXESsin.add(new Entry(dateTime, temp));
                dateTimeFormatArray[count] = sensorTempTimes.get(count).getTime();
            }
            xAXES.add(count, String.valueOf(temp));
            xAxisArray[count] = String.valueOf(temp);
            count++;
        }
        if(dateTimeFormatArray.length>0) {
            // Controlling X axis
            XAxis xAxis = lineChart.getXAxis();
            // Set the xAxis position to bottom. Default is top
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            //xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
          //  xAxis.setValueFormatter(new DateAxisValueFormatter(dateTimeFormatArray));
            ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

            LineDataSet lineDataSet1 = new LineDataSet(yAXEScos, "Normal");
            lineDataSet1.setDrawCircles(true);
            lineDataSet1.setColor(Color.BLUE);

            LineDataSet lineDataSet2 = new LineDataSet(yAXESsin, "sin");
            lineDataSet2.setDrawCircles(true);
            lineDataSet2.setColor(Color.RED);

            lineDataSets.add(lineDataSet1);
            lineDataSets.add(lineDataSet2);
            LineData data = new LineData(lineDataSets);
            lineChart.setData(data);
            lineChart.setVisibility(View.VISIBLE);
            prLoad.setVisibility(View.GONE);
        }else {
            lineChart.setVisibility(View.GONE);
            prLoad.setVisibility(View.GONE);
        }

        /*lineChart.setData(new LineData(xaxes, lineDataSets));

        lineChart.setVisibleXRangeMaximum(65f);*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

class DateAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private ArrayList<String> mValues;

    public DateAxisValueFormatter(ArrayList<String> values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value) {
        return sdf.format(new Date((long) value));
    }
}