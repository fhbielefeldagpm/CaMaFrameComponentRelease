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
package cm.core.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import cm.core.CaseModel;
import cm.core.CaseWorker;
import cm.core.sentries.ExitSentry;
import cm.core.sentries.Sentry;
import cm.core.services.ServiceMessage;
import cm.core.services.TaskService;
import cm.core.services.commands.stagetask.StageTaskTransitionCommandFactory;
import cm.core.services.commands.stagetask.StageTaskTransitionController;
import cm.core.services.commands.stagetask.TaskTransitionCommand;
import cm.core.states.StageTaskTransitions;
import cm.core.tasks.CaseTask;
import cm.core.tasks.HumanTask;
import cm.core.tasks.ProcessTask;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;
import cm.core.utils.PersistenceSettings;

@Stateless
public class TaskServiceImpl implements TaskService {

	@PersistenceContext(unitName = PersistenceSettings.persistenceContextUnitName)
	private EntityManager em;

	public TaskServiceImpl() {

	}

	@Override
	public List<HumanTask> getAllTasks() {
		TypedQuery<HumanTask> query = em.createQuery("SELECT t FROM HumanTask t", HumanTask.class);
		List<HumanTask> tasks = query.getResultList();
		return tasks;
	}

	@Override
	public List<HumanTask> getAllTasksByCase(CaseModel cm) {
		TypedQuery<HumanTask> query = em.createQuery("SELECT t FROM HumanTask t WHERE t.caseRef.id = :caseId",
				HumanTask.class);
		long caseId = cm.getId();
		query.setParameter("caseId", caseId);

		List<HumanTask> tasks = query.getResultList();
		return tasks;
	}

	@Override
	public List<HumanTask> getAllClaimableTasks() {
		TypedQuery<HumanTask> query = em.createQuery(
				"SELECT t FROM HumanTask t WHERE t.claimant IS NULL AND (t.state= :active OR t.state= :enabled)",
				HumanTask.class);
		query.setParameter("active", TaskStates.ACTIVE.toString());
		query.setParameter("enabled", TaskStates.ENABLED.toString());
		List<HumanTask> tasks = query.getResultList();
		return tasks;
	}

	@Override
	public List<HumanTask> getAllClaimableTasksByRole(CaseWorker cw) {
		TypedQuery<HumanTask> query = em.createQuery(
				"SELECT t FROM HumanTask t WHERE t.claimant IS NULL AND (t.state= :active OR t.state= :enabled) AND (t.caseRole.roleName IN :caseRole OR t.caseRole IS NULL)",
				HumanTask.class);
		query.setParameter("active", TaskStates.ACTIVE.toString());
		query.setParameter("enabled", TaskStates.ENABLED.toString());
		query.setParameter("caseRole", cw.getCaseRolesAsString());
		List<HumanTask> tasks = query.getResultList();
		return tasks;
	}

	@Override
	public ServiceMessage createNewInstance(Task taskIn) {
		if (taskIn instanceof HumanTask) {
			HumanTask originalTaskInContext = em.find(HumanTask.class, taskIn.getId());
			HumanTask taskClone = new HumanTask();

			setInstanceIdentityAndPersist(originalTaskInContext, taskClone);
			setInstanceDetailsAndMerge(originalTaskInContext, taskClone);

			HumanTask mergedtaskClone = em.merge(taskClone);
			mergedtaskClone.setStateOfNewInstance();
		} else if (taskIn instanceof ProcessTask) {
			ProcessTask originalTaskInContext = em.find(ProcessTask.class, taskIn.getId());
			ProcessTask taskClone = new ProcessTask();

			setInstanceIdentityAndPersist(originalTaskInContext, taskClone);
			setInstanceDetailsAndMerge(originalTaskInContext, taskClone);

			ProcessTask mergedtaskClone = em.merge(taskClone);
			mergedtaskClone.setStateOfNewInstance();
		} else if (taskIn instanceof CaseTask) {
			CaseTask originalTaskInContext = em.find(CaseTask.class, taskIn.getId());
			CaseTask taskClone = new CaseTask();

			setInstanceIdentityAndPersist(originalTaskInContext, taskClone);
			setInstanceDetailsAndMerge(originalTaskInContext, taskClone);

			CaseTask mergedtaskClone = em.merge(taskClone);
			mergedtaskClone.setStateOfNewInstance();
		}
		return null;
		// TODO ServiceMessage
	}

