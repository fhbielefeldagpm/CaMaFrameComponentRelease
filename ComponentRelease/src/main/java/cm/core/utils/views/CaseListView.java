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
package cm.core.utils.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.views.CaseWorkerInfo;
import com.vaadin.cdi.views.HeaderLayout;
import com.vaadin.cdi.views.NavigationEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import cm.core.CaseModel;
import cm.core.CaseStates;
import cm.core.CaseWorker;
import cm.core.services.CaseService;
import cm.core.services.CaseWorkerService;
import cm.core.services.TaskService;
import cm.core.utils.CaseFactory;

/**
 * <p>
 * Simple Vaadin-CDI-based implementation of a view to list, start and delete
 * {@link CaseModel} blueprints.
 * </p>
 * 
 * @author André Zensen
 *
 */
@CDIView
public class CaseListView extends CustomComponent implements View {

	@Inject
	private CaseWorkerService cwService;
	@Inject
	private TaskService taskService;
	@Inject
	private CaseWorkerInfo caseWorkerInfo;
	@Inject
	private CaseService caseService;

	private CaseModel selectedPrimaryCase;
	private CaseModel selectedSecondaryCase;

	@Inject
	private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

	private Grid<CaseModel> gridPrimaryCases = new Grid<>();
	private Grid<CaseModel> gridSecondaryCases = new Grid<>();
	private Button btnRefreshCases;
	private Button btnStartCase;
	private Button btnDeleteCase;

	@Override
	public void enter(ViewChangeEvent event) {
		String parameters = event.getParameters();
		Layout layout = buildTaskListLayout();
		setCompositionRoot(layout);
		updatePrimaryCases();
	}

	private Layout buildTaskListLayout() {
		VerticalLayout layout = new VerticalLayout();
		
		// --- header
		HorizontalLayout header = new HorizontalLayout();
		Label loggedInUser = new Label("Logged in as: " + caseWorkerInfo.getUser().getLastname() + ", "
				+ caseWorkerInfo.getUser().getFirstname());
		Button logoutBtn = new Button("Logout");
		logoutBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				caseWorkerInfo.setUser(null);
				selectedPrimaryCase = null;
				selectedSecondaryCase = null;
				caseService = null;
				gridPrimaryCases = null;
				gridSecondaryCases = null;
				caseWorkerInfo = null;
				navigationEvent.fire(new NavigationEvent("login"));
			}
		});
		
		// Find the application directory
		String basepath = VaadinService.getCurrent()
		                  .getBaseDirectory().getAbsolutePath();

		// Image as a file resource
		FileResource resource = new FileResource(new File(basepath +
		                        "/VAADIN/CamaFrameLogo_transparent_header.png"));

		// Show the image in the application
		Image image = new Image(null, resource);
		
		header.addComponents(image, loggedInUser, logoutBtn);
		// --- end header
		layout.addComponent(header);
		
