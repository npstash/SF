package proc.content;

import java.util.EventListener;

/**
 * @author Nestor
 */
public interface ContentListener extends EventListener {

    public void contentsChanged(ContentEvent e);

}