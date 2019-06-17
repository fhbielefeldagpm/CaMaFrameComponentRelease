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

import cm.core.CaseWorker;
import cm.core.services.CaseWorkerService;
import cm.core.services.ServiceMessage;
import cm.core.utils.PersistenceSettings;

@Stateless
public class CaseWorkerServiceImpl implements CaseWorkerService {

	@PersistenceContext(unitName = PersistenceSettings.persistenceContextUnitName)
	EntityManager em;
	
	@Override
	public List<CaseWorker> getAllCaseWorkers() {
		TypedQuery<CaseWorker> query = em.createQuery("SELECT cw FROM CaseWorker cw", CaseWorker.class);
		List<CaseWorker> list = query.getResultList();
		return list;
	}

	@Override
	public ServiceMessage persistCaseWorker(CaseWorker cw) {
		em.persist(cw);
		return null;
	}

	@Override
	public ServiceMessage updateCaseWorker(CaseWorker cw) {
		TypedQuery<CaseWorker> query = em.createQuery("SELECT cw FROM CaseWorker cw WHERE cw.id = :cwId", CaseWorker.class);
		query.setParameter("cwId", cw.getId());
		CaseWorker fetched = query.getSingleResult();
		em.merge(cw);
		return null;
	}

	@Override
	public ServiceMessage deleteCaseWorker(CaseWorker cw) {
		TypedQuery<CaseWorker> query = em.createQuery("SELECT cw FROM CaseWorker cw WHERE cw.id = :cwId", CaseWorker.class);
		query.setParameter("cwId", cw.getId());
		CaseWorker fetched = query.getSingleResult();
		if(fetched != null) {
			em.remove(fetched);
		}
		return null;
	}

	@Override
	public CaseWorker getCaseWorkerById(CaseWorker cw) {
		TypedQuery<CaseWorker> query = em.createQuery("SELECT cw FROM CaseWorker cw WHERE cw.id = :cwId", CaseWorker.class);
		query.setParameter("cwId", cw.getId());
		CaseWorker fetched = query.getSingleResult();
		return fetched;
	}

	@Override
	public CaseWorker getCaseWorkerByLogin(String user, String pass) {
		TypedQuery<CaseWorker> query = em.createQuery("SELECT cw FROM CaseWorker cw WHERE cw.user = :user AND cw.pass = :pass", CaseWorker.class);
		query.setParameter("user", user);
		query.setParameter("pass", pass);
		List<CaseWorker> fetched = query.getResultList();
		
		CaseWorker result = null;
		if(fetched.size() > 0) {
			result = fetched.get(0);
		}
		return result;
	}

}
