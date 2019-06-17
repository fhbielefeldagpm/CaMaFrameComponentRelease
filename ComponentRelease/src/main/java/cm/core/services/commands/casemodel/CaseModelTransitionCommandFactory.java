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
package cm.core.services.commands.casemodel;

import cm.core.CaseModel;
import cm.core.states.CaseInstanceTransition;

/**
 * <p>
 * Provides {@link CaseModelTransitionCommand}s based on transitions from
 * {@link CaseInstanceTransition}.
 * </p>
 * <p>
 * Used to implement the command pattern. Provides more control in the
 * individual command classes, for example to add logging.
 * </p>
 * 
 * @author André Zensen
 *
 */
public class CaseModelTransitionCommandFactory {

	public static CaseModelTransitionCommand getCommand(CaseInstanceTransition transition, CaseModel cm) {
		switch (transition) {
		// case create:
		// return new CreateCommand(cm);
		case complete:
			return new CompleteCommand(cm);
		case terminate:
			return new TerminateCommand(cm);
		// case fault:
		// return new FaultCommand(cm);
		case suspend:
			return new SuspendCommand(cm);
		case close:
			return new CloseCommand(cm);
		case reActivate:
			return new ReActivateCommand(cm);
		default:
			return null;
		}
	}
}
