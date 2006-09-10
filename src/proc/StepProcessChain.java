package proc;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import proc.content.ContentEvent;
import proc.content.ContentListener;
import proc.param.StepProcessParameter;

/**
 * @author Nestor
 */
public class StepProcessChain implements StepProcess {

    private StepProcess currentProcess = null;

    private Vector processes = new Vector();

    public StepProcessChain(Collection processes) {
        super();
        this.processes.addAll(processes);
        nextProcess();
        if (currentProcess == null) {
            throw new Error("no processes to run");
        }
    }

    public void addContentListener(ContentListener l) {
        currentProcess.addContentListener(l);
    }

    public void addContentListeners(ContentListener[] l) {
        currentProcess.addContentListeners(l);
    }

    public void addParameter(StepProcessParameter param) {
        currentProcess.addParameter(param);
    }

    public void addStateListener(StateListener l) {
        currentProcess.addStateListener(l);
    }

    public void addStateListeners(StateListener[] l) {
        currentProcess.addStateListeners(l);
    }

    public synchronized void completeProcess() {
        (new Thread(new Runnable() {
            public void run() {
                currentProcess.completeProcess();
                while (currentProcess.getProcessState() != StepProcess.STOPPED)
                    ;
                if (nextProcess()) {
                    currentProcess.initProcess();
                }
            }
        })).start();
    }

    public void fireContentsChanged(ContentEvent ev) {
        currentProcess.fireContentsChanged(ev);
    }

    public void fireStateChanged(StateEvent ev) {
        currentProcess.fireStateChanged(ev);
    }

    public Object getContent() {
        return currentProcess.getContent();
    }

    public ContentListener[] getContentListeners() {
        return currentProcess.getContentListeners();
    }

    public int getCurrentStep() {
        if (currentProcess != null) {
            return currentProcess.getCurrentStep();
        }
        return 0;
    }

    public StepProcessParameter getParameter(String name) {
        for (Iterator iter = processes.iterator(); iter.hasNext();) {
            StepProcess p = (StepProcess) iter.next();
            if (p.getParameter(name) != null) {
                return p.getParameter(name);
            }
        }
        return null;
    }

    public int getParameterCount() {
        int res = 0;
        for (Iterator iter = processes.iterator(); iter.hasNext();) {
            StepProcess p = (StepProcess) iter.next();
            res += p.getParameterCount();
        }
        return res;
    }

    public Collection getParameters() {
        Vector res = new Vector();
        for (Iterator iter = processes.iterator(); iter.hasNext();) {
            StepProcess p = (StepProcess) iter.next();
            res.addAll(p.getParameters());
        }
        return res;
    }

    public String getProcessName() {
        return currentProcess.getProcessName();
    }

    public int getProcessState() {
        return currentProcess.getProcessState();
    }

    public StateListener[] getStateListeners() {
        return currentProcess.getStateListeners();
    }

    public synchronized void initProcess() {
        StepProcess newProcess = (StepProcess) processes.get(0);
        moveListeners(currentProcess, newProcess);
        currentProcess = newProcess;
        currentProcess.initProcess();
    }

    private void moveListeners(StepProcess from, StepProcess to) {
        ContentListener[] cll = currentProcess.getContentListeners();
        to.addContentListeners(cll);
        for (int i = 0; i < cll.length; i++) {
            from.removeContentListener(cll[i]);
        }
        StateListener[] sll = currentProcess.getStateListeners();
        to.addStateListeners(sll);
        for (int i = 0; i < sll.length; i++) {
            from.removeStateListener(sll[i]);
        }
    }

    private boolean nextProcess() {
        if (processes.size() == 0) {
            return false;
        }
        if (currentProcess == null) {
            // to jest stan poczatkowy
            currentProcess = (StepProcess) processes.get(0);
            return true;
        } else if (processes.indexOf(currentProcess) < processes.size() - 1) {
            // nie jest ostatni
            StepProcess newProcess = (StepProcess) processes.get(processes
                    .indexOf(currentProcess) + 1);

            // przerzucenie listenerow do nowego procesu
            moveListeners(currentProcess, newProcess);

            // ustawienie zawartosci
            //            newProcess.setContent(currentProcess.getContent());
            currentProcess = newProcess;
            return true;
        } else {
            // jest ostatni, nie zmieniam go
            return false;
        }
    }

    public void removeAll() {
        currentProcess.removeAll();
    }

    public void removeContentListener(ContentListener l) {
        currentProcess.removeContentListener(l);
    }

    public void removeStateListener(StateListener l) {
        currentProcess.removeStateListener(l);
    }

    public synchronized void runProcess() {
        (new Thread(new Runnable() {
            public void run() {
                currentProcess.runProcess();
                while (currentProcess.getProcessState() != StepProcess.STOPPED)
                    ;
                if (nextProcess()) {
                    currentProcess.initProcess();
                }
            }
        })).start();
    }

    //    public void setContent(Object c) {
    //        currentProcess.setContent(c);
    //    }

    public void stepProcess() {
        if (currentProcess == null) {
            return;
        }
        int currentStep = currentProcess.getCurrentStep();
        currentProcess.stepProcess();
        if (currentProcess.getCurrentStep() == currentStep) {
            // algorytm uwaza sie za zakonczony
            if (nextProcess()) {
                currentProcess.initProcess();
            }
        }
    }

    public void stopProcess() {
        currentProcess.stopProcess();
    }

}