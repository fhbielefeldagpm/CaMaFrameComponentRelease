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
package com.vaadin.cdi.views;

import java.io.File;

import javax.inject.Inject;

import org.apache.cxf.transport.servlet.ServletDestinationFactory;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import cm.core.CaseWorker;
import cm.core.services.CaseWorkerService;

@CDIView("login")
public class LoginView extends CustomComponent implements View, ClickListener {

	@Inject
	private CaseWorkerInfo caseWorkerInfo;

	@Inject
	CaseWorkerService cwService;

//	@Inject
//	private ServiceFacade services;

	private TextField usernameField;
	private PasswordField passwordField;
	private Button loginButton;

	@Inject
	private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

	@Override
	public void enter(ViewChangeEvent event) {

		// Find the application directory
		String basepath = VaadinService.getCurrent()
		                  .getBaseDirectory().getAbsolutePath();

		// Image as a file resource
		FileResource resource = new FileResource(new File(basepath +
		                        "/VAADIN/CamaFrameLogo_transparent.png"));

		// Show the image in the application
		Image image = new Image(null, resource);
		
		usernameField = new TextField("Username");
		passwordField = new PasswordField("Password");
		loginButton = new Button("Login");
		loginButton.addClickListener(this);
		loginButton.setClickShortcut(KeyCode.ENTER);

		VerticalLayout layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		setCompositionRoot(layout);
		layout.setSizeFull();

		layout.addComponent(image);
		layout.addComponent(usernameField);
		layout.addComponent(passwordField);
		layout.addComponent(loginButton);

	}

	@Override
	public void buttonClick(ClickEvent event) {
		String username = usernameField.getValue();
		String password = passwordField.getValue();

		CaseWorker worker = cwService.getCaseWorkerByLogin(username, password);
//        Notification.show("CaseWorker:", worker.getFirstname() + " " + worker.getLastname(), Notification.Type.HUMANIZED_MESSAGE);
		if (worker != null) {
			caseWorkerInfo.setUser(worker);
			navigationEvent.fire(new NavigationEvent("case-list"));
		} else {
			new Notification("Wrong password", Notification.Type.ERROR_MESSAGE).show(getUI().getPage());
			return;
		}

	}
}