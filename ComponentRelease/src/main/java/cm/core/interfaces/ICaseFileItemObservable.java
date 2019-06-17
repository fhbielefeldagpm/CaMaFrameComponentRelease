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

/**
 * <p>
 * Provides an interface to register and unregister {@link CaseFileItemOnPart}
 * observers with a {@link CaseFileItem}.
 * </p>
 * <p>
 * Used to implement the observer pattern adapted for {@link Sentry} mechanisms.
 * </p>
 * 
 * @author André Zensen
 *
 */
public interface ICaseFileItemObservable {

	public String getState();

	public void registerCaseFileItemObserver(CaseFileItemOnPart o);

	public void unregisterCaseFileItemObserver(CaseFileItemOnPart o);
}