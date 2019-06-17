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
import java.util.Collection;

import javax.inject.Inject;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.views.CaseWorkerInfo;
import com.vaadin.cdi.views.NavigationEvent;
import com.vaadin.cdi.views.TaskInfo;
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

import cm.core.CaseWorker;
import cm.core.services.CaseWorkerService;
import cm.core.services.TaskService;
import cm.core.states.StageTaskTransitions;
import cm.core.tasks.CaseTask;
import cm.core.tasks.HumanTask;
import cm.core.tasks.Task;
import cm.core.tasks.TaskStates;

/**
 * <p>
 * Simple Vaadin-CDI-based implementation of a view to list, (un)claim and start
 * {@link Task}s.
 * </p>
 * 
 * @author André Zensen
 *
 */
@CDIView
public class TaskListView extends CustomComponent implements View {

	@Inject
	private CaseWorkerService cwService;
	@Inject
	private TaskService taskService;
	@Inject
	private CaseWorkerInfo caseWorkerInfo;
	@Inject
	private TaskInfo taskInfo;

	@Inject
	private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

	private Grid<HumanTask> gridMyTasks = new Grid<>();
	private Grid<HumanTask> gridClaimable = new Grid<>();

	@Override
	public void enter(ViewChangeEvent event) {
		String parameters = event.getParameters();
		taskInfo.setTask(null);
		Layout layout = buildTaskListLayout();
		setCompositionRoot(layout);
		updateBothGrids();
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
				caseWorkerInfo = null;
				navigationEvent.fire(new NavigationEvent("login"));
			}
		});
		
		layout.addComponent(header);
		
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

		setupMyTasks();
		setupClaimable();

		// Button btnRefreshMyTasks = new Button("Refresh");
		// btnRefreshMyTasks.addClickListener(e -> {
		// updateBothGrids();
		// });

		Button btnRefreshClaimable = new Button("Refresh");
		btnRefreshClaimable.addClickListener(e -> {
			updateBothGrids();
		});

		Label myTaskList = new Label("My Tasks");
		layout.addComponent(myTaskList);

		layout.addComponents(gridMyTasks); // btnRefreshMyTasks
		gridMyTasks.setSizeFull();
		gridMyTasks.setHeightByRows(6);

		Label claimableTaskList = new Label("Claimable Tasks");
		layout.addComponent(claimableTaskList);
		layout.addComponents(gridClaimable, btnRefreshClaimable);
		gridClaimable.setSizeFull();
		gridClaimable.setHeightByRows(4);

		HorizontalLayout navBtnLayout = new HorizontalLayout(generateBackButton(), generateCaseListButton());
		layout.addComponents(navBtnLayout);
		layout.setSizeFull();
		return layout;
	}

	private Button generateCaseListButton() {
		Button caseListButton = new Button("Case List");
		caseListButton.addClickListener(e -> {
			navigationEvent.fire(new NavigationEvent("case-list"));
		});
		return caseListButton;
	}

	private void setupClaimable() {
		gridClaimable.addColumn(Task::getName).setCaption("Name").setExpandRatio(1);
		gridClaimable.addColumn(Task::getState).setCaption("State").setExpandRatio(1);
		gridClaimable.addComponentColumn(this::buildClaimButton).setExpandRatio(1);

		gridClaimable.setSizeFull();
	}

	private void updateClaimable() {
		// Collection<HumanTask> items = this.taskService.getAllClaimableTasks();
		Collection<HumanTask> items = this.taskService.getAllClaimableTasksByRole(this.caseWorkerInfo.getUser());
		this.gridClaimable.setItems(items);

	}

	private void setupMyTasks() {
		gridMyTasks.addColumn(Task::getName).setCaption("Name").setExpandRatio(1);
		gridMyTasks.addColumn(Task::getState).setCaption("State").setExpandRatio(1);
		gridMyTasks.addComponentColumn(this::buildStartButton).setExpandRatio(1);
		gridMyTasks.addComponentColumn(this::buildUnclaimButton).setExpandRatio(1);

		gridMyTasks.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() == null) {
				taskInfo.setTask(null);
			} else {
				taskInfo.setTask(event.getValue());
			}
		});

		gridMyTasks.setSizeFull();
	}

	private void clearSelectedTask() {
		taskInfo.setTask(null);
	}

	private void updateMyTasks() {
		CaseWorker currentUser = caseWorkerInfo.getUser();
		Collection<HumanTask> items = this.taskService.getAllClaimedTasksByCaseWorker(currentUser);
		this.gridMyTasks.setItems(items);

	}

	private Button buildClaimButton(Task t) {
		String captionActive = "Claim";
		String captionEnable = "Activate";
		Button button = new Button();

		boolean isActive = t.getState().equals(TaskStates.ACTIVE.toString());
		boolean isEnable = t.getState().equals(TaskStates.ENABLED.toString());
		button.setEnabled(isActive || isEnable);

		if (isActive) {
			button.setCaption(captionActive);
			button.addClickListener(e -> {
				if (t instanceof HumanTask) {
					HumanTask ht = (HumanTask) t;
					this.taskService.claimTask(ht, caseWorkerInfo.getUser());
					updateBothGrids();
				} else if (t instanceof CaseTask) {
					CaseTask ct = (CaseTask) t;
					this.taskService.claimTask(ct, caseWorkerInfo.getUser());
					updateBothGrids();
				}
			});
		} else if (isEnable) {
			button.setCaption(captionEnable);
			button.addClickListener(e -> {
				if (t instanceof HumanTask) {
					HumanTask ht = (HumanTask) t;
					this.taskService.transitionTask(ht, null, StageTaskTransitions.manualStart);
					updateBothGrids();
				} else if (t instanceof CaseTask) {
					CaseTask ct = (CaseTask) t;
					this.taskService.transitionTask(ct, null, StageTaskTransitions.enable);
					updateBothGrids();
				}
			});
		}
		return button;
	}

	private Button buildUnclaimButton(Task t) {
		String caption = "Unclaim";
		Button button = new Button(caption);

		boolean isEnabled = t.getState().equals(TaskStates.ACTIVE.toString());
		button.setEnabled(isEnabled);

		if (isEnabled) {
			button.addClickListener(e -> {
				if (t instanceof HumanTask) {
					HumanTask ht = (HumanTask) t;
					this.taskService.unclaimTask(ht, caseWorkerInfo.getUser());
					updateBothGrids();
				}
			});
		}
		return button;
	}

	private Button buildStartButton(Task t) {
		String caption = "Start";
		Button button = new Button(caption);
		boolean isEnabled = t.getState().equals(TaskStates.ACTIVE.toString());
		button.setEnabled(isEnabled);
		if (isEnabled) {
			button.addClickListener(e -> {
				Task task = this.taskInfo.getTask();
				if (task != null) {
					// navigationEvent.fire(new NavigationEvent("urlaubsantrag-stellen"));
					navigationEvent.fire(new NavigationEvent(task.getCmId()));
				}
			});
		}

		return button;
	}

	// private Link buildStartLink(Task t) {
	// String antragStellenViewName = "urlaubsantrag-stellen";
	// Link startLink = new Link("Start", new ExternalResource("#!" +
	// antragStellenViewName + "/" + taskInfo.getTask().getId()));
	//
	// return startLink;
	// }

	private void updateBothGrids() {
		clearSelectedTask();
		this.updateMyTasks();
		this.updateClaimable();
	}

	private Button generateBackButton() {
		Button button = new Button("Back");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				navigationEvent.fire(new NavigationEvent(Conventions.deriveMappingForView(TaskListView.class)));
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