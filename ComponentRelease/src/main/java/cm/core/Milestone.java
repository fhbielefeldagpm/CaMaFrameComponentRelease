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
package cm.core;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.listeners.EventMilestoneStates;
import cm.core.rules.RepetitionRule;
import cm.core.rules.RequiredRule;
import cm.core.rules.Rule;
import cm.core.states.EventMilestoneAvailable;
import cm.core.states.EventMilestoneCompleted;
import cm.core.states.EventMilestoneInitial;
import cm.core.states.EventMilestoneSuspended;
import cm.core.states.EventMilestoneTerminated;
import cm.core.states.IEventMilestoneState;
import cm.core.utils.RuleExpressionFactory;

/**
 * <p>Class representing Milestone elements in CMMN.</p>
 * <p>See CMMN 1.1 specification sections 5.4.3, 8.4.3 and 8.6 for more information.</p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("milestone")
public class Milestone extends CaseElement {
	
	// private boolean repetition; // made obsolete by rule impl
	@JohnzonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	private RepetitionRule repetitionRule;
	
	// private boolean required; // made obsolete by rule impl
	@JohnzonIgnore
	@ManyToOne(cascade=CascadeType.ALL)
	private RequiredRule requiredRule;
	@JohnzonIgnore
	@Transient
	private IEventMilestoneState contextState;

	private boolean evaluateRepetition() {
		if (this.repetitionRule != null) {
			return this.repetitionRule.evaluate();	
		} else {
			return false;
		}
	}
	/**
	 * <p>
	 * Returns <code>true</code> or <code>false</code> based on the evaluated
	 * {@link RepetitionRule}.</p>
	 * <p>
	 * Underlying {@link Rule} needs an implementation which is loaded in the
	 * configured {@link RuleExpressionFactory}.
	 * </p>
	 * 
	 * @return <code>true</code> if repeatable, or <code>false</code> if not or rule not set/found
	 */
	@JohnzonIgnore
	public boolean isRepeatable() {
		return this.evaluateRepetition();
	}

	public void setRepetitionRule(RepetitionRule repetitionRule) {
		this.repetitionRule = repetitionRule;
	}
	
	public RepetitionRule getRepetitionRule() {
		return this.repetitionRule;
	}
	
	public RequiredRule getRequiredRule() {
		return requiredRule;
	}

	public void setRequiredRule(RequiredRule requiredRule) {
		this.requiredRule = requiredRule;
	}

	private boolean evaluateRequired() {
		if(this.requiredRule != null) {
			return requiredRule.evaluate();
		} else {
			return false;
		}	
	}
	/**
	 * <p>
	 * Returns <code>true</code> or <code>false</code> based on the evaluated
	 * {@link RequiredRule}
	 * </p>
	 * <p>
	 * Underlying {@link Rule} needs an implementation which is loaded in the
	 * configured {@link RuleExpressionFactory}.
	 * </p>
	 * 
	 * @return <code>true</code> if required, or <code>false</code> if not
	 */
	@JohnzonIgnore
	public boolean isRequired() {
		return evaluateRequired();
	}
	
	public Milestone(){

	}	

	/**
	 * <p>
	 * Constructs a new Milestone with a first level relationship to a
	 * {@link CaseModel}. Automatically sets its state to INITIAL and the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the Milestone directly to a CaseModel. Its
	 * (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param id
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param caseRef
	 *            reference to the CaseModel the Milestone is being added to
	 */
	public Milestone(String id, String name, CaseModel caseRef) {
		super(id, name, caseRef);
		super.state = EventMilestoneStates.INITIAL.toString();
	}
	/**
	 * <p>
	 * Constructs a new Milestone with a second level relationship to a
	 * {@link CaseModel}. Automatically sets its state to INITIAL and the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the Milestone to a Stage. Its
	 * (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param id
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param parentStage
	 *            reference to the Stage the Milestone is being added to
	 */
	public Milestone(String id, String name, Stage parentStage) {
		super(id, name, parentStage);
		super.state = EventMilestoneStates.INITIAL.toString();
	}

	public Milestone(String id, String name) {
		super(id, name);
		super.state = EventMilestoneStates.INITIAL.toString();
	}
	/**
	 * <p>
	 * Returns an implementation of {@link IEventMilestoneState} based on the current
	 * state. The implementation offers all transitions, but only implements those
	 * permissible.
	 * </p>
	 * <p>
	 * Used as a state pattern adaptation. Each implementation captures permissible
	 * transitions from a state. See CMMN 1.1 specification section 8.4.3 for more
	 * information.
	 * </p>
	 * 
	 * @return an implementation of {@link IEventMilestoneState} based on the current
	 *         state, e.g. ACTIVE
	 * @see {@link Milestone#loadContextState()}
	 */
	public IEventMilestoneState getContextState() {
		loadContextState();
		return contextState;
	}

	public void setContextState(IEventMilestoneState contextState) {
		this.contextState = contextState;
	}

	@Override
	public void loadContextState() {
		if (state.equals(EventMilestoneStates.INITIAL.toString())) {
			setContextState(new EventMilestoneInitial(this));
		}
		else if (state.equals(EventMilestoneStates.AVAILABLE.toString())) {
			setContextState(new EventMilestoneAvailable(this));
		}
		else if (state.equals(EventMilestoneStates.SUSPENDED.toString())) {
			setContextState(new EventMilestoneSuspended(this));
		}
		else if (state.equals(EventMilestoneStates.COMPLETED.toString())) {
			setContextState(new EventMilestoneCompleted(this));
		}
		else if (state.equals(EventMilestoneStates.TERMINATED.toString())) {
			setContextState(new EventMilestoneTerminated(this));
		}
	}
}//end Milestone