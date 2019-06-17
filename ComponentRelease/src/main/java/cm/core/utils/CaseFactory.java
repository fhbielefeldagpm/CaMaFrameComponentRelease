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
package cm.core.utils;

import java.util.ArrayList;
import java.util.List;

import cm.core.CaseModel;
import cm.core.CaseRole;
import cm.core.Milestone;
import cm.core.Stage;
import cm.core.data.CaseFileItem;
import cm.core.data.MultiplicityEnum;
import cm.core.data.SimpleProperty;
import cm.core.listeners.EventListener;
import cm.core.rules.RepetitionRule;
import cm.core.rules.RequiredRule;
import cm.core.sentries.CaseFileItemOnPart;
import cm.core.sentries.ElementOnPart;
import cm.core.sentries.EntrySentry;
import cm.core.sentries.ExitSentry;
import cm.core.sentries.IfPart;
import cm.core.states.CaseFileItemTransition;
import cm.core.states.EventMilestoneTransitions;
import cm.core.states.StageTaskTransitions;
import cm.core.tasks.CaseTask;
import cm.core.tasks.HumanTask;
import cm.core.tasks.ProcessTask;

/**
 * <p>Central factory class with methods providing {@link CaseModel} blueprints to
 * be persisted. Can be used to store and reference CaseRoles as enumerations.</p>
 * 
 * @author André Zensen
 *
 */
public class CaseFactory {

	public enum CaseRoles {
		developer, reviewer
	}

	public enum CaseModelNames {
		Component_Release
	}
	
	public enum SubCaseModelNames {
		Create_Technical_Specifications
	}

	public static List<String> getCaseModelNames() {
		ArrayList<String> caseRoles = new ArrayList<>();
		for (CaseModelNames name : CaseModelNames.values()) {
			caseRoles.add(name.toString());
		}
		return caseRoles;
	}
	
	public static List<String> getInstantiableCaseModelNames() {
		ArrayList<String> caseRoles = new ArrayList<>();
		for (CaseModelNames name : CaseModelNames.values()) {
			caseRoles.add(name.toString());
		}
		return caseRoles;
	}

	public static CaseModel getCaseModelByName(String caseName) {
		if (caseName.equals(CaseModelNames.Component_Release.toString())) {
			CaseModel componentRelease = getComponentReleaseCaseModel();
			return componentRelease;
		}
		return null;
	}

	public static List<CaseRole> getRolesUsed() {
		ArrayList<CaseRole> caseRoles = new ArrayList<>();
		for (CaseRoles role : CaseRoles.values()) {
			CaseRole newRole = new CaseRole(role.toString());
			caseRoles.add(newRole);
		}
		return caseRoles;
	}

	public static CaseModel getComponentReleaseCaseModel() {
		CaseModel model = new CaseModel("Component_Release", "Component Release");
		model.setAutoComplete(true);
		CaseFileItem specifications = new CaseFileItem("specifications", MultiplicityEnum.OneOrMore.toString(),
				"Specifications");
		model.getCaseFile().addCaseFileItem(specifications);

		Stage createTechnicalSpecifications = new Stage("createTechSpecs", "Create Technical Specifications", model);
		createTechnicalSpecifications.setAutoComplete(true);
		CaseTask createSpecifications = new CaseTask("createSpecs", "Create Specifications",
				createTechnicalSpecifications);
		
		Milestone specificationsCreated = new Milestone("specificationsCreated", "Specifications Created", model);
		EventListener cancel = new EventListener("cancel", "Cancel", model);

		ExitSentry exitCase = new ExitSentry("exitCase", "exitCase", model);
		ElementOnPart cancelCase = new ElementOnPart(exitCase, cancel, EventMilestoneTransitions.occur.toString());

		EntrySentry entryMsSpecsCreated = new EntrySentry("enterMsSpecsCreated", "entryMilestone",
				specificationsCreated);
		CaseFileItemOnPart fileItemOnPart = new CaseFileItemOnPart(entryMsSpecsCreated, specifications,
				CaseFileItemTransition.create.toString());
		model.getContextState().create();

		return model;
	}

