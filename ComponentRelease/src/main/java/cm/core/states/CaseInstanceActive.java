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
package cm.core.states;

import cm.core.CaseStates;
import cm.core.CaseElement;
import cm.core.Milestone;
import cm.core.Stage;
import cm.core.CaseModel;
import cm.core.listeners.EventListener;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;

/**
 * <p>
 * Represents state ACTIVE of {@link CaseModel}s. Used in the context of an
 * adapted state pattern. Implements permissible transitions.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.2, 6.3 and 8.2 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public class CaseInstanceActive extends CaseInstanceState implements ICaseInstanceState {

	public CaseInstanceActive(CaseModel element) {
		super(element);
	}

	// TODO Throw error / Message for logger for states not applicable
	@Override
	public void create() {

		// not possible
	}

	@Override
	public void complete() {
		/*
		 * Required milestone, stage and task instances are CLOSED, TERMINATED,
		 * COMPLETED, DISABLED, FAILED AND no ACTIVE stage or task instances
		 */
		String transition = "complete";

		boolean canComplete = true;
		for (CaseElement child : ((CaseModel) getElement()).getChildElements()) {
			String childState = child.getState();
			if (child instanceof EventListener) {
				// ignore
			} else {
				if (childState.equals(TaskStates.ACTIVE.toString())) {
					canComplete = false;
					break;
				} else if (!(childState.equals(CaseStates.CLOSED.toString())
						|| childState.equals(TaskStates.TERMINATED.toString())
						|| childState.equals(TaskStates.COMPLETED.toString())
						|| childState.equals(TaskStates.DISABLED.toString())
						|| childState.equals(TaskStates.FAILED.toString())) && elementRequired(child)) {
					canComplete = false;
					break;
				}
			}
		}

		if (canComplete) {
			// getElement().propagateStateToChildren(CaseState.COMPLETED.toString(),
			// getElement());
			getElement().setState(CaseStates.COMPLETED.toString(), transition);
			if (getElement().getCaseTaskRef() != null) {
				getElement().getCaseTaskRef().getContextState().complete();
			}
		} else {
			// TODO Throw error / Message for logger
		}

	}

	private boolean elementRequired(CaseElement e) {
		if (e instanceof Stage) {
			return ((Stage) e).isRequired();
		} else if (e instanceof Task) {
			return ((Task) e).isRequired();
		} else if (e instanceof Milestone) {
			return ((Milestone) e).isRequired();
		}
		return false;
	}

	@Override
	public void terminate() {
		String transition = "terminate";
		getElement().setState(CaseStates.TERMINATED.toString(), transition);
		getElement().propagateSuspend();
		// TODO unscharf in Spezifikation
		/*
		 * was genau durchschlagen soll an die untergeordneten Elemente re-activate ist
		 * für CasePlanModel verfügbar, aber nicht für Stages/Tasks/Milestones/Events
		 * daher wird suspended gewählt und bei reactivate aus Zustand terminated parent
		 * resume ausgelöst
		 */
		if (getElement().getCaseTaskRef() != null) {
			getElement().getCaseTaskRef().getContextState().complete();
		}
	}

	@Override
	public void fault() {
		String transition = "fault";
		getElement().setState(CaseStates.FAILED.toString(), transition);

	}

	@Override
	public void suspend() {
		String transition = "suspend";
		getElement().setState(CaseStates.SUSPENDED.toString(), transition);
		getElement().propagateSuspend();
	}

	@Override
	public void reActivate() {

		// not possible
	}

	@Override
	public void close() {

		// not possible
	}

}
