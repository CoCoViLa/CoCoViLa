package ee.ioc.cs.vsle.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventContainer {

  private List<BaseEventListener> termListeners;

  EventContainer() {
    termListeners = new CopyOnWriteArrayList<BaseEventListener>();
  }

  public void add(BaseEventListener lst) {
    termListeners.add(lst);
  }

  public void traverseAll(BaseEvent event, EventDispatcher dispatcher) {
    for (BaseEventListener lst : termListeners) {
      dispatcher.tryToCallListenerOnEvent(lst, event);
    }
  }

  public boolean delete(BaseEventListener listener) {
    return termListeners.remove(listener);
  }

}
