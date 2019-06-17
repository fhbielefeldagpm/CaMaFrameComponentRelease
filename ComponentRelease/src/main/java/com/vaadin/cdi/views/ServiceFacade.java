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
package com.vaadin.cdi.views;

import javax.inject.Inject;

import com.vaadin.cdi.NormalUIScoped;

import cm.core.services.CaseWorkerService;
import cm.core.services.TaskService;

@NormalUIScoped
public class ServiceFacade {

	@Inject
	private TaskService taskService;

	@Inject
	private CaseWorkerService caseWorkerService;

	public TaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public CaseWorkerService getCaseWorkerService() {
		return caseWorkerService;
	}

	public void setCaseWorkerService(CaseWorkerService caseWorkerService) {
		this.caseWorkerService = caseWorkerService;
	}
}
