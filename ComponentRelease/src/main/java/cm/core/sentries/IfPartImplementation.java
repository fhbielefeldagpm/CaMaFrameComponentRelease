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
package cm.core.sentries;

import cm.core.data.CaseFileItem;
import cm.core.utils.IfPartImplementationFactory;

/**
 * <p>
 * Base class for case specific implementations of {@link IfPart}s of
 * {@link Sentry}s. Implementations are provided by the
 * {@link IfPartImplementationFactory} during runtime and can make use of a
 * referenced {@link CaseFileItem} for evaluation.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.6 and 8.5 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public abstract class IfPartImplementation {

	protected IfPart ip;

	/**
	 * Constructs a new IfPartImplementation with a reference of the IfPart linking
	 * it to a Sentry and CaseFileItem.
	 * 
	 * @param ip
	 *            the IfPart link
	 */
	public IfPartImplementation(IfPart ip) {
		this.ip = ip;
	}

	/**
	 * Implements the evaluation of whether or not this IfPart is satisfied.
	 * 
	 * @return <code>true</code> if the evaluated context is satisfactory, or
	 *         <code>false</code> if the evaluation is negative
	 */
	public abstract boolean isSatisfied();

}
