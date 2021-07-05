package softwise.mechatronics.truBlueMonitor.implementer;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import softwise.mechatronics.truBlueMonitor.listeners.IBooleanListener;
import softwise.mechatronics.truBlueMonitor.listeners.IObserveDataListener;
import softwise.mechatronics.truBlueMonitor.models.AssetAndSensorInfo;
import softwise.mechatronics.truBlueMonitor.models.RefreshTokenResponse;
import softwise.mechatronics.truBlueMonitor.models.Sensor;
import softwise.mechatronics.truBlueMonitor.models.SensorIds;
import softwise.mechatronics.truBlueMonitor.serverUtils.ApiClients;
import softwise.mechatronics.truBlueMonitor.serverUtils.ServiceListeners.APIService;
import softwise.mechatronics.truBlueMonitor.utils.SPTrueTemp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants.BEARER;
import static softwise.mechatronics.truBlueMonitor.utils.BluetoothConstants.CONTENT_TYPE;

public class SensorPresenter {
    APIService apiService;
    private IObserveDataListener mIObserveDataListener;

    public SensorPresenter(Context context, IObserveDataListener iObserveDataListener) {
        this.mIObserveDataListener = iObserveDataListener;
        intPresenters(context);
    }
    public SensorPresenter(Context context) {
        intPresenters(context);
    }
    public void saveUpdateSensorLevelToServer(Context context, Sensor sensor, IBooleanListener booleanListener) {
        String org = String.valueOf(SPTrueTemp.getUserOrg(context));
        String userId = String.valueOf(SPTrueTemp.getUserId(context));
        String levels = SPTrueTemp.getUserLevel(context);
        String token = SPTrueTemp.getToken(context);
        Observable<JsonObject> observable = apiService.setUpAssetsData(BEARER+" "+token,CONTENT_TYPE, org, userId, levels, sensor).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<JsonObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if ("HTTP 401 Unauthorized".equals(e.getMessage())) {
                    callRefreshToken(context, null);
                } else {
                    booleanListener.callBack(false);
                }
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                if (jsonObject != null) {
                    booleanListener.callBack(true);
                }
            }
        });
    }

    private void intPresenters(Context context) {
        apiService = ApiClients.getRetrofitInstance(false).create(APIService.class);
    }

    public void sendAssetAndSensorToServer(Context context, SensorIds sensorIds) {

        String token = String.valueOf(SPTrueTemp.getToken(context));
        String org = String.valueOf(SPTrueTemp.getUserOrg(context));
        String userId = String.valueOf(SPTrueTemp.getUserId(context));
        String levels = SPTrueTemp.getUserLevel(context);
        Observable<AssetAndSensorInfo> observable = apiService.getAssetsData(BEARER+" "+token,CONTENT_TYPE, org, userId, levels, sensorIds)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<AssetAndSensorInfo>() {
            @Override
            public void onCompleted() {
                mIObserveDataListener.dataOnComplete();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                mIObserveDataListener.errorException(e);
                /*
                 * if error occur like token expire
                 * then call refresh token first and then again call getAssetsDataFromServer()*/
                if ("HTTP 401 Unauthorized".equals(e.getMessage())) {
                    callRefreshToken(context, sensorIds);
                }
            }

            @Override
            public void onNext(AssetAndSensorInfo assetAndSensorInfoList) {
                mIObserveDataListener.nextDataLoad(assetAndSensorInfoList);
            }
        });
    }

    public void callRefreshToken(Context context, SensorIds sensorIds) {
        String id = SPTrueTemp.getUserId(context);
        apiService.refreshToken(Integer.parseInt(id)).enqueue(new Callback<RefreshTokenResponse>() {
            @Override
            public void onResponse(Call<RefreshTokenResponse> call, Response<RefreshTokenResponse> response) {
                if (response != null) {
                    RefreshTokenResponse refreshTokenResponse = response.body();
                    SPTrueTemp.saveToken(context, refreshTokenResponse.getMessage());
                    if (sensorIds != null) {
                        sendAssetAndSensorToServer(context, sensorIds);
                    }
                }
            }

            @Override
            public void onFailure(Call<RefreshTokenResponse> call, Throwable t) {
            }
        });
    }

    public void uploadData(String sensorId, JsonArray jsonArray, IBooleanListener iBooleanListener) {
        APIService apiService = ApiClients.getRetrofitInstanceForUpload(true).create(APIService.class);
        Observable<JsonObject> observable =  apiService.uploadTempData(CONTENT_TYPE, jsonArray)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<JsonObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                iBooleanListener.callBack(false);
            }

            @Override
            public void onNext(JsonObject s) {
                Log.e("Upload Response ", s.get("message").toString());
                iBooleanListener.callBack(true);
            }
        });
    }

    public void logout(Context context,int assetId, IBooleanListener listener) {
        int userId = Integer.parseInt(SPTrueTemp.getUserId(context));
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ble_asset_id", assetId);
        jsonObject.addProperty("user_id", userId);
        try {
            apiService.refreshToken(userId).enqueue(new Callback<RefreshTokenResponse>() {
                @Override
                public void onResponse(Call<RefreshTokenResponse> call, Response<RefreshTokenResponse> response) {
                    if (response != null) {
                        RefreshTokenResponse refreshTokenResponse = response.body();
                        String token = refreshTokenResponse.getMessage();
                        Observable<JsonObject> observable = apiService.logoutFromServer(BEARER+" "+token,CONTENT_TYPE, jsonObject)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread());
                        observable.subscribe(new Observer<JsonObject>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(JsonObject s) {
                                if ("true".equals(s.get("success").toString())) {
                                    SPTrueTemp.saveToken(context, null);
                                    listener.callBack(true);
                                } else
                                    listener.callBack(false);
                            }
                        });
                    }

                }

                @Override
                public void onFailure(Call<RefreshTokenResponse> call, Throwable t) {
                }
            });
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void deallocateSensorFromAsset(Context context, int assetsId, IBooleanListener listener) {
        String org = String.valueOf(SPTrueTemp.getUserOrg(context));
        String userId = String.valueOf(SPTrueTemp.getUserId(context));
        String levels = SPTrueTemp.getUserLevel(context);
        String token = SPTrueTemp.getToken(context);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ble_asset_id", assetsId);
        APIService apiService = ApiClients.getRetrofitInstance(false).create(APIService.class);
        Observable<JsonObject> observable = apiService.deallocateSensor(BEARER+" "+token,CONTENT_TYPE, org, userId, levels, jsonObject).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        observable.subscribe(new Observer<JsonObject>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("Deallocate error ", e.getMessage());
            }

            @Override
            public void onNext(JsonObject jsonObject) {
                boolean success = jsonObject.get("success").getAsBoolean();
                String response = String.valueOf(jsonObject.get("message"));
                response = response.replaceAll("^\"|\"$", "");
                Log.e("Deallocate response ", response);
                if(success) {
                    listener.callBack(true);
                }
                else {
                    listener.callBack(false);
                }
            }
        });
    }
}
