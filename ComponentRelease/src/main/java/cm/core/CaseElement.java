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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.interfaces.IElementObservable;
import cm.core.interfaces.IElementObserver;
import cm.core.sentries.ElementOnPart;
import cm.core.sentries.EntrySentry;
import cm.core.sentries.ExitSentry;
import cm.core.sentries.OnPart;
import cm.core.sentries.Sentry;
import cm.core.states.StageTaskTransitions;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;

/**
 * <p>
 * Base class for representation of all elements in CMMN.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.2, 5.4 and chapter 8 for more
 * information.
 * </p>
 * 
 * @author André Zensen
 *
 */
// @MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// @DiscriminatorColumn(name = "ELEMENT_TYPE")
public abstract class CaseElement implements IElementObservable, Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	protected String cmId;
	protected String name;
	// private List<IElementObserver> elementObservers; JPA issue with interfaces
	@JohnzonIgnore
	@OneToMany(cascade = CascadeType.ALL)
	private List<ElementOnPart> elementObservers;
	@JohnzonIgnore
	@OneToMany(cascade = CascadeType.ALL)
	private List<Sentry> sentryRef;
	@JohnzonIgnore
	@ManyToOne
	private Stage parentStage;
	protected String state;
	@JohnzonIgnore
	protected String stateBeforeSuspend;
	@JohnzonIgnore
	protected boolean suspended;
	@JohnzonIgnore
	@ManyToOne
	protected CaseModel caseRef;
	protected CaseModel rootCase;

	public CaseElement() {

	}

	public CaseElement(String cmId, String name) {
		this.cmId = cmId;
		this.name = name;
	}

	// TODO offer constructor without state, assign INITIAL

	public CaseElement(String cmId, String name, Stage parentStage) {
		this.cmId = cmId;
		this.name = name;
		this.parentStage = parentStage;
		this.parentStage.addChildElement(this);
		this.rootCase = this.parentStage.getRootCase();
	}

	public CaseElement(String cmId, String name, CaseModel caseRef) {
		this.cmId = cmId;
		this.name = name;
		this.caseRef = caseRef;
		this.caseRef.addChildElement(this);
		this.rootCase = caseRef;
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

	public void setCmId(String id) {
		this.cmId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ElementOnPart> getElementObservers() {
		return elementObservers;
	}

	public void setElementObservers(List<ElementOnPart> observers) {
		this.elementObservers = observers;
	}

	public List<Sentry> getSentryRef() {
		return sentryRef;
	}

	public void setSentryRef(List<Sentry> sentries) {
		this.sentryRef = sentries;
	}

	public void addSentryRef(Sentry sentry) {
		if (this.sentryRef == null) {
			this.sentryRef = new ArrayList<Sentry>();
		}
		if (!this.sentryRef.contains(sentry)) {
			this.sentryRef.add(sentry);
		}
	}

	public Stage getParentStage() {
		return parentStage;
	}

	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}

	public CaseModel getCaseRef() {
//		CaseModel caseRef = this.caseRef;
//		if (this.caseRef == null && this.parentStage != null) {
//			caseRef = this.parentStage.getCaseRef();
//		}
		return caseRef;
	}

	public void setCaseRef(CaseModel caseRef) {
		this.caseRef = caseRef;
	}	

	public CaseModel getRootCase() {
		return rootCase;
	}

	public void setRootCase(CaseModel rootCase) {
		this.rootCase = rootCase;
	}

	/**
	 * <p>
	 * Sets the state of an element using a transition. Notifies any {@link OnPart}s
	 * observing the element. Automatically tries for auto-completion of parent
	 * stage or case.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification sections 5.4.6, 8.4 and 8.5 for more information.
	 * </p>
	 * 
	 * @param newState
	 *            a specified state, e.g. from enum {@link TaskStates}
	 * @param transition
	 *            a specified transition, e.g. from enum
	 *            {@link StageTaskTransitions}
	 */
	public void setState(String newState, String transition) {
		this.state = newState;
		if (this.elementObservers != null) {
			for (IElementObserver o : this.elementObservers) {
				o.updateElementObserver(transition);
			}
		}
		if (this.state.equals(StageStates.DISABLED.toString()) || this.state.equals(StageStates.COMPLETED.toString())
				|| this.state.equals(StageStates.TERMINATED.toString())
				|| this.state.equals(StageStates.FAILED.toString())) {
			if (this.parentStage != null) {
				if (this.parentStage.isAutoComplete()) {
					this.parentStage.doAutoComplete();
				} else if (!this.parentStage.isAutoComplete()) {
					// TODO notify users of possible completion
				}
			} else if (this.parentStage == null && this.caseRef != null) {
				if (this.caseRef.isAutoComplete()) {
					this.caseRef.doAutoComplete();
				}
			}
		}
	}

	/**
	 * <p>
	 * Sets the state of a cloned instance of a repeatable element to AVAILABLE and
	 * then ACTIVE. Transitions manually activated elements to ENABLE.
	 * </p>
	 * <p>
	 * Used for repetition mechanisms. Goes through transitions enable and start to
	 * notify observing {@link ElementOnPart}s. See CMMN 1.1 specification section
	 * 8.6.4 for more information.
	 * </p>
	 */
	public void setStateOfNewInstance() {
		if (this instanceof Task) {
			Task t = (Task) this;
			t.state = TaskStates.AVAILABLE.toString();
			if (t.isManualStart()) {
				t.getContextState().enable();
			} else {
				t.getContextState().start();
			}

		} else if (this instanceof Stage) {
			Stage s = (Stage) this;
			s.state = StageStates.AVAILABLE.toString();
			if (s.isManualStart()) {
				s.getContextState().enable();
			} else {
				s.getContextState().start();
			}
		}
	}

	public void doAutoComplete() {
		if (this instanceof CaseModel) {
			CaseModel cm = (CaseModel) this;
			if (!cm.hasActiveChildren() && !cm.hasIncompleteRequiredChildren()) {
				cm.getContextState().complete();
			}
		} else if (this instanceof Stage) {
			Stage s = (Stage) this;
			s.getContextState().complete();
		}
	}

	public String getStateBeforeSuspend() {
		return stateBeforeSuspend;
	}

	public void setStateBeforeSuspend(String stateBeforeSuspend) {
		this.stateBeforeSuspend = stateBeforeSuspend;
	}

	public boolean isSuspended() {
		return suspended;
	}

	// if this instanceof CaseModel || Stage
	public void setSuspended(boolean suspended) {
//		if (this instanceof CaseModel) {
//			CaseModel cm = (CaseModel) this;
//			if (suspended) {
//				if (cm.getChildElements() != null) {
//					suspendChildren();
//				}
//			} else {
//				if (cm.getChildElements() != null) {
//					resumeChildren();
//				}
//			}
//		} else if (this instanceof Stage) {
//			Stage s = (Stage) this;
//			if (suspended) {
//				if (s.getChildElements() != null) {
//					suspendChildren();
//				}
//			} else {
//				if (s.getChildElements() != null) {
//					resumeChildren();
//				}
//			}
//		}
		this.suspended = suspended;
	}

//	// if this instanceof CaseModel || Stage
//	private void resumeChildren() {
//		if (this instanceof CaseModel) {
//			CaseModel cm = (CaseModel) this;
//			for (Element child : cm.getChildElements()) {
//				child.setSuspended(false);
//			}
//		} else if (this instanceof Stage) {
//			Stage s = (Stage) this;
//			for (Element child : s.getChildElements()) {
//				child.setSuspended(false);
//			}
//		}
//	}
//
//	// if this instanceof CaseModel || Stage
//	private void suspendChildren() {
//		if (this instanceof CaseModel) {
//			CaseModel cm = (CaseModel) this;
//			for (Element child : cm.getChildElements()) {
//				child.setSuspended(true);
//			}
//		} else if (this instanceof Stage) {
//			Stage s = (Stage) this;
//			for (Element child : s.getChildElements()) {
//				child.setSuspended(true);
//			}
//		}
//	}

	public String getState() {
		return this.state;
	}

	public void setStateWithoutTransition(String state) {
		this.state = state;
	}

	public abstract void loadContextState();

	/**
	 * <p>
	 * Returns <code>true</code> if no {@link EntrySentry} is attached to the
	 * element or one is satisfied, or <code>false</code> if none is satisfied.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification sections 5.4.6 and 8.5 for more information.
	 * </p>
	 * 
	 * @return
	 */
	@JohnzonIgnore
	public boolean isEntrySentrySatisfied() {
		boolean satisfied = false;
		if (this.getSentryRef() == null || this.getSentryRef().size() == 0) {
			return true;
		} else if (this.getSentryRef() != null) {
			for (Sentry s : this.getSentryRef()) {
				if (s instanceof EntrySentry) {
					if (s.isSatisfied()) {
						return true;
					}
				}
			}
		}
		return satisfied;
	}

	/**
	 * <p>
	 * Returns <code>true</code> if no {@link ExitSentry} is attached to the element
	 * or any one is satisfied, or <code>false</code> if none is satisfied.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification sections 5.4.6 and 8.5 for more information.
	 * </p>
	 * 
	 * @return
	 */
	@JohnzonIgnore
	public boolean isExitSentrySatisfied() {
		boolean satisfied = false;
		if (this.getSentryRef() == null || this.getSentryRef().size() == 0) {
			return true;
		} else if (this.getSentryRef() != null) {
			for (Sentry s : this.getSentryRef()) {
				if (s instanceof ExitSentry) {
					if (s.isSatisfied()) {
						return true;
					}
				}
			}
		}
		return satisfied;
	}

	/**
	 * Adds an {@link ElementOnPart} as an observer if it is not already contained
	 * in the list.
	 * 
	 * @param o
	 *            the ElementOnPart observing the element
	 */
	public void registerElementObserver(ElementOnPart o) {
		if (this.elementObservers == null) {
			this.elementObservers = new ArrayList<ElementOnPart>();
		}
		if (!this.elementObservers.contains(o)) {
			this.elementObservers.add(o);
		}
	}

	public void unregisterElementObserver(ElementOnPart o) {
		int idx = this.elementObservers.indexOf(o);
		if (idx > -1) {
			this.elementObservers.remove(idx);
		}
	}

	@Override
	public CaseElement clone() throws CloneNotSupportedException {
		return (CaseElement) super.clone();
	}

}