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

import cm.core.data.CaseFileItem;
import cm.core.interfaces.ICaseFileItemObserver;
import cm.core.states.CaseFileItemTransition;

/**
 * <p>
 * Observes state changes of {@link CaseFileItem}s and links them to
 * {@link Sentry}s. Sentrys are notified when the required transition has taken
 * place.
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
@DiscriminatorValue("casefileitem")
public class CaseFileItemOnPart extends OnPart implements ICaseFileItemObserver {

	// @ManyToOne(cascade = CascadeType.ALL)
	// public CaseFileItem fileItemRef; // observed case file item
	public String requiredTransition; // required state of the observed case file item

	public CaseFileItemOnPart() {

	}

	/**
	 * <p>
	 * Constructs a new CaseFileItemOnPart with references to the Sentry it belongs
	 * to and the CaseFileItem it is observing. Automatically registers itself with
	 * the given Sentry and CaseFileItem.
	 * </p>
	 * 
	 * @param sentryRef
	 *            the Sentry this CaseFileItemOnPart belongs to and notifies
	 * @param caseFileItem
	 *            the CaseFileItem being observed
	 * @param requiredTransition
	 *            the transition it listens for, i.e. from
	 *            {@link CaseFileItemTransition}
	 */
	public CaseFileItemOnPart(Sentry sentryRef, CaseFileItem caseFileItem, String requiredTransition) {
		this.sentryRef = sentryRef;
		this.sentryRef.addOnPart(this);
		caseFileItem.registerCaseFileItemObserver(this);
		this.requiredTransition = requiredTransition;
	}

	public String getRequiredTransition() {
		return requiredTransition;
	}

	public void setRequiredTransition(String requiredTransition) {
		this.requiredTransition = requiredTransition;
	}

	public void updateCaseFileItemObserver(String transition) {
		if (transition.equals(this.requiredTransition)) {
			this.setSatisfied(true);
			this.sentryRef.checkCriteria();
		} else {
			this.setSatisfied(false);
		}

	}
}