	private void setInstanceIdentityAndPersist(Task originalTaskInContext, Task taskClone) {
		taskClone.setCmId(originalTaskInContext.getCmId());
		taskClone.setName(originalTaskInContext.getName());
		em.persist(taskClone);
	}

	private void setInstanceDetailsAndMerge(Task originalTaskInContext, Task taskClone) {
		if (originalTaskInContext.getElementObservers() != null) {
			taskClone.setElementObservers(originalTaskInContext.getElementObservers());
		}
		List<Sentry> sentryRef = originalTaskInContext.getSentryRef();
		if (sentryRef != null) {
			for (Sentry s : sentryRef) {
				if (s instanceof ExitSentry) {
					taskClone.addSentryRef(s);
				}
			}
		}
		taskClone.setParentStage(originalTaskInContext.getParentStage());
		taskClone.setCaseRef(originalTaskInContext.getCaseRef());

		taskClone.setRepetitionRule(originalTaskInContext.getRepetitionRule());
		taskClone.setRequiredRule(originalTaskInContext.getRequiredRule());
		taskClone.setManualActivationRule(originalTaskInContext.getManualActivationRule());

		taskClone.setDescription(originalTaskInContext.getDescription());
		taskClone.setBlocking(originalTaskInContext.isBlocking());
		taskClone.setCaseRole(originalTaskInContext.getCaseRole());
	}

	@Override
	public ServiceMessage claimTask(Task t, CaseWorker cw) {
		Task taskInEm = em.find(t.getClass(), t.getId());
		if (taskInEm != null) {
			if (taskInEm.getClaimant() == null) {
				taskInEm.setClaimant(cw);
				em.merge(taskInEm);
			}
		} else {
			// TODO return error message
		}
		return null;
	}

	@Override
	public ServiceMessage unclaimTask(Task t, CaseWorker cw) {
		Task taskInEm = em.find(t.getClass(), t.getId());
		if (taskInEm != null) {
			if (taskInEm.getClaimant().getId() == cw.getId() || cw.isAdmin()) {
				taskInEm.setClaimant(null);
				em.merge(taskInEm);
			}
		} else {
			// TODO return error message
		}
		return null;
	}

	@Override
	public ServiceMessage transitionTask(Task t, CaseWorker cw, StageTaskTransitions transition) {
		Task taskInEm = em.find(t.getClass(), t.getId());
		if (t.getState().equals(taskInEm.getState())) {
			StageTaskTransitionController sttctrl = new StageTaskTransitionController();
			TaskTransitionCommand sttcomm = StageTaskTransitionCommandFactory.getCommand(transition, taskInEm);
			sttctrl.saveCommand(sttcomm);
			sttctrl.executeCommand();

			em.merge(taskInEm);
		} else {
			// notify of changed state
		}

		// TODO return message
		return null;
	}

	@Override
	public List<HumanTask> getAllClaimedTasksByCaseWorker(CaseWorker cw) {
		TypedQuery<HumanTask> query = em.createQuery("SELECT t FROM HumanTask t WHERE t.claimant.id= :claimaintId",
				HumanTask.class);
		query.setParameter("claimaintId", cw.getId());
//		query.setParameter("available", TaskStates.AVAILABLE.toString());
//		query.setParameter("enabled", TaskStates.ENABLED.toString());
		List<HumanTask> tasks = query.getResultList();
		return tasks;
	}

