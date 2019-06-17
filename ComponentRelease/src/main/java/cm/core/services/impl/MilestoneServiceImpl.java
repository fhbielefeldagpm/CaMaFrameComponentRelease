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
import cm.core.Milestone;
import cm.core.listeners.EventMilestoneStates;
import cm.core.services.MilestoneService;
import cm.core.utils.PersistenceSettings;

@Stateless
public class MilestoneServiceImpl implements MilestoneService {

	@PersistenceContext(unitName = PersistenceSettings.persistenceContextUnitName)
	EntityManager em;

	@Override
	public List<Milestone> getAllMilestones(CaseModel cm) {
		TypedQuery<Milestone> query = em.createQuery("SELECT m FROM Milestone m WHERE m.caseRef.id = :caseId",
				Milestone.class);
		long caseId = cm.getId();
		query.setParameter("caseId", caseId);
		List<Milestone> milestones = query.getResultList();
		return milestones;
	}

	@Override
	public List<Milestone> getMilestonesByState(CaseModel cm, EventMilestoneStates state) {
		TypedQuery<Milestone> query = em.createQuery(
				"SELECT m FROM Milestone m WHERE m.caseRef.id = :caseId AND m.state = :state", Milestone.class);
		long caseId = cm.getId();
		query.setParameter("caseId", caseId);
		String stateAsString = state.toString();
		query.setParameter("state", stateAsString);
		List<Milestone> milestones = query.getResultList();
		return milestones;
	}

}
