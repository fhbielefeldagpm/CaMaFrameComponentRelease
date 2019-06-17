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
package cm.core.tasks;

import cm.core.utils.ProcessTaskImplementationFactory;

/**
 * <p>
 * Base class for case specific implementations of {@link ProcessTask}s.
 * Implementations are provided by the {@link ProcessTaskImplementationFactory}
 * during runtime using the referenced {@link ProcessTask}s cmId.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.6 and 8.5 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public abstract class ProcessTaskImplementation {

	protected ProcessTask processTask;
/**
 * Constructs a new ProcessTaskImplementation with a reference to its ProcessTask element.
 * @param processTask	the ProcessTask this implementation belongs to
 */
	public ProcessTaskImplementation(ProcessTask processTask) {
		this.processTask = processTask;
	}

	public abstract void startProcess();

	public abstract void executeCallBack();

}
