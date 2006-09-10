package proc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.EventListenerList;

/**
 * @author Nestor
 */
public class QueueingStateEventDispatcher extends Thread implements
        StateEventDispatcher {

    private List eventQueue = Collections.synchronizedList(new LinkedList());

    private EventListenerList listeners = new EventListenerList();

    private Semaphore mutex = new Semaphore();

    public boolean runThread = true;

    public QueueingStateEventDispatcher() {
        super();
        start();
    }

    public void addStateListener(StateListener l) {
        listeners.add(StateListener.class, l);
    }

    public void addStateListeners(StateListener[] l) {
        for (int i = 0; i < l.length; i++) {
            listeners.add(StateListener.class, l[i]);
        }
    }

    public void finalize() {
        runThread = false;
        synchronized (mutex) {
            mutex.raise();
        }
    }

    public void fireStateChanged(StateEvent ev) {
        synchronized (mutex) {
            eventQueue.add(ev);
            mutex.raise();
        }
    }

    public StateListener[] getStateListeners() {
        return (StateListener[]) listeners.getListeners(StateListener.class);
    }

    public void removeAll() {
        StateListener[] ls = getStateListeners();
        for (int i = 0; i < ls.length; i++) {
            removeStateListener(ls[i]);
        }
    }

    public void removeStateListener(StateListener l) {
        listeners.remove(StateListener.class, l);
    }

    public void run() {
        while (runThread) {
            synchronized (mutex) {
                mutex.lower();
            }
            while (!eventQueue.isEmpty()) {
                StateEvent ev = (StateEvent) eventQueue.remove(0);
                StateListener[] list = getStateListeners();
                for (int i = 0; i < list.length; i++) {
                    StateListener cl = list[i];
                    cl.stateChanged(ev);
                }
            }
        } // while
    } // run

}