package ee.ioc.cs.vsle.event;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
