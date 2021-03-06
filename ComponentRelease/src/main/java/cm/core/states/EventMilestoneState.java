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
import cm.core.Milestone;
import cm.core.listeners.EventListener;

/**
 * <p>
 * Base class for states of {@link EventListener}s and {@link Milestone}s. Used
 * in the context of an adapted state pattern. Each specialization implements
 * permissible transitions.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.9, 6.10 and 8.4.3 for more information.
 * </p>
 * 
 * @author André Zensen
 */
public abstract class EventMilestoneState {
	private CaseElement element;

	public EventMilestoneState(CaseElement element) {
		this.element = element;
	};

	public CaseElement getElement() {
		return element;
	}

	public void setElement(CaseElement element) {
		this.element = element;
	}
}
