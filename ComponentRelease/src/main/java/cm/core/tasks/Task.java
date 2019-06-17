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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.CaseModel;
import cm.core.CaseRole;
import cm.core.CaseWorker;
import cm.core.CaseElement;
import cm.core.Stage;
import cm.core.StageStates;
import cm.core.rules.ManualActivationRule;
import cm.core.rules.RepetitionRule;
import cm.core.rules.RequiredRule;
import cm.core.rules.Rule;
import cm.core.states.IStageTaskState;
import cm.core.states.StageTaskActive;
import cm.core.states.StageTaskAvailable;
import cm.core.states.StageTaskCompleted;
import cm.core.states.StageTaskDisabled;
import cm.core.states.StageTaskEnabled;
import cm.core.states.StageTaskFailed;
import cm.core.states.StageTaskInitial;
import cm.core.states.StageTaskSuspended;
import cm.core.states.StageTaskTerminated;
import cm.core.utils.RuleExpressionFactory;

/**
 * <p>
 * Base class for Task specializations such as {@link HumanTask}. References
 * {@link Rule}s and its current {@link CaseWorker} who claimed the task. Access
 * can be restricted via a {@link CaseRole}.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 6.8 and 8.4.2 for more information.
 * </p>
 * 
 * @author André Zensen
 * @see {@link CaseWorker}, {@link CaseRole}, {@link ManualActivationRule},
 *      {@link RepetitionRule} and {@link RequiredRule}
 */
//@Entity
@MappedSuperclass
public abstract class Task extends CaseElement implements Cloneable {

	// private boolean required; // made obsolete by rule impl
	@JohnzonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	private RequiredRule requiredRule;

	// private boolean repetition; // made obsolete by rule impl
	@JohnzonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	private RepetitionRule repetitionRule;

	// private boolean manualStart; // made obsolete by rule impl
	@JohnzonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
	private ManualActivationRule manualActivationRule;
	@JohnzonIgnore
	@ManyToOne(cascade = CascadeType.MERGE)
	// @JoinColumn(name="CLAIMANT_ID")
	private CaseWorker claimant;

	private String description;
	@JohnzonIgnore
	private boolean blocking;
	@JohnzonIgnore
	@ManyToOne
	private CaseRole caseRole;
	@JohnzonIgnore
	@Transient
	private IStageTaskState contextState;

	public RequiredRule getRequiredRule() {
		return requiredRule;
	}

	public void setRequiredRule(RequiredRule requiredRule) {
		this.requiredRule = requiredRule;
	}

