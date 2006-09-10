package proc.param;

import java.lang.reflect.Constructor;

import proc.content.ContentEvent;
import proc.content.ContentEventDispatcher;
import proc.content.ContentListener;
import proc.content.DiscardingContentEventDispatcher;

/**
 * @author Nestor
 */
public class StepProcessParameter implements ContentEventDispatcher {

	// lazy creation
	private DiscardingContentEventDispatcher dispatcher = null;

	private int flags = 0;

	private Comparable highBound = null;

	private Comparable lowBound = null;

	private String name = null;

	private Object value = null;

	private Class valueClass = null;

	public StepProcessParameter(String name, Object value, Comparable lowBound,
			Comparable highBound) {
		super();
		this.value = value;
		this.valueClass = value.getClass();
		this.lowBound = lowBound;
		this.highBound = highBound;
		this.name = name;
	}

	public void addContentListener(ContentListener l) {
		if (dispatcher == null) {
			dispatcher = new DiscardingContentEventDispatcher();
		}
		dispatcher.addContentListener(l);
	}

	public void addContentListeners(ContentListener[] l) {
		if (dispatcher == null) {
			dispatcher = new DiscardingContentEventDispatcher();
		}
		dispatcher.addContentListeners(l);
	}

	public void fireContentsChanged(ContentEvent ev) {
		if (dispatcher == null) {
			dispatcher = new DiscardingContentEventDispatcher();
		}
		dispatcher.fireContentsChanged(ev);
	}

	public ContentListener[] getContentListeners() {
		if (dispatcher == null) {
			dispatcher = new DiscardingContentEventDispatcher();
		}
		return dispatcher.getContentListeners();
	}

	public int getFlags() {
		return flags;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public void removeAll() {
		dispatcher.removeAll();
	}

	public void removeContentListener(ContentListener l) {
		if (dispatcher == null) {
			dispatcher = new DiscardingContentEventDispatcher();
		}
		dispatcher.removeContentListener(l);
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void setValue(String s) {
		try {
			Constructor c = valueClass
					.getConstructor(new Class[] { String.class });
			Object newValue = (Object) c.newInstance(new Object[] { s });
			if (lowBound != null) {
				if (lowBound.compareTo(newValue) > 0) {
					throw new Exception("value out of bounds");
				}
			}
			if (highBound != null) {
				if (highBound.compareTo(newValue) < 0) {
					throw new Exception("value out of bounds");
				}
			}
			value = newValue;
			fireContentsChanged(new ContentEvent(this));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}