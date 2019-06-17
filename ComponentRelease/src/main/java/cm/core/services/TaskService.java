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
package cm.core.services;

import java.util.List;

import cm.core.CaseModel;
import cm.core.CaseWorker;
import cm.core.rules.RepetitionRule;
import cm.core.sentries.ElementOnPart;
import cm.core.sentries.EntrySentry;
import cm.core.sentries.ExitSentry;
import cm.core.states.StageTaskTransitions;
import cm.core.tasks.HumanTask;
import cm.core.tasks.ProcessTask;
import cm.core.tasks.Task;

/**
 * <p>
 * Provides interfaces to access, manipulate, merge, clone and transition
 * {@link Task}s.
 * </p>
 * <p>
 * Implementations use an EntityManager and PersistenceContext.
 * </p>
 * 
 * @author André Zensen
 *
 */
public interface TaskService {

	public List<HumanTask> getAllTasks();

	public List<HumanTask> getAllTasksByCase(CaseModel cm);

	public List<HumanTask> getAllClaimableTasks();

	public List<HumanTask> getAllClaimableTasksByRole(CaseWorker cw);

	public List<HumanTask> getAllClaimedTasksByCaseWorker(CaseWorker cw);

	public ProcessTask findProcessTaskById(long id);

	/*
	 * Original with Task as type instead of HumanTask
	 * 
	 * public List<Task> getAllTasks(); public List<Task>
	 * getAllTasksByCase(CaseModel cm); public List<Task> getAllClaimableTasks();
	 * public List<Task> getAllClaimedTasksByCaseWorker(CaseWorker cw);
	 */

	public ServiceMessage claimTask(Task t, CaseWorker cw);

	public ServiceMessage unclaimTask(Task t, CaseWorker cw);

	public ServiceMessage transitionTask(Task t, CaseWorker cw, StageTaskTransitions transition);

	/**
	 * Creates a new instance of a given {@link Task}. Used for repetition
	 * mechanism. Acquires given Task from EntityManager. Its details are copied
	 * except for its persistence id. The clone is then persisted. {@link ElementOnPart} observers and
	 * {@link ExitSentry}s are copied before the persisted copy is merged. {@link EntrySentry} are
	 * not copied since the original Task will still trigger the creation of new
	 * instances if its {@link RepetitionRule} holds.
	 * 
	 * @param t
	 * @return
	 */
	public ServiceMessage createNewInstance(Task t);

	public ServiceMessage mergeTask(Task t);
}
