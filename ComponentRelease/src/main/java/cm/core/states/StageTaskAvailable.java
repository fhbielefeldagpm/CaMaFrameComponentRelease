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
import cm.core.Stage;
import cm.core.tasks.CaseTask;
import cm.core.tasks.ProcessTask;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;
/**
 * <p>
 * Represents state AVAILABLE of {@link Stage}s and {@link Task}s. Used in the
 * context of an adapted state pattern. Implements permissible transitions.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.5, 6.8 and 8.4.2 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public class StageTaskAvailable extends StageTaskState implements IStageTaskState {

	public StageTaskAvailable(CaseElement element) {
		super(element);
	}

	@Override
	public void create() {
		// nothing
	}

	@Override
	public void start() {
		String transition = "start";
		CaseElement e = getElement();
		e.setState(TaskStates.ACTIVE.toString(), transition);
		if (e instanceof Stage) {
			((Stage) e).propagateParentActivate();
		} else if (e instanceof ProcessTask) {
			((ProcessTask) e).startProcess();
		} else if (e instanceof CaseTask) {
			((CaseTask) e).startCase();
		}

	}

	@Override
	public void enable() {
		String transition = "enable";

		getElement().setState(TaskStates.ENABLED.toString(), transition);

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
		// nothing

	}

	@Override
	public void reActivate() {
		// nothing

	}

	@Override
	public void suspend() {
		// nothing

	}

	@Override
	public void complete() {
		// nothing

	}

	@Override
	public void terminate() {
		// nothing

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
