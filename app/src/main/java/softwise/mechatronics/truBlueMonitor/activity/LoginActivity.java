package softwise.mechatronics.truBlueMonitor.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.softwise.trumonitor.R;
import com.softwise.trumonitor.databinding.ActivityLoginBinding;
import softwise.mechatronics.truBlueMonitor.helper.DialogHelper;
import softwise.mechatronics.truBlueMonitor.helper.MethodHelper;
import softwise.mechatronics.truBlueMonitor.models.LoginResponse;
import softwise.mechatronics.truBlueMonitor.models.User;
import softwise.mechatronics.truBlueMonitor.models.UserCredentials;
import softwise.mechatronics.truBlueMonitor.serverUtils.ApiClients;
import softwise.mechatronics.truBlueMonitor.serverUtils.ServiceListeners.APIService;
import softwise.mechatronics.truBlueMonitor.utils.ConnectionUtils;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants.CONTENT_TYPE;

/**
 *
 */
public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        clickListeners();
        // setContentView(R.layout.activity_login);
    }

    private void clickListeners() {
        binding.emailSignInButton.setOnClickListener(v -> {
            if (isFormValid()) {
                String email = binding.adtEmail.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();
                login(email, password);
            }
            //openActivity();
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean isFormValid() {
        String email = binding.adtEmail.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        if (!ConnectionUtils.getConnectivityStatusString(this)) {
            MethodHelper.showToast(this, getString(R.string.msg_no_internet));
            return false;
        }
        if (email.isEmpty() || !(Patterns.EMAIL_ADDRESS.matcher(email)).matches()) {
            binding.ilEmail.setError(getString(R.string.error_invalid_email));
            requestFocus(binding.adtEmail);
            return false;
        }
        if (password.isEmpty()) {
            binding.ilPassword.setError(getString(R.string.error_invalid_password));
            requestFocus(binding.edtPassword);
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        view.requestFocus();
    }

    private void login(String email, String password) {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setEmail(email);
        userCredentials.setPassword(password);

        APIService apiService = ApiClients.getRetrofitInstance(false).create(APIService.class);

        Observable<LoginResponse> observable = apiService.login(CONTENT_TYPE,userCredentials).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        // Set up progress before call
        DialogHelper.showProgressDialog(LoginActivity.this, "Please wait....");
        observable.subscribe(new Observer<LoginResponse>() {
            @Override
            public void onCompleted() {
                DialogHelper.dismissProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                //  Log.e("Error server",e.printStackTrace());
                MethodHelper.showToast(LoginActivity.this, getString(R.string.msg_check_server_connection));
                DialogHelper.dismissProgressDialog();
            }

            @Override
            public void onNext(LoginResponse loginResponse) {
                DialogHelper.dismissProgressDialog();
                User status = loginResponse.getUser();
                Log.e("Login response ",status.toString());
                if (status != null) {
                    SPTrueTemp.saveLoginStatus(getApplicationContext(), true);
                    /* Compare register number and device SIM numbers
                     * if same the continue with login
                     * if not then show message and exit from the app */
                    // if (MethodHelper.checkRegisterMobileNumber(getApplicationContext(),loginResponse)) {
                    MethodHelper.showToast(LoginActivity.this, getString(R.string.msg_login_success));
                    //if login is successful data write into sharedPref
                    MethodHelper.saveUserDataInSP(LoginActivity.this, loginResponse);
                    //First check the role of user and according to that go to pages
                    openActivity();
                    /*} else {
                        DialogHelper.messageDialog(LoginActivity.this, getString(R.string.msg_registered_mobile_number_not_same), result -> {
                            if (result) {
                                // logout from app
                                // clear shared preference
                                MethodHelper.logoutApp(getApplicationContext(), new IBooleanListener() {
                                    @Override
                                    public void callBack(boolean result) {
                                        jumpToSplashActivity();
                                    }
                                });

                            }
                        });
                    }*/
                } else {
                    MethodHelper.showToast(LoginActivity.this, getString(R.string.msg_login_failed));
                }
            }
        });
    }

    private void openActivity() {
        // clear shared preference of device name and address
        SPTrueTemp.clearConnectedAddress(getApplicationContext());
        MethodHelper.jumpActivity(LoginActivity.this, PairedDeviceActivity.class);
        //MethodHelper.jumpActivity(LoginActivity.this, SensorGraphNewActivity.class);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}