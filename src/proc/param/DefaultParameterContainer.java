package proc.param;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;


/**
 * @author Nestor
 */
public class DefaultParameterContainer implements ParameterContainer {

    private HashMap parameters = new HashMap();

    public void parseObjectForParameters(Object o) {
		Field[] flds = o.getClass().getFields();
		for (int i = 0; i < flds.length; i++) {
			if (flds[i].getType().equals(StepProcessParameter.class)) {
				try {
					Object obj = flds[i].get(this);
					if (obj instanceof StepProcessParameter) {
						addParameter((StepProcessParameter) obj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
    }

    public void addParameter(StepProcessParameter param) {
        parameters.put(param.getName(), param);
    }

    public StepProcessParameter getParameter(String name) {
        return (StepProcessParameter) parameters.get(name);
    }

    public int getParameterCount() {
        return parameters.size();
    }

    public Collection getParameters() {
        return parameters.values();
    }

}