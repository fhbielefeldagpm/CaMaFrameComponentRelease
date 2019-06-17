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
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;

/**
 * <p>
 * Represents state SUSPENDED of {@link Stage}s and
 * {@link Task}s. Used in the context of an adapted state pattern. Implements
 * permissible transitions.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.5, 6.8 and 8.4.2 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public class StageTaskSuspended extends StageTaskState implements IStageTaskState {

	public StageTaskSuspended(CaseElement element) {
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
		String transition = "resume";
		CaseElement e = getElement();
		e.setState(e.getStateBeforeSuspend(), transition);

		if (e instanceof Stage) {
			Stage s = (Stage) e;
			if (s.getChildElements() != null) {
				if (s.getChildElements().size() > 0) {
					s.propagateParentResume();
				}
			}
		}
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
		// TODO distribute message

	}

	@Override
	public void parentSuspend() {
		// nothing
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
