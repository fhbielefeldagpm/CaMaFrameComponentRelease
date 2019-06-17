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
package componentrelease.impl.casetask;

import cm.core.CaseModel;
import cm.core.tasks.CaseTask;
import cm.core.tasks.CaseTaskImplementation;
import cm.core.utils.CaseFactory;

public class CreateSpecificationsCaseTaskImplementation extends CaseTaskImplementation {

	public CreateSpecificationsCaseTaskImplementation(CaseTask caseTask) {
		super(caseTask);
	}

	@Override
	public void startCase() {
		CaseModel cm;
		if (this.caseTask.getParentStage() != null) {
			cm = this.caseTask.getRootCase();
		} else {
			cm = this.caseTask.getCaseRef();
		}
		String parentCaseName = cm.getName();
		CaseModel subCase = CaseFactory.getCreateTechnicalSpecificationsCaseModel(parentCaseName);
		this.caseTask.setSubCaseRef(subCase);
		subCase.setCaseTaskRef(this.caseTask);
	}

}
