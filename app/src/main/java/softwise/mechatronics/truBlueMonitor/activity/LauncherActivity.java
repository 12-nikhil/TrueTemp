package softwise.mechatronics.truBlueMonitor.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.softwise.trumonitor.R;
import softwise.mechatronics.truBlueMonitor.helper.DialogHelper;
import softwise.mechatronics.truBlueMonitor.helper.HelperUtils;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;
import com.tbruyelle.rxpermissions2.RxPermissions;


public class LauncherActivity extends AppCompatActivity {

    private RxPermissions rxPermissions;
    int count = 0;
    private int PERMISSION_ALL_REQ_CODE = 201;
    private String[] PERMISSIONS_NEEDED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setTitle("");
        PERMISSIONS_NEEDED = HelperUtils.getManifestPermissions(this);

        if (HelperUtils.hasPermissions(this, PERMISSIONS_NEEDED)) {
            handleDirection();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_NEEDED, PERMISSION_ALL_REQ_CODE);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        super.onResume();

       /* rxPermissions = new RxPermissions(this);
        rxPermissions.requestEachCombined(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE).subscribe(permission -> {
            if(permission.granted) {
                Log.e("LauncherActivity","Call");
                MethodHelper.getDeviceNumberList(getApplicationContext());
                handleDirection();
            }
            else {
               showDialog();
            }
        });*/
       /* rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE).subscribe(aBoolean -> {
            if(aBoolean) {
                Log.e("LauncherActivity","Call");
                MethodHelper.getDeviceNumberList(getApplicationContext());
                handleDirection();
            }
            else {
                showDialog();
            }
        });*/
    }

    private void handleDirection()
    {
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SPTrueTemp.getLoginStatus(getApplicationContext())) {
                    startActivity(new Intent(LauncherActivity.this, PairedDeviceActivity.class));
                   // startActivity(new Intent(LauncherActivity.this, SensorGraphNewActivity.class));
                } else {
                    startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, getResources().getInteger(R.integer.splashscreen_duration));
    }

    private void showDialog()
    {
        DialogHelper.showMessageDialog(this,"Warning",getString(R.string.msg_permission_not_granted));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL_REQ_CODE && (grantResults.length > 0) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleDirection();
        } else {
            //MethodHelper.showToast(getApplicationContext(), getString(R.string.permission_not_granted_msg));
            showDialog();
            finishAffinity();
        }
    }
}