	@Override
	public ProcessTask findProcessTaskById(long id) {
		TypedQuery<ProcessTask> query = em.createQuery("SELECT pt FROM ProcessTask pt WHERE pt.id= :givenId",
				ProcessTask.class);
		query.setParameter("givenId", id);
		List<ProcessTask> tasks = query.getResultList();
		ProcessTask foundPt = tasks.get(0);
		return foundPt;
	}

	@Override
	public ServiceMessage mergeTask(Task t) {
		em.merge(t);
		return null;
	}

	/*
	 * 
	 * Original with Task queries, not HumanTask queries
	 * 
	 * 
	 * @PersistenceContext(unitName = "urlaubsantrag") private EntityManager em;
	 * 
	 * public TaskServiceImpl() {
	 * 
	 * }
	 * 
	 * @Override public List<Task> getAllTasks() { TypedQuery<Task> query =
	 * em.createQuery("SELECT t FROM Task t", Task.class); List<Task> tasks =
	 * query.getResultList(); return tasks; }
	 * 
	 * @Override public List<Task> getAllTasksByCase(CaseModel cm) {
	 * TypedQuery<Task> query =
	 * em.createQuery("SELECT t FROM Task t WHERE t.caseRef.id = :caseId",
	 * Task.class); long caseId = cm.getId(); query.setParameter("caseId", caseId);
	 * 
	 * List<Task> tasks = query.getResultList(); return tasks; }
	 * 
	 * @Override public List<Task> getAllClaimableTasks() { TypedQuery<Task> query =
	 * em.
	 * createQuery("SELECT t FROM Task t WHERE t.claimant IS NULL AND (t.state= :active OR t.state= :enabled)"
	 * , Task.class); query.setParameter("active", TaskStates.ACTIVE.toString());
	 * query.setParameter("enabled", TaskStates.ENABLED.toString()); List<Task>
	 * tasks = query.getResultList(); return tasks; }
	 * 
	 * @Override public ServiceMessage claimTask(Task t, CaseWorker cw) { Task
	 * taskInEm = em.find(t.getClass(), t.getId()); if(taskInEm != null) {
	 * if(taskInEm.getClaimant() == null) { taskInEm.setClaimant(cw);
	 * em.merge(taskInEm); } } else { // TODO return error message } return null; }
	 * 
	 * @Override public ServiceMessage unclaimTask(Task t, CaseWorker cw) { Task
	 * taskInEm = em.find(t.getClass(), t.getId()); if(taskInEm != null) {
	 * if(taskInEm.getClaimant().getId() == cw.getId() || cw.isAdmin()) {
	 * taskInEm.setClaimant(null); em.merge(taskInEm); } } else { // TODO return
	 * error message } return null; }
	 * 
	 * @Override public ServiceMessage transitionTask(Task t, CaseWorker cw,
	 * StageTaskTransitions transition) { Task taskInEm = em.find(t.getClass(),
	 * t.getId()); if(t.getState().equals(taskInEm.getState())) {
	 * StageTaskTransitionController sttctrl = new StageTaskTransitionController();
	 * TaskTransitionCommand sttcomm =
	 * StageTaskTransitionCommandFactory.getCommand(transition, taskInEm);
	 * sttctrl.saveCommand(sttcomm); sttctrl.executeCommand();
	 * 
	 * em.merge(taskInEm); } else { // notify of changed state }
	 * 
	 * // TODO return message return null; }
	 * 
	 * @Override public List<Task> getAllClaimedTasksByCaseWorker(CaseWorker cw) {
	 * TypedQuery<Task> query =
	 * em.createQuery("SELECT t FROM Task t WHERE t.claimant.id= :claimaintId",
	 * Task.class); query.setParameter("claimaintId", cw.getId()); //
	 * query.setParameter("available", TaskStates.AVAILABLE.toString()); //
	 * query.setParameter("enabled", TaskStates.ENABLED.toString()); List<Task>
	 * tasks = query.getResultList(); return tasks; }
	 */

}
