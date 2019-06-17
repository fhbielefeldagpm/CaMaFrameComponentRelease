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
package componentrelease.impl.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.views.CaseWorkerInfo;
import com.vaadin.cdi.views.NavigationEvent;
import com.vaadin.cdi.views.TaskInfo;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;

import cm.core.CaseModel;
import cm.core.data.CaseFile;
import cm.core.data.CaseFileItem;
import cm.core.data.CaseFileItemAttachment;
import cm.core.data.SimpleProperty;
import cm.core.services.CaseFileService;
import cm.core.services.TaskService;
import cm.core.states.CaseFileItemTransition;
import cm.core.states.StageTaskTransitions;
import cm.core.tasks.HumanTask;

@CDIView("review")
public class ReviewSpecifications extends CustomComponent implements View {

	@Inject
	private TaskService taskService;
	@Inject
	private CaseWorkerInfo caseWorkerInfo;
	@Inject
	private CaseFileService cfService;

	@Inject
	TaskInfo taskInfo;

	@Inject
	private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

	private AttachmentGrid attachmentGrid;
	private AttachmentReceiver attachmentReceiverAndSucceedListener;
	private Upload attachmentUpload;

	private CaseFileItem item;

	private Button btnApprove;
	private Button btnRevise;
	
	private TextArea tAnoteForRevision;

	// TODO upload spec files via vaadin Upload class and Receiver-implementations

	@Override
	public void enter(ViewChangeEvent event) {
		Long caseModelRefId = taskInfo.getTask().getCaseRef().getId();
		CaseModel shallowCase = new CaseModel();
		shallowCase.setId(caseModelRefId);
		try {
			setupFields();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Layout layout = buildLayout();
		setCompositionRoot(layout);
		updateAttachmentGrid();
	}

	private void updateAttachmentGrid() {
		CaseModel modelRef = this.taskInfo.getTask().getCaseRef();
		CaseFile caseFile = cfService.getCaseFile(modelRef);
		item = caseFile.getCaseFileItemById("specifications");
		attachmentGrid.updateList(item.getAttachments());

	}

	private void setupFields() throws ParseException {

	}

	private Layout buildLayout() {
		VerticalLayout layout = new VerticalLayout();
		Label instructions = new Label(
				"Please use the form below to upload the specifications."
				+ " A direct upload to the database is used, so please keep your file size to <= 1MB."
				+ " Else, further configuration is necessary.");
		layout.addComponents(instructions);

		Button viewBtn = new Button("Download Selected");
		viewBtn.setEnabled(false);

		viewBtn.addClickListener(e -> {

		});

		Button deleteBtn = new Button("Delete Selected");
		deleteBtn.setEnabled(false);

		deleteBtn.addClickListener(e -> {
			if (this.attachmentGrid.getSelectedAttachment() != null) {
				long attachmentId = this.attachmentGrid.getSelectedAttachment().getId();
				cfService.deleteAttachment(item, cfService.getAttachmentById(attachmentId));
				this.updateAttachmentGrid();
				deleteBtn.setEnabled(false);
			}
		});

		attachmentGrid = new AttachmentGrid(null, viewBtn, deleteBtn);
		attachmentReceiverAndSucceedListener = new AttachmentReceiver((HumanTask) this.taskInfo.getTask(),
				this.attachmentGrid);
		attachmentUpload = new Upload(null, attachmentReceiverAndSucceedListener);

		// upload configuration
		attachmentUpload.setButtonCaption("Upload Attachment");
		attachmentUpload.addSucceededListener(this.attachmentReceiverAndSucceedListener);
		attachmentUpload.addFinishedListener(e -> {
			this.updateAttachmentGrid();
		});

		layout.addComponents(attachmentGrid);
		HorizontalLayout gridBtnLayout = new HorizontalLayout(attachmentUpload, viewBtn, deleteBtn);
		layout.addComponent(gridBtnLayout);
		
		Label lblNoteForRevision = new Label("Please enter a reason for revision.");
		tAnoteForRevision = new TextArea();
		layout.addComponents(lblNoteForRevision, tAnoteForRevision);
		
		btnApprove = generateApproveButton();
		btnRevise = generateReviseButton();
		HorizontalLayout btnLayout = new HorizontalLayout(btnApprove, btnRevise);
		layout.addComponent(btnLayout);

		HorizontalLayout navBtnLayout = new HorizontalLayout(generateBackButton(), generateTaskListButton());
		layout.addComponents(navBtnLayout);
		return layout;
	}

	private Button generateApproveButton() {
		Button completeButton = new Button("Approve Specifications");
		completeButton.addClickListener(e -> {
			CaseModel modelRef = this.taskInfo.getTask().getCaseRef();
			CaseFile caseFile = cfService.getCaseFile(modelRef);
			CaseFileItem item = caseFile.getCaseFileItemById("specifications");
			
			SimpleProperty prop = item.getProperty("dataApproved");
			prop.setValue(Boolean.toString(Boolean.TRUE));
			cfService.updateProperty(prop);
			
			prop = item.getProperty("revisionNeeded");
			prop.setValue(Boolean.toString(Boolean.FALSE));
			cfService.updateProperty(prop);
			
			prop = item.getProperty("dataProvided");
			prop.setValue(Boolean.toString(Boolean.TRUE));
			cfService.updateProperty(prop);
			
			taskService.transitionTask(taskInfo.getTask(), caseWorkerInfo.getUser(), StageTaskTransitions.complete);
			cfService.transitionCaseFileItem(item, CaseFileItemTransition.update);
			btnApprove.setEnabled(false);
			btnRevise.setEnabled(false);
		});
		return completeButton;
	}
	
	private Button generateReviseButton() {
		Button reviseButton = new Button("Send back for Revision");
		reviseButton.addClickListener(e -> {
			String note = tAnoteForRevision.getValue();
			if(note != "") {
				CaseModel modelRef = this.taskInfo.getTask().getCaseRef();
				CaseFile caseFile = cfService.getCaseFile(modelRef);
				CaseFileItem item = caseFile.getCaseFileItemById("specifications");
				SimpleProperty noteProp = item.getProperty("noteForRevision");				
				noteProp.setValue(note);
				cfService.updateProperty(noteProp);
				SimpleProperty prop = item.getProperty("dataApproved");
				prop.setValue(Boolean.toString(Boolean.FALSE));
				cfService.updateProperty(prop);
				
				prop = item.getProperty("revisionNeeded");
				prop.setValue(Boolean.toString(Boolean.TRUE));
				cfService.updateProperty(prop);
				
				taskService.transitionTask(taskInfo.getTask(), caseWorkerInfo.getUser(), StageTaskTransitions.complete);
				btnApprove.setEnabled(false);
				btnRevise.setEnabled(false);			
			} else {
				Notification.show("Please enter a message detailling why a revision is necessary.");
			}
		});
		return reviseButton;
	}

	private Button generateTaskListButton() {
		Button taskListButton = new Button("Task List");
		taskListButton.addClickListener(e -> {
			navigationEvent.fire(new NavigationEvent("task-list"));
		});
		return taskListButton;
	}

	private Button generateBackButton() {
		Button button = new Button("Back");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				navigationEvent
						.fire(new NavigationEvent(Conventions.deriveMappingForView(ReviewSpecifications.class)));
			}
		});
		return button;
	}
}