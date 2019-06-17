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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.listeners.EventListener;
import cm.core.listeners.EventMilestoneStates;
import cm.core.rules.ManualActivationRule;
import cm.core.rules.RequiredRule;
import cm.core.rules.Rule;
import cm.core.sentries.ElementOnPart;
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
import cm.core.states.StageTaskTransitions;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;
import cm.core.utils.RuleExpressionFactory;

/**
 * <p>
 * Class representing Stage elements in CMMN. Repetition decorator not yet
 * implemented since specification does not specify it.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.8, 8.4.2 and 8.6.4 for more
 * information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("stage")
public class Stage extends CaseElement {

	@JohnzonIgnore
	private boolean repetition;
	// TODO implement repetition mechanics
	@JohnzonIgnore
	private int maxRepetitions;
	@JohnzonIgnore
	private int currentRepetition;
	@JohnzonIgnore
	private boolean required; // made obsolete by rule impl
	@JohnzonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	private RequiredRule requiredRule;

	// public void setManualStart(boolean manual) {
	// this.manualStart = manual;
	// }

	@JohnzonIgnore
	private boolean autoComplete;
	// @JohnzonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parentStage", orphanRemoval = true)
	private List<CaseElement> childElements;
	@JohnzonIgnore
	@Transient
	private IStageTaskState contextState;
	@JohnzonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	private ManualActivationRule manualActivationRule;

	private boolean evaluateRequired() {
		if (this.requiredRule != null) {
			return this.requiredRule.evaluate();
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
	 * @return <code>true</code> if required, or <code>false</code> if not or rule not set/found
	 */
	public boolean isRequired() {
		return evaluateRequired();
	}

	public ManualActivationRule getManualActivationRule() {
		return manualActivationRule;
	}

	public void setManualActivationRule(ManualActivationRule manualActivationRule) {
		this.manualActivationRule = manualActivationRule;
	}
	/**
	 * <p>
	 * Returns <code>true</code> or <code>false</code> based on the evaluated
	 * {@link ManualActivationRule}.</p>
	 * <p>
	 * Underlying {@link Rule} needs an implementation which is loaded in the
	 * configured {@link RuleExpressionFactory}.
	 * </p>
	 * 
	 * @return <code>true</code> if manually activated, or <code>false</code> if not or rule not set/found
	 */
	@JohnzonIgnore
	public boolean isManualStart() {
		return evaluateManualActivation();
	}

	private boolean evaluateManualActivation() {
		if (this.manualActivationRule != null) {
			return this.manualActivationRule.evaluate();
		} else {
			return false;
		}
	}

	// for simplified repetition mechanics, not used
	@JohnzonIgnore
	public boolean isRepetition() {
		return repetition;
	}

	public void setRepetition(boolean repetition) {
		this.repetition = repetition;
	}

	public int getMaxRepetitions() {
		return maxRepetitions;
	}

	public void setMaxRepetitions(int maxRepetitions) {
		this.maxRepetitions = maxRepetitions;
	}

	public int getCurrentRepetition() {
		return currentRepetition;
	}

	public void setCurrentRepetition(int currentRepetition) {
		this.currentRepetition = currentRepetition;
	}

	public boolean isAutoComplete() {
		return autoComplete;
	}

	public void setAutoComplete(boolean autoComplete) {
		this.autoComplete = autoComplete;
	}

	public List<CaseElement> getChildElements() {
		return childElements;
	}

	public void setChildElements(List<CaseElement> childElements) {
		this.childElements = childElements;
	}

	/**
	 * <p>
	 * Adds one or more {@link CaseElement}s. Automatically sets the parent stage and
	 * case reference. Checks for duplicate {@link CaseElement#cmId}s. Does not add the
	 * child if its cmId is a duplicate.
	 * </p>
	 * 
	 * @param children
	 *            one ore more Elements
	 */
	public void addChildElement(CaseElement... children) {
		Set<String> elementIds = new TreeSet<>();
		if (this.childElements == null) {
			this.childElements = new ArrayList<CaseElement>();
		} else if (this.childElements.size() > 0) {
			for (CaseElement element : this.childElements) {
				elementIds.add(element.getCmId());
			}
		}
		for (CaseElement element : children) {
			// child is added only if .add of Set returns true
			if (elementIds.add(element.getCmId())) {
				this.childElements.add(element);
				element.setParentStage(this);

			} else {
				// TODO throw duplicate error and error when CaseModel is added
			}
		}

	}
	
	public void removeChild(CaseElement child) {
		String childcmId = child.getCmId();
		int foundIdx = -1;
		if(this.childElements != null) {
			for(int i = 0; i < this.childElements.size(); i++) {
				if(this.childElements.get(i).getCmId().equals(childcmId)) {
					foundIdx = i;
					return;
				}
			}
		}
		if(foundIdx > -1) {
			this.childElements.remove(foundIdx);
		}
	}

