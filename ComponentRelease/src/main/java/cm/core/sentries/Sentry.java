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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import cm.core.CaseModel;
import cm.core.CaseElement;
import cm.core.Milestone;
import cm.core.Stage;
import cm.core.StageStates;
import cm.core.data.CaseFileItem;
import cm.core.interfaces.ICriteriaObserver;
import cm.core.services.TaskService;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;

/**
 * <p>
 * Base class for specializations {@link EntrySentry} and {@link ExitSentry}.
 * Observes state changes of {@link CaseElement}s or {@link CaseFileItem}s via its
 * {@link OnPart}s. Sentrys are notified when the required transition has taken
 * place and checks its OnParts and its (optional) {@link IfPart}. Once
 * satisfied, the Element it is attached to transitions from state AVAILABLE to
 * a permissible state.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.6 and 8.5 for more information.
 * </p>
 * 
 * @author André Zensen
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sentry_type")
public abstract class Sentry implements ICriteriaObserver {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String cmId;
	private String name;
	private boolean isSatisfied;
	@ManyToOne
	private CaseElement elementRef; // element this sentry is attached to
	@OneToMany(mappedBy = "sentryRef", cascade = CascadeType.ALL)
	private List<OnPart> onParts;
	@OneToOne(mappedBy = "sentryRef", cascade = CascadeType.ALL)
	private IfPart ifPart;

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

	public boolean isSatisfied() {
		checkCriteria();
		return this.isSatisfied;
	}

	public void setSatisfied(boolean isSatisfied) {
		this.isSatisfied = isSatisfied;
	}

	public CaseElement getElementRef() {
		return this.elementRef;
	}

	public void setElementRef(CaseElement elementRef) {
		this.elementRef = elementRef;
	}

	public List<OnPart> getOnParts() {
		return onParts;
	}

	public void setOnParts(List<OnPart> onParts) {
		this.onParts = onParts;
	}

	public void addOnPart(OnPart onPart) {
		if (this.onParts == null) {
			this.onParts = new ArrayList<OnPart>();
			this.onParts.add(onPart);
		} else {
			if (!this.onParts.contains(onPart)) {
				this.onParts.add(onPart);
			}
		}
	}

	public void addOnParts(OnPart... onparts) {
		for (OnPart oP : onparts) {
			this.addOnPart(oP);
		}
	}

	public void removeOnPart(OnPart onPart) {
		if (this.onParts != null) {
			int idx = this.onParts.indexOf(onPart);
			if (idx > -1) {
				this.onParts.remove(idx);
			}
		}
	}

	public IfPart getIfPart() {
		return ifPart;
	}

	public void setIfPart(IfPart ifPart) {
		this.ifPart = ifPart;
	}

	/**
	 * Evaluates OnParts and IfPart whether they are satisfied or not. If satisfied,
	 * actions are performed depending on the Sentry being an EntrySentry or
	 * ExitSentry.
	 */
	public void checkCriteria() {
		boolean onPartsPassed = false;
		boolean ifPartPassed = false;

		if (this.ifPart == null) {
			ifPartPassed = true;
		} else if (this.ifPart != null) {
			if (this.ifPart.isSatisfied()) {
				ifPartPassed = true;
			}
		}

		if (this.onParts == null) {
			onPartsPassed = true;
		} else if (this.onParts != null) {
			for (OnPart op : this.onParts) {
				if (op.isSatisfied()) {
					onPartsPassed = true;
				} else if (!op.isSatisfied()) {
					onPartsPassed = false;
				}
			}
		}
		if (onPartsPassed && ifPartPassed) {
			this.setSatisfied(true);

			if (this instanceof EntrySentry) {
				performEntrySentryAction();
			} else if (this instanceof ExitSentry) {
				performExitSentryAction();
			}
		}

	}

	/**
	 * <p>
	 * Transitions a {@link CaseModel} via transition terminate, {@link Stage}s and
	 * {@link Task}s via transition exit.
	 * </p>
	 * <p>
	 * Exit criterion sentries ({@link ExitSentry}) are considered ready for
	 * evaluation while the CasePlanModel ({@link CaseModel}), {@link Stage}, or
	 * {@link Task} is in state ACTIVE. See CMMN 1.1 specification section 8.5.
	 * </p>
	 */
	private void performExitSentryAction() {
		CaseElement e = this.getElementRef();
		if (e.getState().equals(TaskStates.ACTIVE.toString())) {
			if (e instanceof CaseModel) {
				CaseModel cm = (CaseModel) e;
				cm.getContextState().terminate();
			} else if (e instanceof Stage) {
				Stage s = (Stage) e;
				s.getContextState().exit();
			} else if (e instanceof Task) {
				Task t = (Task) e;
				t.getContextState().exit();
			}
		}
	}

	/**
	 * <p>
	 * Transitions a {@link Stage} or {@link Task} via transition enable or start
	 * and a {@link Milestone} via transition occur. Uses {@link TaskService} to
	 * create a new instance of a repeatable Task.
	 * </p>
	 * <p>
	 * Entry criterion sentries ({@link EntrySentry}) are considered ready for
	 * evaluation while the {@link Task}, {@link Stage}, or {@link Milestone} is in
	 * state AVAILABLE. See CMMN 1.1 specification sections 8.5 and 8.6.4.
	 * </p>
	 */
	private void performEntrySentryAction() {
		CaseElement refElement = this.getElementRef();
		Stage parentStage = (Stage) refElement.getParentStage();
		String elementState = refElement.getState();
		if (elementState.equals(TaskStates.AVAILABLE.toString())) {
			if (refElement instanceof Milestone) {
				Milestone m = (Milestone) refElement;
				m.getContextState().occur();
			} else if (refElement instanceof Stage) {
				Stage s = (Stage) refElement;
				if (s.isManualStart()) {
					s.getContextState().enable();
				} else {
					s.getContextState().start();
				}
			} else if (refElement instanceof Task) {
				Task t = (Task) refElement;
				if (t.isManualStart()) {
					t.getContextState().enable();
				} else {
					t.getContextState().start();
				}
			}
		} else if (!elementState.equals(TaskStates.AVAILABLE.toString())
				&& !elementState.equals(TaskStates.INITIAL.toString())) {
			if (refElement instanceof Task) {
				Task t = (Task) refElement;
				if (t.isRepeatable()) {
					// get CaseFileService via CDI-context, since @Inject does not work in a
					// non-managed bean
					TaskService taskService = CDI.current().select(TaskService.class).get();
					taskService.createNewInstance(t);
				}
			}
		}
		// transition a parent Stage which is still in state INITIAL via transition
		// create
		if (parentStage != null) {
			String parentState = parentStage.getState();
			if (parentState.equals(StageStates.INITIAL.toString())) {
				parentStage.getContextState().create();
			}
		}

	}

	@Override
	public void updateCriteriaObserver() {
		this.checkCriteria();
	}
}