package proc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

import proc.StateEvent;
import proc.StateListener;
import proc.StepProcess;
import util.ImageTools;

/**
 * @author Nestor
 */
public class StepProcessControl extends JPanel implements ActionListener,
		StateListener {

	public static final int BTN_COMPLETE = 4;

	public static final int BTN_INIT = 1;

	public static final int BTN_RUN = 2;

	public static final int BTN_SETUP = 8;

	public static final int BTN_STEP = 16;

	public static final int BTN_STOP = 32;

	public static String iconDirectory = "proc\\ui\\";

	public static String BTNS_CONTROLPARAMETERNAME = "BTNS_CONTROLPARAMETERNAME";

	public static final int PNL_INFO = 64;

	private static final int STATE_INITED = 2;

	private static final int STATE_STARTED = 1;

	private static final int STATE_ZERO = 0;

	private ImageIcon iiComplete = ImageTools.makeTransparentIcon(
			new ImageIcon(iconDirectory + "complete.GIF"), Color.WHITE);

	private ImageIcon iiInit = ImageTools.makeTransparentIcon(new ImageIcon(
			iconDirectory + "init.GIF"), Color.WHITE);

	private ImageIcon iiRun = ImageTools.makeTransparentIcon(new ImageIcon(
			iconDirectory + "run.GIF"), Color.WHITE);

	private ImageIcon iiSetup = ImageTools.makeTransparentIcon(new ImageIcon(
			iconDirectory + "setup.GIF"), Color.WHITE);

	private ImageIcon iiStep = ImageTools.makeTransparentIcon(new ImageIcon(
			iconDirectory + "step.GIF"), Color.WHITE);

	private ImageIcon iiStop = ImageTools.makeTransparentIcon(new ImageIcon(
			iconDirectory + "stop.GIF"), Color.WHITE);

	private JButton jbComplete = new JButton(iiComplete);

	private JButton jbInit = new JButton(iiInit);

	private JButton jbRun = new JButton(iiRun);

	private JButton jbSetup = new JButton(iiSetup);

	private JButton jbStep = new JButton(iiStep);

	private JButton jbStop = new JButton(iiStop);

	private JLabel jlProcessName = new JLabel("Nazwa procesu");

	private JLabel jlProcessState = new JLabel("Stan procesu");

	private JLabel jlProcessStep = new JLabel("Krok");

	private int lastState = -1000;

	private StepProcess process = null;

	private int state;

	private int useButtons = BTN_COMPLETE | BTN_INIT | BTN_RUN | BTN_SETUP
			| BTN_STOP | BTN_STEP | PNL_INFO;

	public StepProcessControl(StepProcess process) {
		super();
		if (process.getParameter(BTNS_CONTROLPARAMETERNAME) != null) {
			useButtons = ((Integer) process.getParameter(
					BTNS_CONTROLPARAMETERNAME).getValue()).intValue();
		}
		initComponents();
		this.process = process;
	}

	public StepProcessControl(StepProcess process, boolean simple) {
		super();
		if (simple)
			useButtons = BTN_RUN | BTN_INIT;
		initComponents();
		this.process = process;
	}

	public StepProcessControl(StepProcess process, int buttons) {
		super();
		useButtons = buttons;
		initComponents();
		this.process = process;
	}

	private Method currentMethod = null;

	private synchronized void runMethodInThread(Method m) {
		currentMethod = m;
		(new Thread() {
			public void run() {
				try {
					currentMethod.invoke(process, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == jbInit) {
				runMethodInThread(process.getClass().getMethod(
						"initProcess", null));
			} else if (e.getSource() == jbStep) {
				runMethodInThread(process.getClass().getMethod(
						"stepProcess", null));
			} else if (e.getSource() == jbStop) {
				runMethodInThread(process.getClass().getMethod(
						"stopProcess", null));
			} else if (e.getSource() == jbRun) {
				runMethodInThread(process.getClass().getMethod(
						"runProcess", null));
			} else if (e.getSource() == jbComplete) {
				runMethodInThread(process.getClass().getMethod(
						"completeProcess", null));
			} else if (e.getSource() == jbSetup) {
				doSettings();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void doSettings() {
		StepProcessParameterDialog dlg = new StepProcessParameterDialog(null,
				process.getParameters());
		dlg.setModal(true);
		dlg.setVisible(true);
	}

	private void initComponents() {
		JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
		jbRun.addActionListener(this);
		jbStep.addActionListener(this);
		jbStop.addActionListener(this);
		jbInit.addActionListener(this);
		jbComplete.addActionListener(this);
		jbSetup.addActionListener(this);
		jbRun.setToolTipText("Uruchom proces");
		jbStep.setToolTipText("Œledzenie krok po kroku");
		jbStop.setToolTipText("Pauzuj proces");
		jbInit.setToolTipText("Zainicjalizuj proces");
		jbComplete.setToolTipText("Uruchom, szybki przebieg");
		jbSetup.setToolTipText("Ustawienia procesu");
		ToolTipManager.sharedInstance().setInitialDelay(100);
		if ((useButtons & BTN_INIT) != 0) {
			toolBar.add(jbInit);
		}
		if ((useButtons & BTN_RUN) != 0) {
			toolBar.add(jbRun);
		}
		if ((useButtons & BTN_STEP) != 0) {
			toolBar.add(jbStep);
		}
		if ((useButtons & BTN_STOP) != 0) {
			toolBar.add(jbStop);
		}
		if ((useButtons & BTN_COMPLETE) != 0) {
			toolBar.add(jbComplete);
		}
		if ((useButtons & BTN_SETUP) != 0) {
			toolBar.add(jbSetup);
		}
		setLayout(new BorderLayout());
		if ((useButtons & PNL_INFO) != 0) {
			JPanel infos = new JPanel();
			infos.setLayout(new GridLayout(3, 1));
			infos.add(jlProcessName);
			infos.add(jlProcessState);
			infos.add(jlProcessStep);
			add(infos, BorderLayout.SOUTH);
		}
		add(toolBar, BorderLayout.CENTER);
	}

	private void setButtonsState(int processState) {
		switch (processState) {
		case StepProcess.NOTINITED:
			jbRun.setEnabled(false);
			jbStep.setEnabled(false);
			jbStop.setEnabled(false);
			jbComplete.setEnabled(false);
			break;
		case StepProcess.RUNNING:
			jbRun.setEnabled(false);
			jbStep.setEnabled(true);
			jbStop.setEnabled(true);
			jbComplete.setEnabled(false);
			break;
		case StepProcess.FASTRUNNING:
			jbRun.setEnabled(false);
			jbStep.setEnabled(true);
			jbStop.setEnabled(true);
			jbComplete.setEnabled(false);
			break;
		case StepProcess.STOPPED:
			jbRun.setEnabled(true);
			jbStep.setEnabled(true);
			jbStop.setEnabled(false);
			jbComplete.setEnabled(true);
			break;
		}
	}

	public synchronized void stateChanged(StateEvent e) {
		jlProcessName.setText(e.getStepProcess().getProcessName());
		jlProcessStep.setText("Krok " + e.getStepProcess().getCurrentStep());
		int state = e.getStepProcess().getProcessState();
		if (lastState != state) {
			lastState = state;
			setButtonsState(state);
			switch (state) {
			case StepProcess.RUNNING:
				jlProcessState.setText("Uruchmiony");
				break;
			case StepProcess.STOPPED:
				jlProcessState.setText("Zatrzymany");
				break;
			case StepProcess.FASTRUNNING:
				jlProcessState.setText("Uruchomiony, szybki przebieg");
				break;
			case StepProcess.NOTINITED:
				jlProcessState.setText("Nie zainicjalizowany");
				break;
			}
		}
	}

}