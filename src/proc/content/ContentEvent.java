package proc.content;

import java.util.EventObject;


/**
 * @author Nestor
 */
public class ContentEvent extends EventObject {

    private Object contents = null;

    public ContentEvent(Object source) {
        super(source);
        contents = source;
    }

    public Object getContents() {
        return contents;
    }
}