package proc;

import proc.content.DiscardingContentEventDispatcher;
import proc.content.ContentEvent;
import proc.content.ContentListener;
import proc.param.DefaultParameterContainer;
import proc.param.StepProcessParameter;

/**
 * @author Nestor
 */
public abstract class DefaultStepProcess extends DefaultParameterContainer
		implements StepProcess {

	public static final String PARAM_DELAY = "Pauza miêdzy krokami w ms";

	public static final String PARAM_FREFRESH = "Czestosc odswierzania przy szybkiej symulacji";

	private DiscardingContentEventDispatcher contentDispatcher = new DiscardingContentEventDispatcher();

	private int currentState = StepProcess.NOTINITED;

	private int currentStep = 0;

	protected boolean notificationEnabled = true;

	private QueueingStateEventDispatcher stateDispatcher = new QueueingStateEventDispatcher();

	public DefaultStepProcess() {
		super();
		addParameter(new StepProcessParameter(PARAM_DELAY, new Integer(500),
				new Integer(0), null));
		addParameter(new StepProcessParameter(PARAM_FREFRESH, new Integer(100),
				new Integer(1), null));
	}

	public void addContentListener(ContentListener l) {
		contentDispatcher.addContentListener(l);
		if (notificationEnabled) {
			if (getContent() != null) {
				fireContentsChanged(new ContentEvent(getContent()));
			}
		}
	}

	public void addContentListeners(ContentListener[] l) {
		contentDispatcher.addContentListeners(l);
	}

	public void addStateListener(StateListener l) {
		stateDispatcher.addStateListener(l);
		fireStateChanged(new StateEvent(this));
	}

	public void addStateListeners(StateListener[] l) {
		stateDispatcher.addStateListeners(l);
	}

	public void completeProcess() {
		if (getProcessState() == StepProcess.NOTINITED) {
			return;
		}
		setProcessState(StepProcess.FASTRUNNING);
		(new Thread(new Runnable() {
			public void run() {
				synchronized (this) {
					notificationEnabled = false;
					while (getProcessState() != StepProcess.STOPPED) {
						if (getCurrentStep()
								% ((Integer) getParameter(PARAM_FREFRESH)
										.getValue()).intValue() == 0) {
							notificationEnabled = true;
						}
						stepProcess();
						notificationEnabled = false;
					}
					notificationEnabled = true;
					if (getContent() != null) {
						fireContentsChanged(new ContentEvent(getContent()));
					}
					this.notifyAll();
				}
			}
		})).start();
	}

	public void fireContentsChanged(ContentEvent ev) {
		contentDispatcher.fireContentsChanged(ev);
	}

	public void fireStateChanged(StateEvent ev) {
		stateDispatcher.fireStateChanged(ev);
	}

	public ContentListener[] getContentListeners() {
		return contentDispatcher.getContentListeners();
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public String getProcessName() {
		return this.getClass().getName();
	}

	public int getProcessState() {
		return currentState;
	}

	public StateListener[] getStateListeners() {
		return stateDispatcher.getStateListeners();
	}

	private void incCurrentStep() {
		currentStep++;
		if (notificationEnabled) {
			stateDispatcher.fireStateChanged(new StateEvent(this));
		}
	}

	public void initProcess() {
		setProcessState(StepProcess.STOPPED);
		currentStep = 0;
		if (getContent() != null) {
			fireContentsChanged(new ContentEvent(getContent()));
		}
	}

	public void removeAll() {
		contentDispatcher.removeAll();
	}

	public void removeContentListener(ContentListener l) {
		contentDispatcher.removeContentListener(l);
	}

	public void removeStateListener(StateListener l) {
		stateDispatcher.removeStateListener(l);
	}

	public void runProcess() {
		if (getProcessState() == StepProcess.NOTINITED) {
			return;
		}
		setProcessState(StepProcess.RUNNING);
		(new Thread(new Runnable() {
			public void run() {
				synchronized (this) {
					while (getProcessState() != StepProcess.STOPPED) {
						stepProcess();
						try {
							int idelay = ((Integer) getParameter(PARAM_DELAY)
									.getValue()).intValue();
							Thread.sleep(idelay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					this.notifyAll();
				}
			}
		})).start();
	}

	protected void setProcessState(int s) {
		currentState = s;
		fireStateChanged(new StateEvent(this));
	}

	public void stepProcess() {
		incCurrentStep();
		if (notificationEnabled) {
			if (getContent() != null) {
				fireContentsChanged(new ContentEvent(getContent()));
			}
		}
	}

	public void stopProcess() {
		setProcessState(StepProcess.STOPPED);
	}
}