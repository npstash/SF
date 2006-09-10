package proc.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import proc.StepProcessInitializator;

public class StepProcessMainFrame extends JFrame implements ActionListener {

	protected JDesktopPane desktop = new JDesktopPane();

	private JMenuItem[] jmiProcs = null;

	private StepProcessInitializator[] procs = null;

	private int windowCount = 0;

	public StepProcessMainFrame(String title, StepProcessInitializator[] procs) {
		super(title);
		this.procs = procs;

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenu menu1 = new JMenu("Menu");
		menuBar.add(menu1);

		jmiProcs = new JMenuItem[this.procs.length];
		for (int i = 0; i < this.procs.length; i++) {
			JMenuItem jmiNew = new JMenuItem(this.procs[i].getName());
			jmiProcs[i] = jmiNew;
			jmiNew.addActionListener(this);
			menu1.add(jmiNew);
		}

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(desktop, BorderLayout.CENTER);
		try {
			String lfClassName = "javax.swing.plaf.metal.MetalLookAndFeel";
			UIManager.setLookAndFeel(lfClassName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setSize(800, 500);
		setLocation(0, 0);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JMenuItem) {
			for (int i = 0; i < jmiProcs.length; i++) {
				if (jmiProcs[i] == e.getSource()) {
					startNewProcess(procs[i]);
					break;
				}
			}
		}
	}

	private void startNewProcess(StepProcessInitializator p) {
		StepProcessInternalFrame f = new StepProcessInternalFrame(p.getName(),
				p.newInstance(), desktop, p.getToolsClasses(), p
						.getToolsNames());
		f.setLocation(20 * (windowCount % 10), 20 * (windowCount % 10));
		windowCount++;
		desktop.add(f);
		f.setVisible(true);
	}

}