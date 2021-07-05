package softwise.mechatronics.truBlueMonitor.serverUtils;

import android.util.Log;

import softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClients {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(boolean isUploadCall) {
        String baseURL = null;
        if(isUploadCall)
        {
            baseURL = BluetoothConstants.BASE_URL_UPLOAD;
        }else {
            baseURL = BluetoothConstants.BASE_URL;
        }
        if (retrofit == null) {
            Log.e("Base url" ,baseURL);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }else if(!isUploadCall){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getRetrofitInstanceForUpload(boolean isUploadCall) {
        retrofit = null;
        String baseURL =  BluetoothConstants.BASE_URL_UPLOAD;;
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
