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
package cm.core.repository;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import cm.core.CaseModel;
import cm.core.CaseWorker;
import cm.core.tasks.Task;

public class CaseRepositoryImpl implements CaseRepository {

	private HashMap<Long, CaseInstance> caseInstances = new HashMap<>();
	private EntityManagerFactory emf;
	private EntityManager em; // TODO inject / refactor to hasve it in each instance instead

	public CaseRepositoryImpl() {
		emf = Persistence.createEntityManagerFactory("cmcore"); // TODO include unit name in properties in central place
		em = emf.createEntityManager();
		// TODO injection, annotations EXTENDED
		// TODO IDEA: em per CaseInstance?
	}
	
	@Override
	public void addCaseModel(CaseModel cm) {
		long caseId = cm.getId();
		if(!this.caseInstances.containsKey(caseId)) {
			CaseInstance instance = new CaseInstance(cm);
			this.caseInstances.put(caseId, instance);
			em.getTransaction().begin();
			em.persist(cm);
		}
	}

	@Override
	public CaseModel getCaseById(long id) {
		if (!caseInstances.isEmpty() || caseInstances.containsKey(id)) {
			for (CaseInstance ci : caseInstances.values()) {
				if (ci.getCaseId() == id) {
					return ci.getCaseModel();
				}
			}
		} else if (caseInstances.isEmpty() || !caseInstances.containsKey(id)) {
			return fetchCaseModelFromDatabase(id);
		}
		return null;
	}

	private CaseModel fetchCaseModelFromDatabase(long id) {
		CaseModel cm = em.find(CaseModel.class, id);
		if (cm != null) {
			CaseInstance ci = new CaseInstance(cm);
			caseInstances.put(cm.getId(), ci);
		}
		return cm;
	}

	@Override
	public void detachCase(CaseModel cm) {
		long caseId = cm.getId();
		if (caseInstances.containsKey(caseId)) {
			em.getTransaction().begin();
			em.detach(cm);
			em.getTransaction().commit();
			caseInstances.remove(caseId);
		}

	}

	@Override
	public void updateCase(CaseModel cm) {
		long caseId = cm.getId();
		if (caseInstances.containsKey(caseId)) {
			em.getTransaction().begin();
			em.merge(cm);
			em.getTransaction().commit();
		} else {
			fetchCaseModelFromDatabase(caseId);
			if (caseInstances.containsKey(caseId)) {
				em.getTransaction().begin();
				em.merge(cm);
				em.getTransaction().commit();
			}
		}
	}

	@Override
	public void deleteCase(CaseModel cm) {
		long caseId = cm.getId();
		if (caseInstances.containsKey(caseId)) {
			em.getTransaction().begin();
			em.remove(cm);
			em.getTransaction().commit();
		} else {
			fetchCaseModelFromDatabase(caseId);
			if (caseInstances.containsKey(caseId)) {
				em.getTransaction().begin();
				em.merge(cm);
				em.getTransaction().commit();
			}
		}

	}

	@Override
	public void updateTask(long caseId, Task t) {
		if (caseInstances.containsKey(caseId)) {
			em.getTransaction().begin();
			em.merge(t);
			em.getTransaction().commit();
		} else {
			fetchCaseModelFromDatabase(caseId);
			em.getTransaction().begin();
			em.merge(t);
			em.getTransaction().commit();
		}
	}

	@Override
	public void updateTask(CaseModel cm, Task t) {
		long caseId = cm.getId();
		if (caseInstances.containsKey(caseId)) {
			em.getTransaction().begin();
			em.merge(t);
			em.getTransaction().commit();
		} else {
			fetchCaseModelFromDatabase(caseId);
			em.getTransaction().begin();
			em.merge(t);
			em.getTransaction().commit();
		}

	}

	@Override
	public List<Task> getAllTasksByCase(long caseId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> getAllTasksByState(CaseModel cm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> getAllTasksByClaimaint(CaseWorker cw) {
		// TODO Auto-generated method stub
		return null;
	}
}
