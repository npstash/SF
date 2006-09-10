package proc;

import java.util.EventObject;

/**
 * @author Nestor
 */
public class StateEvent extends EventObject {

    private StepProcess contents = null;

    public StateEvent(StepProcess source) {
        super(source);
        contents = source;
    }

    public StepProcess getStepProcess() {
        return contents;
    }
}