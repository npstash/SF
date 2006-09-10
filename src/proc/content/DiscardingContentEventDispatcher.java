package proc.content;

import javax.swing.event.EventListenerList;

import proc.Semaphore;

/**
 * @author Nestor
 */
public class DiscardingContentEventDispatcher extends Thread implements
        ContentEventDispatcher {

    private ContentEvent lastEvent = null;

    private EventListenerList listeners = new EventListenerList();

    private Semaphore mutex = new Semaphore();

    public boolean runThread = true;

    public DiscardingContentEventDispatcher() {
        super();
        start();
    }

    public void addContentListener(ContentListener l) {
        listeners.add(ContentListener.class, l);
    }

    public void addContentListeners(ContentListener[] l) {
        for (int i = 0; i < l.length; i++) {
            listeners.add(ContentListener.class, l[i]);
        }
    }

    public synchronized void fireContentsChanged(ContentEvent ev) {
        synchronized (mutex) {
            lastEvent = ev;
            // possibly discarding previous lastEvent
            // this guarantees to fire most-recent event
            mutex.raise();
        }
    }

    public ContentListener[] getContentListeners() {
        return (ContentListener[]) listeners
                .getListeners(ContentListener.class);
    }

    public void removeAll() {
        ContentListener[] ls = getContentListeners();
        for (int i = 0; i < ls.length; i++) {
            removeContentListener(ls[i]);
        }
    }

    public void removeContentListener(ContentListener l) {
        listeners.remove(ContentListener.class, l);
    }

    public void run() {
        while (runThread) {
            ContentEvent event = null;
            synchronized (mutex) {
                mutex.lower();
                event = lastEvent;
                lastEvent = null;
            }

            if (event != null) {
                ContentListener[] list = getContentListeners();
                for (int i = 0; i < list.length; i++) {
                    ContentListener cl = list[i];
                    cl.contentsChanged(event);
                }
            }
        }
        System.out.println("Dispatcher thread exits");
    }

}