//	public void removeChildElement(CaseElement child) {
//		if (this.childElements != null) {
//			for (int i = 0; i < this.childElements.size(); i++) {
//				if (this.childElements.get(i).getCmId().equals(child.getCmId()))
//					this.childElements.remove(i);
//			}
//		}
//	}

	public void setSuspended(boolean suspended) {
		if (suspended) {
			if (this.getChildElements() != null) {
				suspendChildren();
				this.suspended = suspended;
			}
		} else {
			if (this.getChildElements() != null) {
				resumeChildren();
			}
		}
	}

	private void resumeChildren() {
		for (CaseElement child : this.getChildElements()) {
			if(child instanceof Stage) {
				((Stage) child).setSuspended(false);
			} else {
				child.setSuspended(false);
			}			
		}

	}

	private void suspendChildren() {
		for (CaseElement child : this.getChildElements()) {
			if(child instanceof Stage) {
				((Stage) child).setSuspended(true);
			} else {
				child.setSuspended(true);
			}			
		}
	}

	/**
	 * <p>
	 * Returns <code>true</code> if any contained child elements are in state
	 * ACTIVE.
	 * </p>
	 * <p>
	 * Used for auto-completion mechanism. See CMMN 1.1 specification section 8.6.1
	 * for more information.
	 * </p>
	 * 
	 * @return <code>true</code> if the Stage contains an active child element, or
	 *         <code>false</code> if none is in state ACTIVE.
	 */
	public boolean hasActiveChildren() {
		boolean hasActiveElements = false;

		if (this instanceof Stage) {
			String stageState = this.getState();
			if (stageState.equals(StageStates.ACTIVE.toString())) {
				for (CaseElement e : this.getChildElements()) {
					if (e.getState().equals(TaskStates.ACTIVE.toString())) {
						hasActiveElements = true;
						if (hasActiveElements)
							break;
					}
				}
			}
		}

		return hasActiveElements;
	}

	/**
	 * <p>
	 * Returns <code>true</code> if contained children of type {@link Stage} or
	 * {@link Task} are not in a permissible state and
	 * evaluation of {@link RequiredRule} returns <code>true</code>. Permissible
	 * states are COMPLETED, TERMINATED, DISABLED and FAILED.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification section 8.6.3 for more information.
	 * </p>
	 * 
	 * @return <code>true</code> if a required and incomplete child element was
	 *         found
	 */
	public boolean hasIncompleteRequiredChildren() {
		boolean hasIncompleteRequiredChildren = false;

		for (CaseElement e : this.getChildElements()) {
			String eState = e.getState();
			boolean permissibleState = false;
			permissibleState = eState.equals(TaskStates.DISABLED.toString())
					|| eState.equals(TaskStates.COMPLETED.toString()) || eState.equals(TaskStates.TERMINATED.toString())
					|| eState.equals(TaskStates.FAILED.toString());
			if (e instanceof Stage) {
				Stage s = (Stage) e;
				if (s.isRequired()) {
					if (!permissibleState) {
						hasIncompleteRequiredChildren = true;
						break;
					}

				}
			} else if (e instanceof Task) {
				Task t = (Task) e;
				if (t.isRequired()) {
					if (!permissibleState) {
						hasIncompleteRequiredChildren = true;
						break;
					}

				}
			} else if (e instanceof Milestone) {
				Milestone m = (Milestone) e;
				if (m.isRequired()) {
					if (!permissibleState) {
						hasIncompleteRequiredChildren = true;
						break;
					}

				}
			}
		}

		return hasIncompleteRequiredChildren;
	}

	/**
	 * <p>
	 * Returns <code>true</code> if no children in state ACTIVE are contained or the
	 * children are not required. Used for auto-complete mechanism.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification sections 8.6.1 and 8.6.3 for more information.
	 * </p>
	 * 
	 * @return <code>true</code> if no child element is not active nor required, or
	 *         <code>false</code> if one is active or required
	 */
	@JohnzonIgnore
	public boolean isReadyForComplete() {
		boolean readyForComplete = true;

		if (!(this.getChildElements() == null)) {
			for (CaseElement e : this.getChildElements()) {
				String eState = e.getState();
				if (eState.equals(TaskStates.ACTIVE.toString())) {
					readyForComplete = false;
					break; // break if any one element is active and this negates ready for autocomplete
				} else if (elementRequired(e)) {
					if (eState.equals(TaskStates.DISABLED.toString()) || eState.equals(TaskStates.COMPLETED.toString())
							|| eState.equals(TaskStates.TERMINATED.toString())
							|| eState.equals(TaskStates.FAILED.toString())) {
						readyForComplete = true;
					} else {
						readyForComplete = false;
						break; // break if any one element does not fulfill the stated required states for
								// required elements
					}
				} else {
					if (eState.equals(TaskStates.DISABLED.toString()) || eState.equals(TaskStates.COMPLETED.toString())
							|| eState.equals(TaskStates.TERMINATED.toString())
							|| eState.equals(TaskStates.FAILED.toString())) {
						readyForComplete = true;
					} else {
						readyForComplete = false;
						break; // break if any one element does not fulfill the stated required states for
								// required elements
					}
				}

			}
		}

		return readyForComplete;
	}

	private boolean elementRequired(CaseElement e) {
		if (e instanceof Stage) {
			return ((Stage) e).isRequired();
		} else if (e instanceof Task) {
			return ((Task) e).isRequired();
		} else if (e instanceof Milestone) {
			return ((Milestone) e).isRequired();
		}
		return false;
	}

	public Stage() {

	}

	// for first level relationship to case
	/**
	 * <p>
	 * Constructs a new Stage with a first level relationship to a
	 * {@link CaseModel}. Automatically sets its state to INITIAL and the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the Stage directly to a CaseModel. Its
	 * (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param caseRef
	 *            reference to the CaseModel the Stage is being added to
	 */
	public Stage(String cmId, String name, CaseModel caseRef) {
		super(cmId, name, caseRef);
		super.state = StageStates.INITIAL.toString();
	}

	// for sub-stages, caseRef will be fetched from parent(s) stage(s)
	/**
	 * <p>
	 * Constructs a new Stage with a second level relationship to a
	 * {@link CaseModel}. Automatically sets its state to INITIAL and the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the Stage to another Stage. Its
	 * (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param id
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param parentStage
	 *            reference to the Stage the Stage is being added to
	 */
	public Stage(String id, String name, Stage parentStage) {
		super(id, name, parentStage);
		super.state = StageStates.INITIAL.toString();
//		super.caseRef = parentStage.getCaseRef();
	}

	/**
	 * <p>
	 * Constructor available for running tests.
	 * </p>
	 * 
	 * @param id
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 */
	public Stage(String id, String name) {
		super();
		super.setCmId(id);
		super.setName(name);
		super.state = StageStates.INITIAL.toString();
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
	 * @see {@link Stage#loadContextState()}
	 */
	public IStageTaskState getContextState() {
		loadContextState();
		return contextState;
	}

	public void setContextState(IStageTaskState contextState) {
		this.contextState = contextState;
	}

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

	/**
	 * <p>
	 * Propagates the Stage changing its state to ACTIVE to all contained child
	 * elements.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification section 8.4.2 for more information.
	 * </p>
	 */
	public void propagateParentActivate() {
		String childState = "";
		List<CaseElement> childElements = getChildElements();
		if (childElements != null) {
			for (CaseElement child : childElements) {
				childState = child.getState();
				if (child instanceof Task) {
					if (childState.equals(TaskStates.INITIAL.toString())) {
						((Task) child).getContextState().create();
					}
				} else if (child instanceof Stage) {
					if (childState.equals(StageStates.INITIAL.toString())) {
						((Stage) child).getContextState().create();
					}
				} else if (child instanceof Milestone) {
					if (childState.equals(EventMilestoneStates.INITIAL.toString())) {
						((Milestone) child).getContextState().create();
					}
				} else if (child instanceof EventListener) {
					if (childState.equals(EventMilestoneStates.INITIAL.toString())) {
						((EventListener) child).getContextState().create();
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * Propagates the Stage changing its state to SUSPENDED to all contained child
	 * elements.
	 * </p>
	 * <p>
	 * Uses the state pattern See CMMN 1.1 specification section 8.4.2 for more
	 * information.
	 * </p>
	 * 
	 * @see {@link Stage#getContextState()}
	 */
	public void propagateParentSuspend() {
		for (CaseElement child : getChildElements()) {
			if (child instanceof Task) {
				((Task) child).getContextState().parentSuspend();
			} else if (child instanceof Stage) {
				((Stage) child).getContextState().parentSuspend();
			} else if (child instanceof Milestone) {
				((Milestone) child).getContextState().suspend();
			} else if (child instanceof EventListener) {
				((EventListener) child).getContextState().suspend();
			}
		}
	}

	/**
	 * <p>
	 * Propagates the Stage changing its state from SUSPENDED to all contained child
	 * elements.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification section 8.4.2 for more information.
	 * </p>
	 */
	public void propagateParentResume() {
		for (CaseElement child : getChildElements()) {
			if (child instanceof Task) {
				((Task) child).getContextState().parentResume();
			} else if (child instanceof Stage) {
				((Stage) child).getContextState().parentResume();
			} else if (child instanceof Milestone) {
				((Milestone) child).getContextState().resume();
			} else if (child instanceof EventListener) {
				((EventListener) child).getContextState().resume();
			}
		}
	}

	/**
	 * <p>
	 * Propagates a Stage changing its state to ACTIVE to a parent Stage, activating
	 * it. The parent stage undergoes possible transitions so {@link ElementOnPart}s
	 * can pick up on them.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification section 8.4.2 for more information.
	 * </p>
	 */
	public void propagateActivation() {
		Stage parentStage = (Stage) getParentStage();
		if (parentStage != null) {
			String parentState = this.getParentStage().getState();
			if (!parentState.equals(StageStates.ACTIVE.toString())) {
				if (parentState.equals(StageStates.INITIAL.toString())) {
					parentStage.getContextState().create();
				} else if (parentState.equals(StageStates.AVAILABLE.toString())) {
					// skip states until ACTIVE, but include sending transitions for listening
					// sentries/onParts
					parentStage.setState(StageStates.ACTIVE.toString(), StageTaskTransitions.start.toString());
					parentStage.setState(StageStates.ACTIVE.toString(), StageTaskTransitions.enable.toString());
					parentStage.setState(StageStates.ACTIVE.toString(), StageTaskTransitions.manualStart.toString());
				} else if (parentState.equals(StageStates.ENABLED.toString())) {
					parentStage.getContextState().manualStart();
				} else if (parentState.equals(StageStates.DISABLED.toString())) {
					parentStage.getContextState().reEnable();
					parentStage.getContextState().manualStart();
				} else if (parentState.equals(StageStates.FAILED.toString())) {
					parentStage.getContextState().reActivate();
				} else if (parentState.equals(StageStates.SUSPENDED.toString())) {
					parentStage.getContextState().resume();
				}
			}
			parentStage.propagateActivation();
		}

	}

}