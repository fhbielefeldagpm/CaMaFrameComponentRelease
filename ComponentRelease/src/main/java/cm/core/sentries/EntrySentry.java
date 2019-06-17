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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import cm.core.CaseModel;
import cm.core.CaseElement;
import cm.core.data.CaseFileItem;
import cm.core.listeners.EventListener;

/**
 * <p>
 * 
 * Observes state changes of {@link CaseElement}s or {@link CaseFileItem}s via its
 * {@link OnPart}s. Sentrys are notified when the required transition has taken
 * place and checks its OnParts and its (optional) {@link IfPart}. Once
 * satisfied, the Element it is attached to transitions to state ENABLED/ACTIVE
 * (Stages and Tasks) or COMPLETED (Milestones).
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.6 and 8.5 for more information.
 * </p>
 * 
 * @author André Zensen
 * @see {@link Sentry}
 */
@Entity
@DiscriminatorValue("entry")
public class EntrySentry extends Sentry {

	public EntrySentry() {

	}
	/**
	 * Constructs a new EntrySentry. Automatically registers with the Element it is attached to.
	 * @param cmId			the cmId which can be based on a .CMMN file mark-up
	 * @param name			an optional human-readable name to identify it
	 * @param elementRef	reference to the Element it is attached to
	 * @throws Error 		if a CaseModel or EventListener is the referenced Element
	 */
	public EntrySentry(String id, String name, CaseElement elementRef) {
		// TODO restrict to permissible elements, e.g. no CaseModel
		if (elementRef instanceof CaseModel || elementRef instanceof EventListener) {
			throw new Error("CaseModels/EventListeners cannot have EntrySentries.");
		}
		super.setCmId(id);
		super.setName(name);
		super.setElementRef(elementRef);
		elementRef.addSentryRef(this);
	}
}