	private boolean evaluateRequired() {
		return this.requiredRule.evaluate();
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
	 * @return <code>true</code> if required, or <code>false</code> if not or rule
	 *         not set/found
	 */
	@JohnzonIgnore
	public boolean isRequired() {
		if (this.requiredRule != null) {
			return evaluateRequired();
		} else {
			return false;
		}
	}

	// private boolean repetition; // made obsolete by rule impl

	private boolean evaluateRepetition() {
		return this.repetitionRule.evaluate();
	}

	/**
	 * <p>
	 * Returns <code>true</code> or <code>false</code> based on the evaluated
	 * {@link RepetitionRule}.
	 * </p>
	 * <p>
	 * Underlying {@link Rule} needs an implementation which is loaded in the
	 * configured {@link RuleExpressionFactory}.
	 * </p>
	 * 
	 * @return <code>true</code> if repeatable, or <code>false</code> if not or rule
	 *         not set/found
	 */
	@JohnzonIgnore
	public boolean isRepeatable() {
		if (this.repetitionRule != null) {
			return evaluateRepetition();
		} else {
			return false;
		}
	}

	public void setRepetitionRule(RepetitionRule repetitionRule) {
		this.repetitionRule = repetitionRule;
	}

	public RepetitionRule getRepetitionRule() {
		return this.repetitionRule;
	}

	// private boolean manualStart; // made obsolete by rule impl

	public ManualActivationRule getManualActivationRule() {
		return manualActivationRule;
	}

	public void setManualActivationRule(ManualActivationRule manualActivationRule) {
		this.manualActivationRule = manualActivationRule;
	}

	/**
	 * <p>
	 * Returns <code>true</code> or <code>false</code> based on the evaluated
	 * {@link ManualActivationRule}.
	 * </p>
	 * <p>
	 * Underlying {@link Rule} needs an implementation which is loaded in the
	 * configured {@link RuleExpressionFactory}.
	 * </p>
	 * 
	 * @return <code>true</code> if manually activated, or <code>false</code> if not
	 *         or rule not set/found
	 */
	@JohnzonIgnore
	public boolean isManualStart() {
		if (this.manualActivationRule != null) {
			return evaluateManualActivation();
		} else {
			return false;
		}
	}

	private boolean evaluateManualActivation() {
		return this.manualActivationRule.evaluate();
	}

	public CaseRole getCaseRole() {
		return caseRole;
	}

	public void setCaseRole(CaseRole caseRole) {
		this.caseRole = caseRole;
	}

	public Task() {

	}

	// for first level relationship to case
	/**
	 * <p>
	 * Constructs a new Task with a first level relationship to a {@link CaseModel}.
	 * Automatically adds itself to the CaseModel. Automatically sets its state to
	 * INITIAL and the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the Task directly to a CaseModel. Its
	 * (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param caseRef
	 *            reference to the CaseModel the Task is being added to
	 */
	public Task(String cmId, String name, CaseModel caseRef) {
		super(cmId, name, caseRef);
		super.state = TaskStates.INITIAL.toString();
		this.blocking = true;
	}

	/**
	 * <p>
	 * Constructs a new Task with a second level relationship to a
	 * {@link CaseModel}. Automatically adds itself to the parent Stage.
	 * Automatically sets its state to INITIAL, adds itself to the parentStage and
	 * the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the Task to a Stage. Its (persistence)
	 * id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param parentStage
	 *            reference to the Stage the Task is being added to
	 */
	public Task(String cmId, String name, Stage parentStage) {
		super(cmId, name, parentStage);
		super.state = TaskStates.INITIAL.toString();
		this.blocking = true;
	}

	/**
	 * <p>
	 * Constructor available for running tests.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 */
	public Task(String cmId, String name) {
		super();
		super.setCmId(cmId);
		super.setName(name);
		super.state = TaskStates.INITIAL.toString();
		this.blocking = true;
	}

	public CaseWorker getClaimant() {
		return claimant;
	}

	public void setClaimant(CaseWorker claimant) {
		this.claimant = claimant;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JohnzonIgnore
	public boolean isBlocking() {
		return blocking;
	}

	public void setBlocking(boolean blocking) {
		this.blocking = blocking;
	}

	/**
	 * <p>
	 * Returns an implementation of {@link IStageTaskState} based on the current
	 * state. The implementation offers all transitions, but only implements those
	 * permissible.
	 * </p>
	 * <p>
	 * Used as a state pattern adaptation. Each implementation captures permissible
	 * transitions from a state. See CMMN 1.1 specification section 8.4.2 for more
	 * information.
	 * </p>
	 * 
	 * @return an implementation of {@link IStageTaskState} based on the current
	 *         state, e.g. ACTIVE
	 * @see {@link Task#loadContextState()}
	 */
	public IStageTaskState getContextState() {
		loadContextState();
		return contextState;
	}

	public void setContextState(IStageTaskState contextState) {
		this.contextState = contextState;
	}

	@Override
	public void loadContextState() {
		if (state.equals(StageStates.INITIAL.toString())) {
			setContextState(new StageTaskInitial(this));
		} else if (state.equals(StageStates.AVAILABLE.toString())) {
			setContextState(new StageTaskAvailable(this));
		} else if (state.equals(StageStates.ENABLED.toString())) {
			setContextState(new StageTaskEnabled(this));
		} else if (state.equals(StageStates.DISABLED.toString())) {
			setContextState(new StageTaskDisabled(this));
		} else if (state.equals(StageStates.ACTIVE.toString())) {
			setContextState(new StageTaskActive(this));
		} else if (state.equals(StageStates.FAILED.toString())) {
			setContextState(new StageTaskFailed(this));
		} else if (state.equals(StageStates.SUSPENDED.toString())) {
			setContextState(new StageTaskSuspended(this));
		} else if (state.equals(StageStates.COMPLETED.toString())) {
			setContextState(new StageTaskCompleted(this));
		} else if (state.equals(StageStates.TERMINATED.toString())) {
			setContextState(new StageTaskTerminated(this));
		}
	}
}