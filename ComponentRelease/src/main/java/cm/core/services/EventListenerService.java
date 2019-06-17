/*
 * Copyright © 2018-2019 André Zensen, University of Applied Sciences Bielefeld
 * and various authors (see https://www.fh-bielefeld.de/wug/forschung/ag-pm)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cm.core.services;

import java.util.List;

import cm.core.CaseModel;
import cm.core.CaseWorker;
import cm.core.listeners.EventListener;
import cm.core.states.EventMilestoneTransitions;

/**
 * <p>
 * Provides interfaces to retrieve, trigger and transition {@link EventListener}s.
 * </p>
 * <p>
 * Implementations use an EntityManager and PersistenceContext.
 * </p>
 * 
 * @author André Zensen
 *
 */
public interface EventListenerService {

	public List<EventListener> getAllEventListeners(CaseModel cm);
	
	public List<EventListener> getAllAvailableEventListeners(CaseModel cm);
	/**
	 * Triggers an {@link EventListener} by transitioning it with transition <code>occur</code>.
	 * @param el	the EventListener to trigger
	 * @param cw	a CaseWorker who is authorized to trigger the EventListener
	 * @return		a ServiceMessage
	 */
	public ServiceMessage triggerEventListener(EventListener el, CaseWorker cw);
	
	public ServiceMessage transitionEventListener(EventListener el, CaseWorker cw, EventMilestoneTransitions transition);
	
}
