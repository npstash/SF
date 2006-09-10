package proc.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import proc.StateEventDispatcher;
import proc.StepProcess;
import proc.content.ContentEventDispatcher;
import proc.content.ContentListener;
import proc.param.ParameterContainer;

/**
 * @author Nestor
 */
public class StepProcessToolInternalFrame extends JInternalFrame implements
		ActionListener {

	private class ToolIsaListener extends InternalFrameAdapter {
		public void internalFrameClosed(InternalFrameEvent e) {
			process.removeContentListener((ContentListener) tool);
			if (tool instanceof ContentEventDispatcher) {
				((ContentEventDispatcher) tool).removeAll();
			}
			if (tool instanceof StateEventDispatcher) {
				((StateEventDispatcher) tool).removeAll();
			}
		}
	}

	private class ProcIsaListener extends InternalFrameAdapter {
		public void internalFrameClosed(InternalFrameEvent e) {
			// TODO. unreliable code
			process.removeContentListener((ContentListener) tool);
			if (process instanceof ContentEventDispatcher) {
				((ContentEventDispatcher) tool).removeAll();
			}
			if (process instanceof StateEventDispatcher) {
				((StateEventDispatcher) tool).removeAll();
			}
		}
	}

	private StepProcess process = null;

	private Component tool = null;

	public StepProcessToolInternalFrame(String title, StepProcess proc,
			Component toolComponent) {
		super(title, true, true, true, true);
		tool = toolComponent;
		if (tool instanceof ParameterContainer) {
			JMenuBar menuBar = new JMenuBar();
			JMenuItem menu = new JMenuItem("Ustawienia");
			menu.addActionListener(this);
			menuBar.add(menu);
			setJMenuBar(menuBar);
		}
		if ((proc != null) && (tool instanceof ContentListener)) {
			process = proc;
			process.addContentListener((ContentListener) tool);
			addInternalFrameListener(new ToolIsaListener());
		}
		// TODO. unreliable code
		if ((proc != null) && (proc instanceof ContentListener)
				&& (tool instanceof ContentEventDispatcher)) {
			process = proc;
			((ContentEventDispatcher) tool)
					.addContentListener((ContentListener) proc);
			addInternalFrameListener(new ProcIsaListener());
		}
		getContentPane().add(toolComponent);
	}

	public void actionPerformed(ActionEvent e) {
		if (tool instanceof ParameterContainer) {
			StepProcessParameterDialog dlg = new StepProcessParameterDialog(
					null, ((ParameterContainer) tool).getParameters());
			dlg.setModal(true);
			dlg.setVisible(true);
			repaint();
		}
	}

}