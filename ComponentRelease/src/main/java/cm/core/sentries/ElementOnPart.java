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

import cm.core.CaseElement;
import cm.core.interfaces.IElementObserver;
import cm.core.states.StageTaskTransitions;

/**
 * <p>
 * Observes state changes of {@link CaseElement}s and links them to {@link Sentry}s.
 * Sentrys are notified when the required transition has taken place.
 * </p>
 * <p>
 * Represents the CMMN PlanItemOnPart. See CMMN 1.1 specification sections 5.4.6
 * and 8.5 for more information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("element")
public class ElementOnPart extends OnPart implements IElementObserver {

	public String requiredTransition;

	/**
	 * <p>
	 * Constructs a new ElementOnPart with references to the Sentry it belongs to
	 * and the Element it is observing. Automatically registers itself with the
	 * given Sentry and Element.
	 * </p>
	 * 
	 * @param sentryRef
	 *            the Sentry this ElementOnPart belongs to and notifies
	 * @param elementRef
	 *            the Element being observed
	 * @param requiredTransition
	 *            the transition it listens for, e.g. from
	 *            {@link StageTaskTransitions}
	 */
	public ElementOnPart(Sentry sentryRef, CaseElement elementRef, String requiredTransition) {
		this.sentryRef = sentryRef;
		this.sentryRef.addOnPart(this);
		elementRef.registerElementObserver(this);
		this.requiredTransition = requiredTransition;
	}

	public ElementOnPart() {

	}

	public void updateElementObserver(String transition) {
		if (transition.equals(this.requiredTransition)) {
			this.setSatisfied(true);
			this.sentryRef.checkCriteria();
		} else
			this.setSatisfied(false);
	}
}