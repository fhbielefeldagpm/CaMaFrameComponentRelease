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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import cm.core.data.CaseFileItem;
import cm.core.rules.ManualActivationRule;
import cm.core.rules.RepetitionRule;
import cm.core.rules.RequiredRule;
import cm.core.rules.Rule;
import cm.core.rules.RuleExpression;
import cm.core.utils.IfPartImplementationFactory;

/**
 * <p>
 * Links a Sentry to the implementation of an IfPart. Can be linked to a
 * CaseFileItem. The actual implementation is based on class
 * {@link IfPartImplementation} which is provided by
 * {@link IfPartImplementationFactory} based on the cmId of this IfPart. Base
 * class for creating a named IfPart reference to a {@link CaseFileItem}.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.6, 8.5 and 8.6 for more information.
 * </p>
 * 
 * @author André Zensen
 * @see {@link IfPartImplementation}, {@link IfPartImplementationFactory}
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class IfPart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	protected String cmId;
	@OneToOne
	private Sentry sentryRef;
	@ManyToOne
	private CaseFileItem caseFileItemRef;
	protected boolean satisfied;

	@Transient
	IfPartImplementation ifpImpl;

	public IfPart() {

	}

	/**
	 * Constructs a new IfPart. Its cmId is used by
	 * {@link IfPartImplementationFactory} to provide the case specific
	 * implementation of this IfPart at runtime.
	 * 
	 * @param cmId
	 *            the cmId used to provide the actual implementation, can be based
	 *            on a .CMMN file mark-up
	 * @param sentryRef
	 *            the referenced Sentry, used for bidirectional navigations
	 * @param caseFileItemRef
	 *            the referenced CaseFileItem as a basis for evaluations
	 */
	public IfPart(String cmId, Sentry sentryRef, CaseFileItem caseFileItemRef) {
		super();
		this.cmId = cmId;
		this.sentryRef = sentryRef; // TODO check if can be removed
		this.sentryRef.setIfPart(this);
		this.caseFileItemRef = caseFileItemRef;
	}

	public Sentry getSentryRef() {
		return sentryRef;
	}

	public void setSentryRef(Sentry sentryRef) {
		this.sentryRef = sentryRef;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCmId() {
		return cmId;
	}

	public void setCmId(String cmId) {
		this.cmId = cmId;
	}

	public CaseFileItem getCaseFileItemRef() {
		return caseFileItemRef;
	}

	public void setCaseFileItemRef(CaseFileItem caseFileItemRef) {
		this.caseFileItemRef = caseFileItemRef;
	}

	/**
	 * Evaluates whether or not this IfPart is satisfied by first getting the actual
	 * implementation based on its cmId. The {@link IfPartImplementation} is then
	 * evaluated.
	 * 
	 * @return <code>true</code> if satisfied, or <code>false</code> if not
	 */
	public boolean isSatisfied() {
		boolean ifPartResponse = false;
		this.ifpImpl = IfPartImplementationFactory.getIfPartImplementation(this);
		ifPartResponse = ifpImpl.isSatisfied();
		this.satisfied = ifPartResponse;
		return ifPartResponse;
	};

	/**
	 * Triggers the referenced Sentry when it is satisfied.
	 * 
	 * @param satisfied
	 */
	public void setSatisfied(boolean satisfied) {// TODO check if can be removed
		this.satisfied = satisfied;
		if (this.satisfied) {
			this.sentryRef.checkCriteria();
		}
	}

}