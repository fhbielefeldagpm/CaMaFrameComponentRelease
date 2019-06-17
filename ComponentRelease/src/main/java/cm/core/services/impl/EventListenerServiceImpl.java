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
package cm.core.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import cm.core.CaseModel;
import cm.core.CaseWorker;
import cm.core.listeners.EventListener;
import cm.core.listeners.EventMilestoneStates;
import cm.core.services.EventListenerService;
import cm.core.services.ServiceMessage;
import cm.core.states.EventMilestoneTransitions;
import cm.core.utils.PersistenceSettings;

@Stateless
public class EventListenerServiceImpl implements EventListenerService {

	@PersistenceContext(unitName = PersistenceSettings.persistenceContextUnitName)
	private EntityManager em;

	@Override
	public List<EventListener> getAllEventListeners(CaseModel cm) {
		TypedQuery<EventListener> query = em
				.createQuery("SELECT el FROM EventListener el WHERE el.caseRef.id = :caseId", EventListener.class);
		long caseId = cm.getId();
		query.setParameter("caseId", caseId);
		List<EventListener> listeners = query.getResultList();
		return listeners;
	}

	@Override
	public List<EventListener> getAllAvailableEventListeners(CaseModel cm) {
		TypedQuery<EventListener> query = em.createQuery(
				"SELECT el FROM EventListener el WHERE el.caseRef.id = :caseId AND el.state = :state",
				EventListener.class);
		long caseId = cm.getId();
		query.setParameter("caseId", caseId);
		String state = EventMilestoneStates.AVAILABLE.toString();
		query.setParameter("state", state);
		List<EventListener> listeners = query.getResultList();
		return listeners;
	}

	@Override
	public ServiceMessage triggerEventListener(EventListener el, CaseWorker cw) {
		TypedQuery<EventListener> query = em.createQuery("SELECT el FROM EventListener el WHERE el.id =:elId",
				EventListener.class);
		long elId = el.getId();
		query.setParameter("elId", elId);
		List<EventListener> listeners = query.getResultList();
		EventListener fetchedEl = listeners.get(0);
		fetchedEl.getContextState().occur();
		return null;
	}

	@Override
	public ServiceMessage transitionEventListener(EventListener el, CaseWorker cw,
			EventMilestoneTransitions transition) {
		TypedQuery<EventListener> query = em.createQuery("SELECT el FROM EventListener el WHERE el.id =:elId",
				EventListener.class);
		long elId = el.getId();
		query.setParameter("elId", elId);
		List<EventListener> listeners = query.getResultList();
		EventListener fetchedEl = listeners.get(0);
		// refactor into transition commands, see cm.core.services.commands packages
		switch (transition) {
		case occur:
			fetchedEl.getContextState().occur();
		case suspend:
			fetchedEl.getContextState().suspend();
		case resume:
			fetchedEl.getContextState().resume();
		case terminate:
			fetchedEl.getContextState().terminate();
		case parentTerminate:
			fetchedEl.getContextState().parentTerminate();
		default:

		}
		em.merge(fetchedEl);
		return null;
	}

}
