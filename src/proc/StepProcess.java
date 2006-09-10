package proc;

import proc.content.ContentEventDispatcher;
import proc.param.ParameterContainer;

/**
 * @author Nestor
 */
public interface StepProcess extends ParameterContainer,
        ContentEventDispatcher, StateEventDispatcher {

    public static final int FASTRUNNING = -4;

    public static final int NOTINITED = -3;

    public static final int RUNNING = -1;

    public static final int STOPPED = -2;

    public void completeProcess();

    public Object getContent();

    public int getCurrentStep();

    public String getProcessName();

    public int getProcessState();

    public void initProcess();

    public void runProcess();

    public void stepProcess();

    public void stopProcess();

}