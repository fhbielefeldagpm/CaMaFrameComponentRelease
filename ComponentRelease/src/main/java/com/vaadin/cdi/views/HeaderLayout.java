package com.vaadin.cdi.views;

import java.io.File;

import javax.inject.Inject;

import com.vaadin.cdi.UIScoped;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@ViewScoped
public class HeaderLayout extends HorizontalLayout {

	@Inject
	private javax.enterprise.event.Event<NavigationEvent> navigationEvent;
	
	public HeaderLayout(CaseWorkerInfo caseWorkerInfo) {
		Label loggedInUser = new Label("Logged in as: " + caseWorkerInfo.getUser().getLastname() + ", "
				+ caseWorkerInfo.getUser().getFirstname());
		Button logoutBtn = new Button("Logout");
		logoutBtn.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				navigationEvent.fire(new NavigationEvent("login"));
			}
		});
		
		VerticalLayout userInfo = new VerticalLayout();
		userInfo.addComponents(loggedInUser, logoutBtn);
		this.addComponent(userInfo);
		
		// Find the application directory
		String basepath = VaadinService.getCurrent()
		                  .getBaseDirectory().getAbsolutePath();

		// Image as a file resource
		FileResource resource = new FileResource(new File(basepath +
		                        "/VAADIN/CamaFrameLogo_transparent_header.png"));

		// Show the image in the application
		Image image = new Image(null, resource);
		
		this.addComponent(image);
	}
	
}
