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
package cm.core.services.commands.casefileitem;

import cm.core.data.CaseFileItem;

/**
 * <p>
 * Encapsulates the <code>create</code> transition of {@link CaseFileItem}s as a
 * command. Executed by {@link CaseFileItemTransitionController}.
 * </p>
 * <p>
 * Used to implement the command pattern. Provides more control in the
 * individual command classes, for example to add logging in the overridden
 * method.
 * </p>
 * 
 * @author André Zensen
 *
 */
public class CreateCommand implements CaseFileItemTransitionCommand {

	private CaseFileItem cfi;

	public CreateCommand(CaseFileItem cfi) {
		this.cfi = cfi;
	}

	@Override
	public void execute() {
		this.cfi.getContextState().create();
	}

}
