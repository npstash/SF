package proc.param;

import java.util.Collection;


/**
 * @author Nestor
 */
public interface ParameterContainer {
    
    public void addParameter(StepProcessParameter param);

    public StepProcessParameter getParameter(String name);

    public int getParameterCount();

    public Collection getParameters();

}
