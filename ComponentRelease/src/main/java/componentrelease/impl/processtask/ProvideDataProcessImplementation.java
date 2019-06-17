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
package componentrelease.impl.processtask;

import javax.enterprise.inject.spi.CDI;

import cm.core.CaseModel;
import cm.core.data.CaseFileItem;
import cm.core.data.CaseFileItemAttachment;
import cm.core.services.CaseFileService;
import cm.core.states.CaseFileItemTransition;
import cm.core.tasks.ProcessTask;
import cm.core.tasks.ProcessTaskImplementation;

public class ProvideDataProcessImplementation extends ProcessTaskImplementation {


	public ProvideDataProcessImplementation(ProcessTask processTask) {
		super(processTask);
	}

	@Override
	public void startProcess() {
		System.out.println("Process Execution of ProcessTask " + this.processTask.getCmId() + ", " + this.processTask.getId());
		
		Long primaryCaseId = this.processTask.getRootCase().getCaseTaskRef().getRootCase().getId();
		CaseModel shallowCaseModel = new CaseModel();
		shallowCaseModel.setId(primaryCaseId);
		CaseFileService cfService = CDI.current().select(CaseFileService.class).get();
		CaseFileItem primaryItem = cfService.getCaseFileItem(shallowCaseModel, "specifications");
		CaseFileItem secondaryItem = cfService.getCaseFileItem(this.processTask.getRootCase(), "specifications");
		for(CaseFileItemAttachment att : secondaryItem.getAttachments()) {
			cfService.saveAttachment(primaryItem, att);
		}	
		cfService.transitionCaseFileItem(primaryItem, CaseFileItemTransition.create);
		this.processTask.getContextState().complete();
		// TODO Auto-generated method stub
		// get the specifications of this CaseModel and transfer it to parent CaseModel via the CaseTask reference of this CaseModel
	}
	
	@Override
	public void executeCallBack() {
		System.out.println("CALLBACK");
//		TaskService tService = CDI.current().select(TaskService.class).get();
//		ProcessTask thisPt = tService.findProcessTaskById(this.processTask.getId());
//		thisPt.setName("CALLBACK SUCCESSFUL");
//		tService.mergeTask(thisPt);
		}

}
