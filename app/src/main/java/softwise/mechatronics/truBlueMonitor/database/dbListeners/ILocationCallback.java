package softwise.mechatronics.truBlueMonitor.database.dbListeners;

import softwise.mechatronics.truBlueMonitor.database.gpsloc;

public interface ILocationCallback {
    void getLocation(gpsloc gpsloc);

}
