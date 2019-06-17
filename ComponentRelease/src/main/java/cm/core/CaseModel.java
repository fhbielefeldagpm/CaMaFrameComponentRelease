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

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.LockModeType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.data.CaseFile;
import cm.core.listeners.EventListener;
import cm.core.listeners.EventMilestoneStates;
import cm.core.rules.RequiredRule;
import cm.core.states.CaseInstanceActive;
import cm.core.states.CaseInstanceClosed;
import cm.core.states.CaseInstanceCompleted;
import cm.core.states.CaseInstanceFailed;
import cm.core.states.CaseInstanceInitial;
import cm.core.states.CaseInstanceSuspended;
import cm.core.states.ICaseInstanceState;
import cm.core.tasks.CaseTask;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;

@NamedQueries({
		@NamedQuery(name = "CaseModel.findAll", query = "SELECT c FROM CaseModel c", lockMode = LockModeType.NONE),
		@NamedQuery(name = "CaseModel.findAllShort", query = "SELECT c.id, c.cmId, c.name, c.state FROM CaseModel c") })
/**
 * <p>
 * Class representing a case in CMMN.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.2.1, 8.2 and 8.4.1 for more
 * information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("case")
public class CaseModel extends CaseElement {

	@JohnzonIgnore
	private boolean autoComplete;
	@JohnzonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "caseRef")
	private List<CaseElement> childElements;
	@JohnzonIgnore
	@ManyToMany(cascade = CascadeType.ALL)
	private List<CaseRole> caseRoles;
	@JohnzonIgnore
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<CaseWorker> caseWorkers;
	@JohnzonIgnore
	@ManyToMany(cascade = CascadeType.PERSIST)
	private List<CaseWorker> caseAdmins;
	@JohnzonIgnore
	@OneToOne(cascade = CascadeType.ALL) // TODO orphan removal?
	private CaseFile caseFile;
	@JohnzonIgnore
	@OneToOne
	private CaseTask caseTaskRef;
	@JohnzonIgnore
	@Transient
	private ICaseInstanceState contextState;

	public CaseTask getCaseTaskRef() {
		return caseTaskRef;
	}

	public void setCaseTaskRef(CaseTask caseTaskRef) {
		this.caseTaskRef = caseTaskRef;
	}

	@JohnzonIgnore
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
	 * Adds one or more {@link CaseElement}s. Automatically sets the case reference.
	 * Checks for duplicate {@link CaseElement#cmId}s. Does not add the child if its
	 * cmId is a duplicate.
	 * </p>
	 * 
	 * @param children one ore more Elements
	 */
	public void addChildElement(CaseElement... children) {
		Set<String> elementIds = new HashSet<String>();
		if (this.childElements == null) {
			this.childElements = new ArrayList<CaseElement>();
		} else if (this.childElements.size() > 0) {
			for (CaseElement element : this.childElements) {
				elementIds.add(element.getCmId());
			}
		}
		for (CaseElement element : children) {
			if (elementIds.add(element.getCmId())) {
				this.childElements.add(element);
				element.setCaseRef(this);
			} else {
				// TODO throw duplicate error
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
//				if (this.childElements.get(i).cmId.equals(child.getCmId())) {
//					this.childElements.remove(i);
//					break;
//				}
//			}
//		}
//	}

	// if this instanceof CaseModel || Stage
	public void setSuspended(boolean suspended) {
		if (suspended) {
			if (this.getChildElements() != null) {
				suspendChildren();
			}
		} else {
			if (this.getChildElements() != null) {
				resumeChildren();
			}
		}
		super.suspended = suspended;
	}

	// if this instanceof CaseModel || Stage
	private void resumeChildren() {
		for (CaseElement child : this.getChildElements()) {
			if (child instanceof Stage) {
				((Stage) child).setSuspended(false);
			} else {
				child.setSuspended(false);
			}
		}

	}

	// if this instanceof CaseModel || Stage
	private void suspendChildren() {
		for (CaseElement child : this.getChildElements()) {
			if (child instanceof Stage) {
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
	 * @return <code>true</code> if the CaseModel contains an active child element,
	 *         or <code>false</code> if none is in state ACTIVE.
	 */
	@JohnzonIgnore
	// if this instanceof CaseModel || Stage
	public boolean hasActiveChildren() {
		boolean hasActiveElements = false;

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

		return hasActiveElements;
	}

	/**
	 * <p>
	 * Returns <code>true</code> if contained children of type {@link Stage} or
	 * {@link Task}are not in a permissible state and evaluation of
	 * {@link RequiredRule} returns <code>true</code>. Permissible states are
	 * COMPLETED, TERMINATED, DISABLED and FAILED.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification section 8.6.3 for more information.
	 * </p>
	 * 
	 * @return <code>true</code> if a required and incomplete child element was
	 *         found
	 */
	@JohnzonIgnore
	// if this instanceof CaseModel || Stage
	public boolean hasIncompleteRequiredChildren() {
		boolean hasIncompleteRequiredChildren = false;

		for (CaseElement e : this.getChildElements()) {
			String eState = e.getState();
			boolean permissibleState = false;
			permissibleState = eState.equals(TaskStates.DISABLED.toString())
					|| eState.equals(TaskStates.COMPLETED.toString()) || eState.equals(TaskStates.TERMINATED.toString())
					|| eState.equals(TaskStates.FAILED.toString());
			if (elementRequired(e)) {
				if (!permissibleState) {
					hasIncompleteRequiredChildren = true;
					break;
				}

			}
		}

		return hasIncompleteRequiredChildren;
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

	public CaseModel() {

	}

	public CaseModel(String id, String name) {
		super.cmId = id;
		super.name = name;
		this.caseFile = new CaseFile(this);
		this.state = CaseStates.INITIAL.toString();
	}

	public List<CaseRole> getCaseRoles() {
		return caseRoles;
	}

	public boolean addCaseRole(CaseRole r) {
		if (this.caseRoles == null) {
			this.caseRoles = new ArrayList<CaseRole>();
		} else if (caseRoles.contains(r)) {
			return false;
		}
		caseRoles.add(r);
		return true;

	}

	public boolean removeCaseRole(CaseRole r) {
		if (this.caseRoles != null) {
			int roleIdx = this.caseRoles.indexOf(r);
			if (roleIdx != -1) {
				this.caseRoles.remove(roleIdx);
				return true;
			}
		}
		return false;
	}

	public void setCaseRoles(List<CaseRole> caseRoles) {
		this.caseRoles = caseRoles;
	}

	/**
	 * <p>
	 * Returns an implementation of {@link ICaseInstanceState} based on the current
	 * state. The implementation offers all transitions, but only implements those
	 * permissible.
	 * </p>
	 * <p>
	 * Used as a state pattern adaptation. Each implementation captures permissible
	 * transitions from a state. See CMMN 1.1 specification section 8.4.1 for more
	 * information.
	 * </p>
	 * 
	 * @return an implementation of {@link ICaseInstanceState} based on the current
	 *         state, e.g. ACTIVE
	 * @see {@link CaseModel#loadContextState()}
	 */
	public ICaseInstanceState getContextState() {
		loadContextState();
		return contextState;
	}

	public void setContextState(ICaseInstanceState contextState) {
		this.contextState = contextState;
	}

	public void loadContextState() {
		if (state.equals(CaseStates.INITIAL.toString())) {
			setContextState(new CaseInstanceInitial(this));
		} else if (state.equals(CaseStates.ACTIVE.toString())) {
			setContextState(new CaseInstanceActive(this));
		} else if (state.equals(CaseStates.COMPLETED.toString())) {
			setContextState(new CaseInstanceCompleted(this));
		} else if (state.equals(CaseStates.FAILED.toString())) {
			setContextState(new CaseInstanceFailed(this));
		} else if (state.equals(CaseStates.SUSPENDED.toString())) {
			setContextState(new CaseInstanceSuspended(this));
		} else if (state.equals(CaseStates.CLOSED.toString())) {
			setContextState(new CaseInstanceClosed(this));
		}
	}

	public CaseFile getCaseFile() {
		return caseFile;
	}

	public void setCaseFile(CaseFile caseFile) {
		this.caseFile = caseFile;
	}

	// public String getState() {
	// return super.getState();
	// }
	//
	// public void setState(String state, String transition) {
	// super.setState(state, transition);
	// }

	public List<CaseWorker> getCaseWorkers() {
		return caseWorkers;
	}

	public void setCaseWorkers(List<CaseWorker> caseWorkers) {
		this.caseWorkers = caseWorkers;
	}

	public List<CaseWorker> getCaseAdmins() {
		return caseAdmins;
	}

	public void setCaseAdmins(List<CaseWorker> caseAdmins) {
		this.caseAdmins = caseAdmins;
	}

	/**
	 * <p>
	 * Called when a CaseModel transitions from state INITIAL to state ACTIVE.
	 * </p>
	 * <p>
	 * See CMMN 1.1. specification section 8.4.1 for more information.
	 * </p>
	 * 
	 * @see {@link CaseModel#getContextState()}
	 */
	public void propagateActivate() {
		String childState = "";
		if (getChildElements() != null) {
			for (CaseElement child : getChildElements()) {
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
	 * Propagates suspension of the CaseModel to its child elements.
	 * </p>
	 * <p>
	 * See CMMN 1.1. specification section 8.4.1 for more information.
	 * </p>
	 * 
	 * @see {@link CaseModel#getContextState()}
	 */
	public void propagateSuspend() {
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
	 * Propagates re-activation of the suspended CaseModel to its child elements.
	 * </p>
	 * <p>
	 * See CMMN 1.1. specification section 8.4.1 for more information.
	 * </p>
	 * 
	 * @see {@link CaseModel#getContextState()}
	 */
	public void propagateReactivate() {
		for (CaseElement child : getChildElements()) {
			if (child instanceof Task) {
				((Task) child).getContextState().resume();
			} else if (child instanceof Stage) {
				((Stage) child).getContextState().resume();
			} else if (child instanceof Milestone) {
				((Milestone) child).getContextState().resume();
			} else if (child instanceof EventListener) {
				((EventListener) child).getContextState().resume();
			}
		}
	}

}