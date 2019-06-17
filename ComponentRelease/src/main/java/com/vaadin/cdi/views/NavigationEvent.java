package com.vaadin.cdi.views;

public class NavigationEvent {
	private final String navigateTo;

	public NavigationEvent(String navigateTo) {
		this.navigateTo = navigateTo;
	}

	public String getNavigateTo() {
		return navigateTo;
	}

}