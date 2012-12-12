package gr.ntua.vision.monitoring.management;

import java.util.Observable;
import java.util.Vector;


public class ManagementMessageQueue extends Observable {
    private Vector<ManagementMessage> queue;


    public synchronized void addMessage(final ManagementMessage m) {
        queue.add(m);
        notifyObservers();
    }


    public synchronized ManagementMessage getMessage() {
        if (!queue.isEmpty())
            return queue.remove(0);
        else
            return null;
    }
}
