package softwise.mechatronics.truBlueMonitor.listeners;

public interface SerialListener {
    void onSerialConnect();

    void onSerialConnectError(Exception e);

    void onSerialRead(byte[] data);

    void onSerialReadString(String data);

    void onSerialIoError(Exception e);
}
