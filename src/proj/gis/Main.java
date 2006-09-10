package proj.gis;

import java.awt.Frame;

import javax.swing.WindowConstants;

import proc.StepProcessInitializator;
import proc.ui.StepProcessMainFrame;
import proj.gis.proc.GraphModelA;
import proj.gis.proc.GraphModelB;
import proj.gis.proc.GraphModelER;
import proj.gis.proc.SFModelBA;

/**
 * @author Nestor
 */
public class Main {

	public static void main(String[] args) {
		StepProcessMainFrame f = new StepProcessMainFrame("Projekt GIS",
				new StepProcessInitializator[] { new SFModelBA(),
						new GraphModelA(), new GraphModelB(),
						new GraphModelER() });
		f.setVisible(true);
		f.setExtendedState(Frame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}