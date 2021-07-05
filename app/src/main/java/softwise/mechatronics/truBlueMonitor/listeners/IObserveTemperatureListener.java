package softwise.mechatronics.truBlueMonitor.listeners;

public interface IObserveTemperatureListener {
    void loadTemperature(int sensorId,String temp,String unit);
}
