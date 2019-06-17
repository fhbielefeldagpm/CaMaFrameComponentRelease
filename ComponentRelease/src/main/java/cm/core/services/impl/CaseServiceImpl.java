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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cm.core.CaseModel;
import cm.core.CaseStates;
import cm.core.CaseWorker;
import cm.core.CaseElement;
import cm.core.Stage;
import cm.core.services.CaseService;
import cm.core.services.commands.casemodel.CaseModelTransitionCommand;
import cm.core.services.commands.casemodel.CaseModelTransitionCommandFactory;
import cm.core.services.commands.casemodel.CaseModelTransitionController;
import cm.core.states.CaseInstanceTransition;
import cm.core.tasks.CaseTask;
import cm.core.tasks.Task;
import cm.core.utils.PersistenceSettings;

@Stateless
public class CaseServiceImpl implements CaseService {

	@PersistenceContext(unitName = PersistenceSettings.persistenceContextUnitName)
	private EntityManager em;

	public CaseServiceImpl() {

	}

	@Override
	public void persistCase(CaseModel cm) {
		em.persist(cm);
	}

	@Override
	public List<CaseModel> getAllCases() {
		TypedQuery<CaseModel> query = em.createQuery("SELECT c FROM CaseModel c", CaseModel.class);
		List<CaseModel> cases = query.getResultList();
		return cases;
	}

	@Override
	public List<CaseModel> getPrimaryCases(String state) {
		TypedQuery<CaseModel> query = null;
		if (!state.equals("all")) {
			query = em.createQuery("SELECT c FROM CaseModel c WHERE c.caseTaskRef IS NULL AND c.state = :state",
					CaseModel.class);
			if (state != null) {
				query.setParameter("state", state);
			}
		} else if (state.equals("all")) {
			query = em.createQuery("SELECT c FROM CaseModel c WHERE c.caseTaskRef IS NULL",
					CaseModel.class);
		}
		List<CaseModel> cases = query.getResultList();
//		for(CaseModel c : cases) {
//			em.detach(c);
//		}
		return cases;
	}

	@Override
	public List<CaseModel> getSecondaryCases(CaseModel cm) {
		TypedQuery<CaseTask> ct_query = em.createQuery("SELECT ct FROM CaseTask ct WHERE ct.subCaseRef IS NOT NULL AND ct.rootCase.id = :caseId", CaseTask.class);
		ct_query.setParameter("caseId", cm.getId());
		List<CaseTask> caseTasksWithSubCase = ct_query.getResultList();
		if(caseTasksWithSubCase.size() > 0) {
			ArrayList<Long> subcaseIds = new ArrayList<>();
			for(CaseTask ct : caseTasksWithSubCase) {
				subcaseIds.add(ct.getSubCaseRef().getId());
			}
			
			TypedQuery<CaseModel> query = em.createQuery("SELECT c FROM CaseModel c WHERE c.id IN :caseTaskIds",
					CaseModel.class);
			query.setParameter("caseTaskIds", subcaseIds);
			List<CaseModel> cases = query.getResultList();
//			for(CaseModel c : cases) {
//				em.detach(c);
//			}
			return cases;
		}
		return null;
	}

	private StringBuilder builder = new StringBuilder();
	private String nl = "\n";
	@Override
	public void deleteCase(CaseModel cm) {
		
 				

		TypedQuery<CaseModel> query = em.createQuery("SELECT c FROM CaseModel c WHERE c.id = :caseId", CaseModel.class);
		query.setParameter("caseId", cm.getId());
		CaseModel fetchedCase = query.getSingleResult();
		em.refresh(fetchedCase);
		em.remove(fetchedCase);
//		if (fetchedCase != null) {
//
//			List<Element> caseChildElements = fetchedCase.getChildElements();
//			for (int i = 0; i < caseChildElements.size(); i++)	{
//				Element child = caseChildElements.get(i);
//				if(child instanceof Stage) {
//					removeElementsInStage((Stage)child);
//				} else if (child instanceof CaseTask) {
//					removeSubCaseFromCaseTask((CaseTask)child);
//				}
//				child.setCaseRef(null);
//				caseChildElements.remove(child);
//				em.remove(child);
//			}			
//			em.remove(fetchedCase);
//		}
	}
	
	private void removeSubCaseFromCaseTask(CaseTask ct) {
		CaseModel subcase = ct.getSubCaseRef();
		subcase.setCaseTaskRef(null);
		ct.setSubCaseRef(null);
		deleteCase(subcase);
	}
	
	private void removeElementsInStage(Stage s) {
		List<CaseElement> stageChildElements = s.getChildElements();
		for (int i = 0; i < stageChildElements.size(); i++)	{
			CaseElement child = stageChildElements.get(i);
			if(child instanceof Stage) {
				removeElementsInStage((Stage)child);
			} else if (child instanceof CaseTask) {
				removeSubCaseFromCaseTask((CaseTask)child);
			}
			child.setCaseRef(null);
			stageChildElements.remove(child);
//			em.remove(child);
		}	
	}


	@Override
	public void transitionCase(CaseModel cm, CaseWorker cw, CaseInstanceTransition transition) {
		if (cw.isAdmin()) {
			CaseModel cmInEm = em.find(CaseModel.class, cm.getId());
			CaseModelTransitionController cmttctrl = new CaseModelTransitionController();
			CaseModelTransitionCommand command = CaseModelTransitionCommandFactory.getCommand(transition, cmInEm);
			cmttctrl.saveCommand(command);
			cmttctrl.executeCommand();
			em.merge(cmInEm);
		} else {
			// TODO notify via message that user is not admin
		}
	}

	@Override
	public CaseModel getCaseById(CaseModel cm) {
		TypedQuery<CaseModel> query = em.createQuery("SELECT c FROM CaseModel c WHERE c.id = :caseId", CaseModel.class);
		query.setParameter("caseId", cm.getId());
		CaseModel fetchedCase = query.getSingleResult();
		return fetchedCase;
	}

	@Override
	public List<CaseElement> getElementsInCase(CaseModel cm) {
		TypedQuery<CaseElement> query = em.createQuery("SELECT e FROM Element e WHERE e.rootCase.id= :caseId",
				CaseElement.class);
		query.setParameter("caseId", cm.getId());
		List<CaseElement> elementsFound = query.getResultList();
		return elementsFound;
	}

	@Override
	public List<Task> getTasksInCase(CaseModel cm) {
		TypedQuery<Task> query = em.createQuery("SELECT t FROM Task t WHERE t.rootCase.id= :caseId", Task.class);
		query.setParameter("caseId", cm.getId());
		List<Task> elementsFound = query.getResultList();
		return elementsFound;
	}

}
