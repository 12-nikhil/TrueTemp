package softwise.mechatronics.truBlueMonitor.database;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.room.Room;

import softwise.mechatronics.truBlueMonitor.database.dbListeners.DatabaseCallback;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ILocationCallback;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ISensorDataCallback;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ISingleSensorData;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.ISingleSensorTempCallback;
import softwise.mechatronics.truBlueMonitor.database.dbListeners.SensorDatabaseCallback;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DatabaseClient {

    private Context mCtx;
    private static DatabaseClient mInstance;
    public static final String DB_NAME = "sensor_db.db";

    //our app database object
    private AppDatabase appDatabase;
    private String TAG;

    public DatabaseClient(Context mCtx) {
        this.mCtx = mCtx;
        TAG = getClass().getSimpleName();

        //creating the app database with Room database builder
        //DB_NAME contain database name
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, DB_NAME).build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    public void getAllSensor(final ISensorDataCallback sensorDataCallback) {
       /* appDatabase.sensorDao().getAllSensor().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<EntitySensor>>() {
            @Override
            public void accept(List<EntitySensor> entitySensors) throws Exception {
                sensorDataCallback.onLoadAllSensor(entitySensors);
            }
        });*/
        appDatabase.sensorDao().getAllSensorData(true).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<EntitySensor>>() {
            @Override
            public void accept(List<EntitySensor> entitySensors) throws Exception {
                sensorDataCallback.onLoadAllSensor(entitySensors);
            }
        });
    }

    public List<EntitySensor> getAllSensorData() {
        final List<EntitySensor>[] entitySensorList = new List[]{null};
        appDatabase.sensorDao().getAllSensor().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<EntitySensor>>() {
            @Override
            public void accept(List<EntitySensor> entitySensors) throws Exception {
                entitySensorList[0] = new ArrayList<>();
                entitySensorList[0].addAll(entitySensors);
            }
        });
        return entitySensorList[0];
    }

    public void getAndSaveEntitySensor(int sensorId, EntitySensor entitySensor, SensorDatabaseCallback iSingleSensorData) {
        final boolean[] result = {false};
        appDatabase.sensorDao().getSensorById(sensorId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<EntitySensor>() {
            @Override
            public void accept(EntitySensor entitySensors) throws Exception {
                // Log.e("Sensor Data in local db", String.valueOf(entitySensors.getSensor_id()));
                if (entitySensors != null) {
                    result[0] = false;
                } else {
                    // save data in local db
                    saveEntitySensor(entitySensor, new SensorDatabaseCallback() {
                        @Override
                        public void onSensorAdded() {
                            result[0] = true;
                            iSingleSensorData.onSensorAdded();
                        }

                        @Override
                        public void onSensorAddedFailed() {
                            result[0] = false;
                            iSingleSensorData.onSensorAddedFailed();
                        }
                    });
                }

            }
        });
      //return result[0];
    }

    public EntitySensor getEntitySensor(int sensorId) {
        final EntitySensor[] eS = {null};
        appDatabase.sensorDao().getSensorById(sensorId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<EntitySensor>() {
            @Override
            public void accept(EntitySensor entitySensors) throws Exception {
                // Log.e("Sensor Data in local db", String.valueOf(entitySensors.getSensor_id()));
                eS[0] = entitySensors;
            }
        });
        return eS[0];
    }

    public void getEntitySensorData(int sensorId, ISingleSensorData iSingleSensorData) {
        appDatabase.sensorDao().getSensorById(sensorId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<EntitySensor>() {
            @Override
            public void accept(EntitySensor entitySensors) throws Exception {
                // Log.e("Single Sensor Data", String.valueOf(entitySensors.getSensor_id()));
                iSingleSensorData.onLoadSensor(entitySensors);
            }
        });
    }

    public void saveEntitySensor(EntitySensor entitySensor, SensorDatabaseCallback databaseCallback) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().insert(entitySensor);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallback.onSensorAdded();
                //Log.e(TAG, "Data add in db" + entitySensor.getBle_sensor_id());
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onSensorAddedFailed();
                e.printStackTrace();
            }
        });
    }

    public void saveEntitySensorList(List<EntitySensor> entitySensor, SensorDatabaseCallback databaseCallback) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                for(EntitySensor sensor:entitySensor) {
                    appDatabase.sensorDao().insert(sensor);
                }
            }
        }).observeOn(AndroidSchedulers.from(Looper.getMainLooper()))
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "Data add in db "+entitySensor.get(0).getBle_sensor_id());
                databaseCallback.onSensorAdded();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onSensorAddedFailed();
                e.printStackTrace();
            }
        });
    }

    public void addSensorData( EntitySensor entitySensor) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().insert(entitySensor);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                //Log.e(TAG,"Data add in db"+entitySensor.getBle_sensor_id());
            }

            @Override
            public void onError(Throwable e) {
               e.printStackTrace();
            }
        });
    }

    public void deleteSensoryId(int id) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().deleteById(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    public void deleteSensor(EntitySensor entitySensor) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().delete(entitySensor);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }
    public void updateSensorData(final EntitySensor entitySensor) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().update(entitySensor);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {d.dispose();
            }

            @Override
            public void onComplete() {
                Log.e(TAG,"Data update in db"+entitySensor.getBle_sensor_id()+" Frequency = "+entitySensor.getUpdate_frequency());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }
    public void updateEntitySensorData(final EntitySensor entitySensor) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().update(entitySensor);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {d.dispose();
            }

            @Override
            public void onComplete() {
                Log.e(TAG,"Data update in db"+entitySensor.getBle_sensor_id()+" Flag = "+entitySensor.isFlag());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }


    public void updateSensor(final EntitySensor entitySensor,final DatabaseCallback databaseCallback) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().update(entitySensor);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {d.dispose();
            }

            @Override
            public void onComplete() {
                databaseCallback.onSensorUpdated();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onDataNotAvailable();
            }
        });
    }

    public void updateSensorTempUnit(String temp,String unit,int id) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().updateTemp(temp,unit,id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                d.dispose();
            }

            @Override
            public void onComplete() {
                Log.e("Sensor update","Successfuly"+temp+" id "+id);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }
    // ========= Location =============
    public void addLocation(final gpsloc gpsloc) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.gpslocDao().insert(gpsloc);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }
    public void updateLocation(final gpsloc gpsloc) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.gpslocDao().update(gpsloc);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void getLocation(int userId,final ILocationCallback locationCallback) {
        appDatabase.gpslocDao().getGpsLocByUserId(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<gpsloc>() {
            @Override
            public void accept(gpsloc gpsloc) throws Exception {
                locationCallback.getLocation(gpsloc);
            }
        });
    }

    // ======================= sensor temp time ============================

    public void addSensorTemp(SensorTempTime sensorTempTime) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorTempTimeDao().insert(sensorTempTime);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e("SensorTemp data","add disposable");
            }

            @Override
            public void onComplete() {
                Log.e("SensorTemp data","add Complete");
            }

            @Override
            public void onError(Throwable e) {
                Log.e("SensorTemp data error","add "+e.getMessage());
            }
        });
    }

    public void getAllSensorTemp(final ISensorTempCallback sensorTempCallback) {
        appDatabase.sensorTempTimeDao().getAllSensorTemp().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<SensorTempTime>>() {
            @Override
            public void accept(List<SensorTempTime> sensorTempTimes) throws Exception {
                sensorTempCallback.loadTemperature(sensorTempTimes);
            }
        });
    }

    public void getSensorTemp(int sensor_id,final ISensorTempCallback sensorTempCallback) {
        appDatabase.sensorTempTimeDao().getSensorTempById(sensor_id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<SensorTempTime>>() {
            @Override
            public void accept(List<SensorTempTime> sensorTempTimes) throws Exception {
                sensorTempCallback.loadTemperature(sensorTempTimes);
            }
        });
    }

    public void getSingleSensorTemp(int sensor_id,final ISingleSensorTempCallback sensorTempCallback) {
        appDatabase.sensorTempTimeDao().getSingleSensorTempById(sensor_id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<SensorTempTime>() {
                    @Override
                    public void accept(SensorTempTime sensorTempTime) throws Exception {
                        sensorTempCallback.onSensorTempLoad(sensorTempTime);
                    }
                });
    }

    public void getSensorTempByFlag(boolean flag,final softwise.mechatronics.truBlueMonitor.database.dbListeners.ISensorTempCallback sensorTempCallback) {
        appDatabase.sensorTempTimeDao().getSingleSensorTempByFlag(flag).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<List<SensorTempTime>>() {
                    @Override
                    public void accept(List<SensorTempTime> sensorTempTime) throws Exception {
                        sensorTempCallback.onTempLoad(sensorTempTime);
                    }
                });
    }

    public void deleteSensorTempId(int id) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorTempTimeDao().deleteSensorTempById(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }


    public void deleteSensorTable() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                appDatabase.sensorDao().deleteSensorTable();
            }
        }).observeOn(AndroidSchedulers.from(Looper.getMainLooper()))
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }
}
