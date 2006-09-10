package proc.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import proc.StepProcess;
import util.HashRegistry;

/**
 * @author Nestor
 */
public class StepProcessInternalFrame extends JInternalFrame implements
		ActionListener {

	private JDesktopPane addto = null;

	private StepProcessControl control = null;

	public Object heightKey = new Object();

	private StepProcess proc = null;

	private List toolFrames = new LinkedList();

	private JMenuItem[] tools = null;

	private Class[] toolClasses = null;

	public Object widthKey = new Object();

	public StepProcessInternalFrame(String title, StepProcess process,
			JDesktopPane pane, Collection additionalToolsClasses, String[] names) {
		super(title, true, true, true, true);
		addto = pane;
		proc = process;
		if ((names == null) && (additionalToolsClasses != null)) {
			names = new String[additionalToolsClasses.size()];
			int i = 0;
			for (Iterator iter = additionalToolsClasses.iterator(); iter
					.hasNext();) {
				Class c = (Class) iter.next();
				names[i++] = c.getName();
			}
		}

		if (additionalToolsClasses != null) {
			if (additionalToolsClasses.size() > 0) {
				JMenuBar menuBar = new JMenuBar();
				JMenu menu = new JMenu("Narzedzia");
				int i = 0;
				toolClasses = new Class[additionalToolsClasses.size()];
				tools = new JMenuItem[additionalToolsClasses.size()];
				for (Iterator iter = additionalToolsClasses.iterator(); iter
						.hasNext();) {
					Class c = (Class) iter.next();
					JMenuItem item = new JMenuItem(names[i]);
					tools[i] = item;
					item.addActionListener(this);
					menu.add(item);
					toolClasses[i] = c;
					i++;
				}
				menuBar.add(menu);
				setJMenuBar(menuBar);
			}
		}
		control = new StepProcessControl(proc);
		getContentPane().add(control);
		proc.addStateListener(control);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosed(InternalFrameEvent e) {
				proc.removeStateListener(control);
				while (!toolFrames.isEmpty()) {
					JInternalFrame f = (JInternalFrame) toolFrames.get(0);
					f.dispose();
				}
				toolFrames.clear();
			}
		});
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem item = (JMenuItem) e.getSource();
			Class toolClass = null;
			for (int i = 0; i < tools.length; i++) {
				if (tools[i] == e.getSource()) {
					toolClass = toolClasses[i];
					break;
				}
			}
			if (toolClass == null)
				return;
			String className = item.getText();
			try {
				Object toolObject = toolClass.newInstance();

				if (toolObject instanceof JFrame) {
					JFrame toolJFrame = (JFrame) toolObject;
					toolJFrame.setVisible(true);
				} else if (toolObject instanceof Component) {
					Component toolComponent = (Component) toolObject;
					StepProcessToolInternalFrame toolFrame = new StepProcessToolInternalFrame(
							className, proc, toolComponent);
					toolFrame.setLocation(getLocation().x + 50,
							getLocation().y + 50);
					Integer w = (Integer) HashRegistry.getObject(widthKey);
					Integer h = (Integer) HashRegistry.getObject(heightKey);
					if (w == null)
						w = new Integer(350);
					if (h == null)
						h = new Integer(300);
					toolFrame.setSize(w.intValue(), h.intValue());
					addto.add(toolFrame);
					toolFrames.add(toolFrame);
					toolFrame
							.addInternalFrameListener(new InternalFrameAdapter() {
								public void internalFrameClosed(
										InternalFrameEvent e) {
									toolFrames.remove(e.getInternalFrame());
								}
							});
					toolFrame.setVisible(true);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
		}
	}
}