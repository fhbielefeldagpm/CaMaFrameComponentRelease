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

import cm.core.CaseElement;
import cm.core.Milestone;
import cm.core.Stage;
import cm.core.StageStates;
import cm.core.listeners.EventListener;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;

/**
 * <p>
 * Represents state ACTIVE of {@link Stage}s and {@link Task}s. Used in the
 * context of an adapted state pattern. Implements permissible transitions.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.5, 6.8 and 8.4.2 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public class StageTaskActive extends StageTaskState implements IStageTaskState {

	public StageTaskActive(CaseElement element) {
		super(element);
	}

	@Override
	public void create() {
		// nothing

	}

	@Override
	public void start() {
		// nothing

	}

	@Override
	public void enable() {
		// nothing

	}

	@Override
	public void manualStart() {
		// nothing

	}

	@Override
	public void reEnable() {
		// nothing

	}

	@Override
	public void disable() {
		// nothing

	}

	@Override
	public void resume() {
		// nothing

	}

	@Override
	public void fault() {
		String transition = "fault";
		getElement().setState(TaskStates.FAILED.toString(), transition);

	}

	@Override
	public void reActivate() {
		// nothing

	}

	@Override
	public void suspend() {
		String transition = "suspend";
		String currentState = getElement().getState();
		CaseElement e = getElement();
		e.setStateBeforeSuspend(currentState);
		e.setState(TaskStates.SUSPENDED.toString(), transition);
		e.setSuspended(true);
		// if (e.getChildElements() != null) {
		// for (Element child : e.getChildElements().values()) {
		// if(child instanceof EventListener) {
		// ((EventListener) child).getContextState().suspend();
		// } else if(child instanceof Milestone) {
		// ((Milestone) child).getContextState().suspend();
		// } else if(child instanceof Stage) {
		// ((Stage) child).getContextState().parentSuspend();
		// } else if (child instanceof Task) {
		// ((Task) child).getContextState().parentSuspend();
		// }
		// }
		// }
		if (e instanceof Stage) {
			Stage s = (Stage) e;
			if (s.getChildElements() != null) {
				if (s.getChildElements().size() > 0) {
					for (CaseElement child : s.getChildElements()) {
						if (child instanceof EventListener) {
							((EventListener) child).getContextState().suspend();
						} else if (child instanceof Milestone) {
							((Milestone) child).getContextState().suspend();
						} else if (child instanceof Stage) {
							((Stage) child).getContextState().parentSuspend();
						} else if (child instanceof Task) {
							((Task) child).getContextState().parentSuspend();
						}
					}
				}
			}
		}

	}

	@Override
	public void complete() {
		String transition = "complete";
		CaseElement elem = getElement();
		elem.setState(TaskStates.COMPLETED.toString(), transition);

		// for repetition evaluate expression via rule
		if (elem instanceof Stage) {
			Stage element = (Stage) elem;
			boolean elementIsRepeatable = element.isRepetition();
			if (elementIsRepeatable) {
				int currRep = element.getCurrentRepetition();
				int maxRep = element.getMaxRepetitions();
				if (currRep < maxRep) {
					element.setState(StageStates.INITIAL.toString(), StageTaskTransitions.create.toString());
					element.getContextState().create();
					element.setCurrentRepetition(currRep + 1);
				}
				// TODO implement stage behaviour
			}
		} else if (elem instanceof Task) {
			Task element = (Task) elem;
			boolean elementIsRepeatable = element.isRepeatable();
			if (elementIsRepeatable) {
				// TODO clone instance
				System.out.println("New instance would be created here.");
			}
		}

	}

	@Override
	public void terminate() {
		String transition = "terminate";
		getElement().setState(TaskStates.TERMINATED.toString(), transition);

	}

	@Override
	public void exit() {
		String transition = "exit";
		getElement().setState(TaskStates.TERMINATED.toString(), transition);

	}

	@Override
	public void parentSuspend() {
		String transition = "parentSuspend";
		CaseElement e = getElement();
		e.setStateBeforeSuspend(e.getState());
		e.setState(TaskStates.SUSPENDED.toString(), transition);
		e.setSuspended(true);

		if (e instanceof Stage) {
			Stage s = (Stage) e;
			if (s.getChildElements() != null) {
				if (s.getChildElements().size() > 0) {
					s.propagateParentSuspend();
				}
			}
		}

	}

	@Override
	public void parentResume() {
		String transition = "parentResume";
		CaseElement e = getElement();
		e.setState(e.getStateBeforeSuspend(), transition);
		e.setSuspended(false);

		if (e instanceof Stage) {
			Stage s = (Stage) e;
			if (s.getChildElements() != null) {
				if (s.getChildElements().size() > 0) {
					s.propagateParentResume();
				}
			}
		}
	}

}
