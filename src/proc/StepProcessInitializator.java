package proc;

import java.util.Collection;

/**
 * @author Nestor
 */
public interface StepProcessInitializator {

    public StepProcess newInstance();
    
    public Collection getToolsClasses();
    
    public String[] getToolsNames();

    public String getName();
}
