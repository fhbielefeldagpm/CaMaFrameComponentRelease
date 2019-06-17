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
package cm.core.tasks;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import cm.core.CaseModel;
import cm.core.Stage;

/**
 * <p>
 * Class representing HumanTask elements in CMMN. Its cmId can be used to
 * generate view implementations and its caseRef to provide necessary context.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.10, 6.8.1 and 8.4.2 for more
 * information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("human_task")
public class HumanTask extends Task {

	public HumanTask() {

	}

	// for first level relationship to case
	/**
	 * <p>
	 * Constructs a new HumanTask with a first level relationship to a
	 * {@link CaseModel}. Automatically adds itself to the CaseModel. Automatically
	 * sets its state to INITIAL and the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the HumanTask directly to a CaseModel.
	 * Its (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param caseRef
	 *            reference to the CaseModel the HumanTask is being added to
	 */
	public HumanTask(String id, String name, CaseModel caseRef) {
		super(id, name, caseRef);
	}

	/**
	 * <p>
	 * Constructs a new HumanTask with a second level relationship to a
	 * {@link CaseModel}. Automatically adds itself to the parent Stage.
	 * Automatically sets its state to INITIAL, the parentStage and the caseRef via
	 * the parent Stage.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the HumanTask to a Stage. Its
	 * (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param parentStage
	 *            reference to the Stage the HumanTask is being added to
	 */
	public HumanTask(String id, String name, Stage parentStage) {
		super(id, name, parentStage);
	}

	/**
	 * <p>
	 * Constructor available for running tests.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 */
	public HumanTask(String id, String name) {
		super(id, name);
	}
}