	public static CaseModel getCreateTechnicalSpecificationsCaseModel(String parentCaseName) {
		CaseModel model = new CaseModel(
				SubCaseModelNames.Create_Technical_Specifications.toString() + " - " + parentCaseName,
				SubCaseModelNames.Create_Technical_Specifications.toString().replace("_", " ") + " - " + parentCaseName);
		model.setAutoComplete(true);

		CaseFileItem specifications = new CaseFileItem("specifications", MultiplicityEnum.OneOrMore.toString(),
				"Specifications");
		model.getCaseFile().addCaseFileItem(specifications);
		SimpleProperty dataProvided = new SimpleProperty("dataProvided", Boolean.toString(false));
		specifications.addProperty(dataProvided);
		SimpleProperty dataApproved = new SimpleProperty("dataApproved", Boolean.toString(false));
		SimpleProperty noteForRevision = new SimpleProperty("noteForRevision", "");
		SimpleProperty revisionNeeded = new SimpleProperty("revisionNeeded", Boolean.toString(false));
		specifications.addProperty(dataApproved, revisionNeeded, noteForRevision);
		specifications.getContextState().create();

		HumanTask assembleSpecifications = new HumanTask("assemble", "Assemble Specifications", model);
		
		HumanTask reviewSpecifications = new HumanTask("review", "Review Specifications", model);
		reviewSpecifications.setRepetitionRule(new RepetitionRule("repeatReview", specifications));

		HumanTask reviseSpecifications = new HumanTask("revise", "Revise Specifications", model);
		reviseSpecifications.setRepetitionRule(new RepetitionRule("repeatRevise", specifications));

		ProcessTask provideData = new ProcessTask("provideData", "Provide parent case with data", model);
		provideData.setRequiredRule(new RequiredRule("reqProvideData", specifications));

		EntrySentry enterReviewSpecificationsFromAssemble = new EntrySentry("enterReviewSpecifications",
				"enterReviewSpecifications", reviewSpecifications);
		ElementOnPart ht_reviewSpecifications = new ElementOnPart(enterReviewSpecificationsFromAssemble,
				assembleSpecifications, StageTaskTransitions.complete.toString());

		EntrySentry enterReviewSpecificationsFromRevise = new EntrySentry("enterReviewSpecificationsFromRevise",
				"enterReviewSpecificationsFromRevise", reviewSpecifications);
		ElementOnPart ht_reviewSpecifications2 = new ElementOnPart(enterReviewSpecificationsFromRevise,
				reviseSpecifications, StageTaskTransitions.complete.toString());

		EntrySentry enterReviewSpecificationsFromCaseFile = new EntrySentry("enterReviewSpecificationsFromCaseFile",
				"enterReviewSpecificationsFromCaseFile", reviewSpecifications);
		CaseFileItemOnPart ht_reviewSpecifications3 = new CaseFileItemOnPart(enterReviewSpecificationsFromCaseFile,
				specifications, CaseFileItemTransition.update.toString());

		EntrySentry enterReviseSpecifications = new EntrySentry("enterReviseSpecifications",
				"enterReviseSpecifications", reviseSpecifications);
		ElementOnPart ht_reviseSpecifications = new ElementOnPart(enterReviseSpecifications, reviewSpecifications,
				StageTaskTransitions.complete.toString());
		IfPart ifRevision = new IfPart("ifRevision", enterReviseSpecifications, specifications);

		EntrySentry enterProvideData = new EntrySentry("enterProvideData", "enterProvideData", provideData);
		CaseFileItemOnPart cfi_specifications = new CaseFileItemOnPart(enterProvideData, specifications,
				CaseFileItemTransition.update.toString());
		IfPart ifApproved = new IfPart("ifApproved", enterProvideData, specifications);

		model.getContextState().create();
		return model;
	}

}
