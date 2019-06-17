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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cm.core.CaseModel;
import cm.core.CaseWorker;
import cm.core.CaseElement;
import cm.core.Stage;
import cm.core.data.CaseFile;
import cm.core.data.CaseFileItem;
import cm.core.tasks.Task;

public class CaseInstance {

	private CaseModel cm;
	private Map<String, Task> taskRegistry;
	private Map<String, CaseFileItem> dataRegistry;
	
	
	
	public CaseInstance(CaseModel cm) {
		this.cm = cm;
		buildRegistries();
	}

	private void buildRegistries() {
		if (cm != null) {
			this.taskRegistry = new HashMap<>();
			this.dataRegistry = new HashMap<>();
			
			buildTaskRegistry(taskRegistry, cm);
			if (cm.getCaseFile() != null) {
				buildDataRegistry(dataRegistry, cm.getCaseFile());
			}
		}
	}
	
	private void buildTaskRegistry(Map<String, Task> taskRegistry, CaseElement parent) {
		Collection<CaseElement> children = null;
		if (parent instanceof CaseModel) {
			CaseModel cm = (CaseModel) parent;
			children = cm.getChildElements();
		} else if (parent instanceof Stage) {
			Stage s = (Stage) parent;
			children = s.getChildElements();
		}
		for(CaseElement e : children) {
			if(e instanceof Task) {
				taskRegistry.put(e.getCmId(), (Task) e);
			} else if (e instanceof Stage) {
				buildTaskRegistry(this.taskRegistry, e);
			}
		}
	}
	
	private void buildDataRegistry(Map<String, CaseFileItem> dataRegistry, CaseFile cf) {
		for(CaseFileItem cfi : cf.getCaseFileItems()) {
			dataRegistry.put(cfi.getCmId(), cfi);
//			if(cfi.getChildren() != null) {
//				if(cfi.getChildren().size() > 0) {
//					
//				}
//			}
			// TODO cover children as well or keep as simple entry to children?
		}
	}
	
	public long getCaseId() {
		return this.cm.getId();
	}
	public String getCaseCmId() {
		return this.cm.getCmId();
	}
	public String getCaseName() {
		return this.cm.getName();
	}
	public String getCaseState() {
		return this.cm.getState();
	}
	public CaseModel getCaseModel() {
		return this.cm;
	}
	
	public List<Task> getAllTasks() {
		return new ArrayList<Task>(taskRegistry.values());
		// TODO deep copy instead?
	}
	
	public List<Task> getAllTasks(String stateFilter) {
		ArrayList<Task> availableTasks = new ArrayList<>();
		for(Task t : taskRegistry.values()) {
			if(t.getClaimant() == null && (t.getState().equals(stateFilter))) {
				availableTasks.add(t);
			}
		}
		return availableTasks;
		// TODO deep copy instead?
	}
	
	public List<CaseFileItem> getAllCaseFileItems() {
		ArrayList<CaseFileItem> availableCaseFileItems = new ArrayList<>();
		for(CaseFileItem cfi : dataRegistry.values()) {
				availableCaseFileItems.add(cfi);
		}
		return availableCaseFileItems;
		// TODO deep copy instead?
	}
	
	public List<Task> getAllTasksByClaimaint(CaseWorker cw) {
		ArrayList<Task> claimaintTasks = new ArrayList<>();
		for(Task t : taskRegistry.values()) {
			if(t.getClaimant().getId() == cw.getId()) {
				claimaintTasks.add(t);
			}
		}
		return claimaintTasks;
	}
}
