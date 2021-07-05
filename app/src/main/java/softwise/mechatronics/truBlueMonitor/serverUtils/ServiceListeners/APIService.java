package softwise.mechatronics.truBlueMonitor.serverUtils.ServiceListeners;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import softwise.mechatronics.truBlueMonitor.models.AssetAndSensorInfo;
import softwise.mechatronics.truBlueMonitor.models.LoginResponse;
import softwise.mechatronics.truBlueMonitor.models.RefreshTokenResponse;
import softwise.mechatronics.truBlueMonitor.models.Sensor;
import softwise.mechatronics.truBlueMonitor.models.SensorIds;
import softwise.mechatronics.truBlueMonitor.models.UserCredentials;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

public interface APIService {
    @POST("/api/auth/bluetooth/login")
    //Observable<LoginResponse> login(@Body UserCredentials userCredentials);
    Observable<LoginResponse> login(@Header("Content-Type")String contentType,@Body UserCredentials userCredentials);
   /* @POST("/api/bluetooth/login")
    Observable<LoginResponse> login(@Body UserCredentials userCredentials);*/

    @GET("/api/auth/token/{operatorId}/regenerate")
    Call<RefreshTokenResponse> refreshToken(@Path("operatorId")int operatorId);
    /*//@POST("api/bluetooth/sensor/setup")
    Observable<List<AssetAndSensorInfo>> getAssetsData(@Header("token")String token, @Header ("org")String org, @Header("user")String user, @Header("level") String level, @Body SensorIds sensorData);*/

    @POST("api/bluetooth/asset/setup")
    Observable<JsonObject> setUpAssetsData(@Header("Authorization")String token,@Header("Content-Type")String contentType, @Header ("org")String org, @Header("user")String user, @Header("level") String level, @Body Sensor sensor);

    @POST("api/bluetooth/asset/allocate")
    Observable<AssetAndSensorInfo> getAssetsData(@Header("Authorization")String token,@Header("Content-Type")String contentType, @Header ("org")String org, @Header("user")String user, @Header("level") String level, @Body SensorIds sensorData);

    @POST("api/sensor/ble-data")
    Observable<JsonObject> uploadTempData(@Header("Content-Type")String contentType,@Body JsonArray sensorData);

   /* @GET("api/bluetooth/{user_id}/logout")
    Observable<JsonObject> logoutFromServer(@Path("user_id")int usrId,@Header("Authorization")String token, @Header ("org")int org, @Header("user")int user);*/

    @POST("api/bluetooth/logout")
    Observable<JsonObject> logoutFromServer(@Header("Authorization")String token,@Header("Content-Type")String contentType, @Body JsonObject jsonObject);

    @GET("api/bluetooth/asset/list")
    Observable<List<AssetAndSensorInfo>> getAllAssetsData(@Header("Authorization")String token,@Header("Content-Type")String contentType,@Header ("org")String org);

    @POST("api/bluetooth/saveAsset")
    Observable<String> saveAssetsData(@Header("token")String token,@Body AssetAndSensorInfo assetAndSensorInfo);

    @POST("api/bluetooth/sensor/deallocate")
    Observable<JsonObject> deallocateSensor(@Header("Authorization")String token,@Header("Content-Type")String contentType, @Header ("org")String org, @Header("user")String user, @Header("level") String level, @Body JsonObject jsonObject);

}
