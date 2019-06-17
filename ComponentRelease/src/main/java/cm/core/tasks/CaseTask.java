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

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.CaseModel;
import cm.core.Stage;
import cm.core.utils.CaseTaskImplementationFactory;

/**
 * <p>
 * Class representing CaseTask elements in CMMN. Its cmId is used to get
 * {@link CaseTaskImplementation}s from the
 * {@link CaseTaskImplementationFactory}. Its caseRef can be used to provide
 * necessary context. Its subCaseRef can be used to access associated
 * {@link CaseModel}s.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.4.10, 6.8.2 and 8.4.2 for more
 * information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@DiscriminatorValue("case_task")
public class CaseTask extends Task {
	@JohnzonIgnore
	@Transient
	private CaseTaskImplementation cTi;
	@JohnzonIgnore
	@OneToOne(cascade = CascadeType.ALL, mappedBy="caseTaskRef")
	private CaseModel subCaseRef;

	public CaseTask() {

	}

	public CaseModel getSubCaseRef() {
		return subCaseRef;
	}

	public void setSubCaseRef(CaseModel subCaseRef) {
		this.subCaseRef = subCaseRef;
	}
	// for first level relationship to case
	/**
	 * <p>
	 * Constructs a new CaseTask with a first level relationship to a {@link CaseModel}.
	 * Automatically adds itself to the CaseModel. Automatically sets its state to
	 * INITIAL and the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the CaseTask directly to a CaseModel. Its
	 * (persistence) id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param caseRef
	 *            reference to the CaseModel the CaseTask is being added to
	 */
	public CaseTask(String id, String name, CaseModel caseRef) {
		super(id, name, caseRef);
	}
	/**
	 * <p>
	 * Constructs a new CaseTask with a second level relationship to a
	 * {@link CaseModel}. Automatically adds itself to the parent Stage.
	 * Automatically sets its state to INITIAL, adds itself to the parentStage and
	 * the caseRef.
	 * </p>
	 * <p>
	 * Use this constructor if you are adding the CaseTask to a Stage. Its (persistence)
	 * id should be set automatically by JPA.
	 * </p>
	 * 
	 * @param cmId
	 *            the cmId which can be based on a .CMMN file mark-up
	 * @param name
	 *            a human-readable name based on the name given to a CMMN model
	 *            element
	 * @param parentStage
	 *            reference to the Stage the CaseTask is being added to
	 */
	public CaseTask(String id, String name, Stage parentStage) {
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
	public CaseTask(String id, String name) {
		super(id, name);
	}
	/**
	 * First gets the {@link CaseTaskImplementation} from the
	 * {@link CaseTaskImplementationFactory}, then starts the process via method
	 * startCase().
	 */
	public void startCase() {
		cTi = getCaseTaskImplementation(this);
		cTi.startCase();
	}

	private CaseTaskImplementation getCaseTaskImplementation(CaseTask ct) {
		CaseTaskImplementation cTi = CaseTaskImplementationFactory.getCaseTaskImplementation(this);
		return cTi;
	}
}