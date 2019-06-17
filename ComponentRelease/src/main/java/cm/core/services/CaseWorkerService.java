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

import cm.core.CaseWorker;

/**
 * <p>
 * Provides interfaces to persist, access, manipulate and delete
 * {@link CaseWorker}s.
 * </p>
 * <p>
 * Implementations use an EntityManager and PersistenceContext.
 * </p>
 * 
 * @author André Zensen
 *
 */
public interface CaseWorkerService {

	public CaseWorker getCaseWorkerById(CaseWorker cw);

	public List<CaseWorker> getAllCaseWorkers();

	public ServiceMessage persistCaseWorker(CaseWorker cw);

	public ServiceMessage updateCaseWorker(CaseWorker cw);

	public ServiceMessage deleteCaseWorker(CaseWorker cw);

	public CaseWorker getCaseWorkerByLogin(String user, String pass);
}
