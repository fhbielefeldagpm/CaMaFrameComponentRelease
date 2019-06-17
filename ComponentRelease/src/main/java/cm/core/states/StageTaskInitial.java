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
import cm.core.StageStates;
import cm.core.tasks.CaseTask;
import cm.core.tasks.ProcessTask;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;

/**
 * <p>
 * Represents newly introduced state INITIAL of {@link Stage}s and
 * {@link Task}s. Used in the context of an adapted state pattern. Implements
 * permissible transitions.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.5, 6.8 and 8.4.2 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public class StageTaskInitial extends StageTaskState implements IStageTaskState {

	public StageTaskInitial(CaseElement element) {
		super(element);
	}

	@Override
	public void create() {
		String transition = "create";
		CaseElement e = getElement();
		e.setState(TaskStates.AVAILABLE.toString(), transition);
		
		if (e.isEntrySentrySatisfied()) {
			if(e instanceof Task) {
				Task t = (Task) e;
				if(!t.isBlocking()) {
					transition = "complete";
					t.setState(TaskStates.COMPLETED.toString(), transition);
					if(t instanceof ProcessTask) {
						((ProcessTask) t).startProcess();
					} else if (t instanceof CaseTask) {
						((CaseTask) t).startCase();
					}
				} else if(t.isManualStart()) {
					transition = "enable";
					t.setState(TaskStates.ENABLED.toString(), transition);
				} else if (!t.isManualStart()) {
					transition = "start";
					t.setState(TaskStates.ACTIVE.toString(), transition);
					if(t instanceof ProcessTask) {
						((ProcessTask) t).startProcess();
					} else if (t instanceof CaseTask) {
						((CaseTask) t).startCase();
					}
				}
			} else if (e instanceof Stage) {
				Stage s = (Stage) e;
				if(s.isManualStart()) {
					transition = "enable";
					s.setState(StageStates.ENABLED.toString(), transition);
				} else if (!s.isManualStart()) {
					transition = "start";
					s.setState(StageStates.ACTIVE.toString(), transition);
					s.propagateParentActivate();
				}
			}
		}
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
		// nothing

	}

	@Override
	public void parentSuspend() {
		// nothing

	}

	@Override
	public void parentResume() {
		// nothing

	}

}
