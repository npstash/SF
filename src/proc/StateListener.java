package proc;

import java.util.EventListener;

/**
 * @author Nestor
 */
public interface StateListener extends EventListener {

    public void stateChanged(StateEvent e);

}