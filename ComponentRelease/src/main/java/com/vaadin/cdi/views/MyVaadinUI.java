package com.vaadin.cdi.views;

import javax.inject.Inject;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.URLMapping;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

@Theme("valo")
@CDIUI("")
//@URLMapping("/welcome/") defined in web.xml
@Push(PushMode.AUTOMATIC)
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {

    @Inject
    private javax.enterprise.event.Event<NavigationEvent> navigationEvent;

    @Override
    protected void init(VaadinRequest request) {

        navigationEvent.fire(new NavigationEvent("login"));
    }

}