//		HorizontalLayout loginLyt = new HorizontalLayout();
//		Label loggedInUser = new Label("Logged in as: " + caseWorkerInfo.getUser().getLastname() + ", "
//				+ caseWorkerInfo.getUser().getFirstname());
//		loginLyt.addComponent(loggedInUser);
//		Button logoutBtn = new Button("Logout");
//		logoutBtn.addClickListener(new ClickListener() {
//			@Override
//			public void buttonClick(ClickEvent event) {
//				caseWorkerInfo.setUser(null);
//				selectedPrimaryCase = null;
//				selectedSecondaryCase = null;
//				caseService = null;
//				gridPrimaryCases = null;
//				gridSecondaryCases = null;
//				caseWorkerInfo = null;
//				navigationEvent.fire(new NavigationEvent("login"));
//			}
//		});
//		loginLyt.addComponent(logoutBtn);
//
//		layout.addComponent(loginLyt);

		Label primaryCasesList = new Label("Primary Cases");
		layout.addComponent(primaryCasesList);

		setupCaseLists();

		btnRefreshCases = generateRefreshButton();
		btnStartCase = generateStartCaseButton();
		btnDeleteCase = generateDeleteButton();

		HorizontalLayout btnLayout = new HorizontalLayout(btnDeleteCase, btnRefreshCases, btnStartCase);

		layout.addComponents(gridPrimaryCases);
		gridPrimaryCases.setSizeFull();
		gridPrimaryCases.setHeightByRows(6);
		layout.addComponent(btnLayout);
		layout.addComponents(gridSecondaryCases);
		gridSecondaryCases.setSizeFull();
		gridSecondaryCases.setHeightByRows(4);

		HorizontalLayout navBtnLayout = new HorizontalLayout(generateBackButton(), generateTaskListButton());
		layout.addComponents(navBtnLayout);
		layout.setSizeFull();
		return layout;
	}

	private Button generateTaskListButton() {
		Button taskListButton = new Button("Task List");
//		taskListButton.addClickListener(e -> {
//			navigationEvent.fire(new NavigationEvent("task-list"));
//		});
//		return taskListButton;
		taskListButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				navigationEvent.fire(new NavigationEvent("task-list"));				
			}
		});
		return taskListButton;
	}

	private Button generateStartCaseButton() {
		Button start = new Button("Start Component Release Case");
		start.addClickListener(e -> {
			startNewCase(this.caseWorkerInfo.getUser());
		});
		return start;
	}

	private Button generateRefreshButton() {
		Button refresh = new Button("Refresh");
		refresh.addClickListener(e -> {
			this.updatePrimaryCases();
		});
		return refresh;
	}

	private void startNewCase(CaseWorker cw) {
		CaseModel cm = CaseFactory.getComponentReleaseCaseModel();
		caseService.persistCase(cm);
//		cm = caseService.getCaseById(cm);
		this.updatePrimaryCases();
	}

	private void updatePrimaryCases() {
		Collection<CaseModel> items = this.caseService.getPrimaryCases("all");
		this.gridPrimaryCases.setItems(items);
		this.gridPrimaryCases.deselectAll();
		this.selectedPrimaryCase = null;
		this.selectedSecondaryCase = null;
		List<CaseModel> emptyList = new ArrayList<>();
		this.gridSecondaryCases.setItems(emptyList);
	}

	private void updateSecondaryCases() {
		Collection<CaseModel> items = this.caseService.getSecondaryCases(selectedPrimaryCase);
		if (items != null) {
			if (items.size() > 0) {
				this.gridSecondaryCases.setItems(items);
			}
		} else {
			this.gridSecondaryCases.setItems();
		}
	}

	private void setupCaseLists() {
		gridPrimaryCases.addColumn(CaseModel::getName).setCaption("Name").setExpandRatio(1);
		gridPrimaryCases.addColumn(CaseModel::getState).setCaption("State").setExpandRatio(1);
		gridPrimaryCases.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() == null) {
				selectedPrimaryCase = null;
			} else {
				selectedPrimaryCase = event.getValue();
				btnDeleteCase.setEnabled(true);

				updateSecondaryCases();
			}
		});

		gridSecondaryCases.addColumn(CaseModel::getName).setCaption("Name").setExpandRatio(1);
		gridSecondaryCases.addColumn(CaseModel::getState).setCaption("State").setExpandRatio(1);
		gridSecondaryCases.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() == null) {
				selectedSecondaryCase = null;
			} else {
				selectedSecondaryCase = event.getValue();

			}
		});

	}

	private Button generateDeleteButton() {
		Button delete = new Button("Delete Case");
		delete.addClickListener(e -> {
			CaseModel selected = selectedPrimaryCase;
			selected = caseService.getCaseById(selected);
			if (selected != null) {
				caseService.deleteCase(selected);
				updatePrimaryCases();
				selected = null;
				btnDeleteCase.setEnabled(false);
			}
		});
		delete.setEnabled(false);
		return delete;
	}

	private Button generateBackButton() {
		Button button = new Button("Back");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				navigationEvent.fire(new NavigationEvent(Conventions.deriveMappingForView(CaseListView.class)));
			}
		});
		return button;
	}

	// private Layout buildErrorLayout() {
	// VerticalLayout layout = new VerticalLayout();
	// layout.addComponent(new Label("No such user"));
	// layout.addComponent(generateBackButton());
	// return layout;
	// }

	// private Layout buildUserLayout() {
	// VerticalLayout layout = new VerticalLayout();
	// layout.setSizeFull();
	// layout.addComponent(new Label("Talking to"));
	// layout.addComponent(generateBackButton());
	// layout.addComponent(buildChatLayout());
	// return layout;
	// }

	// private Component buildChatLayout() {
	// VerticalLayout chatLayout = new VerticalLayout();
	// chatLayout.setSizeFull();
	// chatLayout.setMargin(false);
	// messageLayout = new VerticalLayout();
	// messageLayout.setWidth("100%");
	//
	// final TextField messageField = new TextField();
	// messageField.setWidth("100%");
	// final Button sendButton = new Button("Send");
	// sendButton.addClickListener(new ClickListener() {
	//
	// @Override
	// public void buttonClick(ClickEvent event) {
	// String message = messageField.getValue();
	// if (!message.isEmpty()) {
	// messageField.setValue("");
	// messageEvent.fire(new Message(userInfo.getUser(),
	// targetUser, message));
	// }
	// }
	// });
	// sendButton.setClickShortcut(KeyCode.ENTER);
	// Panel messagePanel = new Panel();
	// messagePanel.setHeight("400px");
	// messagePanel.setWidth("100%");
	// chatLayout.addComponent(messagePanel);
	// HorizontalLayout entryLayout = new HorizontalLayout(sendButton,
	// messageField);
	// entryLayout.setWidth("100%");
	// entryLayout.setExpandRatio(messageField, 1);
	// entryLayout.setSpacing(true);
	// chatLayout.addComponent(entryLayout);
	// return chatLayout;
	// }

	// private Layout buildUserSelectionLayout() {
	// VerticalLayout layout = new VerticalLayout();
	// layout.addComponent(new Label("Select user to talk to:"));
	// for (User user : userDAO.getUsers()) {
	// if (user.equals(userInfo.getUser())) {
	// continue;
	// }
	// layout.addComponent(generateUserSelectionButton(user));
	// }
	// if (accessControl.isUserInRole("admin")) {
	// layout.addComponent(new Label("Admin:"));
	// Button createUserButton = new Button("Create user");
	// createUserButton.addClickListener(new ClickListener() {
	//
	// @Override
	// public void buttonClick(ClickEvent event) {
	// navigationEvent.fire(new NavigationEvent("create-user"));
	// }
	// });
	// layout.addComponent(createUserButton);
	// }
	// return layout;
	// }

	// private Button generateUserSelectionButton(final User user) {
	// Button button = new Button(user.getName());
	// button.addClickListener(new ClickListener() {
	//
	// @Override
	// public void buttonClick(ClickEvent event) {
	// navigationEvent.fire(new NavigationEvent(Conventions
	// .deriveMappingForView(ChatView.class)
	// + "/"
	// + user.getUsername()));
	// }
	// });
	// return button;
	// }

}