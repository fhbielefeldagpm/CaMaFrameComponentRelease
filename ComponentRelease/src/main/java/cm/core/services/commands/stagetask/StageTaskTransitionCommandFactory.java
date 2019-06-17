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
package cm.core.services.commands.stagetask;

import cm.core.states.StageTaskTransitions;
import cm.core.tasks.Task;

/**
 * <p>
 * Provides {@link TaskTransitionCommand}s based on transitions from
 * {@link StageTaskTransitions}.
 * </p>
 * <p>
 * Used to implement the command pattern. Provides more control in the
 * individual command classes, for example to add logging.
 * </p>
 * 
 * @author André Zensen
 *
 */
public class StageTaskTransitionCommandFactory {

	public static TaskTransitionCommand getCommand(StageTaskTransitions transition, Task t) {
		switch (transition) {
//		case create:
//		case start:
		case enable:
			return new EnableCommand(t);
		case reEnable:
			return new ReEnableCommand(t);
		case disable:
			return new DisableCommand(t);
		case manualStart:
			return new ManualStartCommand(t);
		case resume:
			return new ResumeCommand(t);
		case reActivate:
			return new ReActivateCommand(t);
//		case fault:
		case suspend:
			return new SuspendCommand(t);
//		case parentSuspend:
//		case parentResume:
//		case exit:
		case terminate:
			return new TerminateCommand(t);
		case complete:
			return new CompleteCommand(t);
		default:
			return null;
		}
		// TODO send notifications in command?
	}

}
