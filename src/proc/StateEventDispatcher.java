package proc;

/**
 * @author Nestor
 */
public interface StateEventDispatcher {

    public void addStateListener(StateListener l);

    public void addStateListeners(StateListener[] l);

    public void fireStateChanged(StateEvent ev);

    public StateListener[] getStateListeners();

    public void removeAll();

    public void removeStateListener(StateListener l);
}