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
import javax.persistence.Transient;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.CaseModel;
import cm.core.Stage;
import cm.core.utils.ProcessTaskImplementationFactory;

/**
 * <p>
 * Class representing ProcessTask elements in CMMN. Its cmId is used to get
 * {@link ProcessTaskImplementation}s. Its caseRef can be used to provide
 * necessary context.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.10, 6.8.3 and 8.4.2 for more
 * information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("process_task")
public class ProcessTask extends Task {
	@JohnzonIgnore
	@Transient
	private ProcessTaskImplementation pi;

	public ProcessTask() {
	}

	// for first level relationship to case
	/**
	 * <p>
	 * Constructs a new ProcessTask with a first level relationship to a
	 * {@link CaseModel}. Automatically adds itself to the CaseModel. Automatically
	 * sets its state to INITIAL and the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the ProcessTask directly to a
	 * CaseModel. Its (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param caseRef
	 *            reference to the CaseModel the ProcessTask is being added to
	 */
	public ProcessTask(String cmId, String name, CaseModel caseRef) {
		super(cmId, name, caseRef);
	}

	/**
	 * <p>
	 * Constructs a new ProcessTask with a second level relationship to a
	 * {@link CaseModel}. Automatically sets its state to INITIAL, the parentStage
	 * and the caseRef via the parent Stage.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the Task to a Stage. Its (persistence)
	 * id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param parentStage
	 *            reference to the Stage the ProcessTask is being added to
	 */
	public ProcessTask(String cmId, String name, Stage parentStage) {
		super(cmId, name, parentStage);
	}

	/**
	 * <p>
	 * Constructs a new ProcessTask with a second level relationship to a
	 * {@link CaseModel}. Automatically adds itself to the parent Stage.
	 * Automatically sets its state to INITIAL, the parentStage and the caseRef via
	 * the parent Stage.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the ProcessTask to a Stage. Its
	 * (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param parentStage
	 *            reference to the Stage the ProcessTask is being added to
	 */
	public ProcessTask(String id, String name) {
		super(id, name);
	}

	/**
	 * First gets the {@link ProcessTaskImplementation} from the
	 * {@link ProcessTaskImplementationFactory}, then starts the process via method
	 * startProcess().
	 */
	public void startProcess() {
		pi = getProcessImplementation(this);
		pi.startProcess();
	}

	public void executeCallBack() {
		pi = getProcessImplementation(this);
		pi.executeCallBack();
	}

	private ProcessTaskImplementation getProcessImplementation(ProcessTask pt) {
		ProcessTaskImplementation pi = ProcessTaskImplementationFactory.getProcessImplementation(this);
		return pi;
	}
}