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
package cm.core.listeners;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.CaseModel;
import cm.core.CaseElement;
import cm.core.Stage;
import cm.core.states.EventMilestoneAvailable;
import cm.core.states.EventMilestoneCompleted;
import cm.core.states.EventMilestoneInitial;
import cm.core.states.EventMilestoneSuspended;
import cm.core.states.EventMilestoneTerminated;
import cm.core.states.IEventMilestoneState;
/**
 * <p>
 * Class representing base EventListener elements in CMMN. Specializations TimerEventListener or UserEventListener not yet implemented.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.2 and 8.4.3 for more
 * information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("event_listener")
public class EventListener extends CaseElement {
	@JohnzonIgnore
	@Transient
	private IEventMilestoneState contextState;

	public EventListener(String id, String name, CaseModel caseRef) {
		super(id, name, caseRef);
		super.state = EventMilestoneStates.INITIAL.toString();
	}
	
	public EventListener(String id, String name, Stage parentStage) {
		super(id, name, parentStage);
		super.state = EventMilestoneStates.INITIAL.toString();
	}
	
	public EventListener() {
		
	}
	/**
	 * <p>
	 * Returns an implementation of {@link IEventMilestoneState} based on the current
	 * state. The implementation offers all transitions, but only implements those
	 * permissible.
	 * </p>
	 * <p>
	 * Used as a state pattern adaptation. Each implementation captures permissible
	 * transitions from a state. See CMMN 1.1 specification section 8.4.3 for more
	 * information.
	 * </p>
	 * 
	 * @return an implementation of {@link IEventMilestoneState} based on the current
	 *         state, e.g. AVAILABLE
	 * @see {@link EventListener#loadContextState()}
	 */
	public IEventMilestoneState getContextState() {
		loadContextState();
		return contextState;
	}

	public void setContextState(IEventMilestoneState contextState) {
		this.contextState = contextState;
	}

	@Override
	public void loadContextState() {
		if (state.equals(EventMilestoneStates.INITIAL.toString())) {
			setContextState(new EventMilestoneInitial(this));
		}
		else if (state.equals(EventMilestoneStates.AVAILABLE.toString())) {
			setContextState(new EventMilestoneAvailable(this));
		}
		else if (state.equals(EventMilestoneStates.SUSPENDED.toString())) {
			setContextState(new EventMilestoneSuspended(this));
		}
		else if (state.equals(EventMilestoneStates.COMPLETED.toString())) {
			setContextState(new EventMilestoneCompleted(this));
		}
		else if (state.equals(EventMilestoneStates.TERMINATED.toString())) {
			setContextState(new EventMilestoneTerminated(this));
		}
	}
}