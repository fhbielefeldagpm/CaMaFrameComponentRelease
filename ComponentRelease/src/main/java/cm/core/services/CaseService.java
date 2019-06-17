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
import cm.core.CaseElement;
import cm.core.states.CaseInstanceTransition;
import cm.core.tasks.Task;

/**
 * <p>
 * Provides interfaces to persist, access, manipulate, transition and delete
 * {@link CaseModel}s.
 * </p>
 * <p>
 * Implementations use an EntityManager and PersistenceContext.
 * </p>
 * 
 * @author André Zensen
 *
 */
public interface CaseService {

	public void persistCase(CaseModel cm);

	public CaseModel getCaseById(CaseModel cm);

	public List<CaseModel> getAllCases();
	
	public List<CaseModel> getPrimaryCases(String state);
	
	public List<CaseModel> getSecondaryCases(CaseModel cm);

	public void deleteCase(CaseModel cm);

	/**
	 * Transitions a CaseModel instance, provided the claimaint correspondends to
	 * the CaseWorker reference given as a parameter.
	 * 
	 * @param cm
	 *            a CaseModel object as a reference (its persistence id is enough)
	 * @param cw
	 *            a CaseWorker object as a reference (its persistence id is enough)
	 * @param transition
	 *            the transition to perform
	 */
	public void transitionCase(CaseModel cm, CaseWorker cw, CaseInstanceTransition transition);

	public List<CaseElement> getElementsInCase(CaseModel cm);

	public List<Task> getTasksInCase(CaseModel cm);

}
