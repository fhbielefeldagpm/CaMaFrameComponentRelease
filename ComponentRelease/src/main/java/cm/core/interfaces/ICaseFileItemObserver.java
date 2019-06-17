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
package cm.core.interfaces;

import cm.core.data.CaseFileItem;
import cm.core.sentries.CaseFileItemOnPart;
import cm.core.sentries.Sentry;
import cm.core.states.CaseFileItemTransition;

/**
 * <p>
 * Provides an interface to notify {@link CaseFileItemOnPart} observers of a
 * state change of a {@link CaseFileItem}.
 * </p>
 * <p>
 * Used to implement the observer pattern adapted for {@link Sentry} mechanisms.
 * </p>
 * 
 * @author André Zensen
 *
 */
public interface ICaseFileItemObserver {
	/**
	 * Compares the given transition with the one it is listening for. Notifies any
	 * observers of the state transition of the observed CaseFileItem. Triggers the
	 * referenced Sentry to check if it is satisfied.
	 * 
	 * @param transition
	 *            the transition as a String, i.e. one of
	 *            {@link CaseFileItemTransition}
	 */
	public void updateCaseFileItemObserver(String transition);
}