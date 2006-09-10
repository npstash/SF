package proc.content;

/**
 * @author Nestor
 */
public interface ContentEventDispatcher {

    public void addContentListener(ContentListener l);

    public void addContentListeners(ContentListener[] l);

    public void fireContentsChanged(ContentEvent ev);

    public ContentListener[] getContentListeners();

    public void removeAll();

    public void removeContentListener(ContentListener l);
}
