package com.vaadin.cdi.views;

import java.io.Serializable;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.vaadin.cdi.access.AccessControl;

import cm.core.CaseRole;

@Alternative
public class CustomAccessControl extends AccessControl implements Serializable {

	@Inject
	private CaseWorkerInfo userInfo;

	@Override
	public boolean isUserSignedIn() {
		return userInfo.getUser() != null;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (isUserSignedIn()) {
			for (CaseRole roleOfWorker : userInfo.getUser().getCaseRole()) {
				if (roleOfWorker.toString().equals(role)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getPrincipalName() {
		if (isUserSignedIn()) {
			return userInfo.getUser().getFirstname();
		}
		return null;
	}

}
