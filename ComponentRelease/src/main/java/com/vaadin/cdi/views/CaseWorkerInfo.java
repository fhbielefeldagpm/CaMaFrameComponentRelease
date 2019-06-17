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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.security.RolesAllowed;

import com.vaadin.cdi.UIScoped;

import cm.core.CaseWorker;

@UIScoped
public class CaseWorkerInfo implements Serializable {
	private CaseWorker user;

	private List<String> roles = new LinkedList<String>();

	public CaseWorkerInfo() {
		this.user = null;
	}

	public CaseWorker getUser() {
		return user;
	}

	public String getName() {
		if (user == null) {
			return "anonymous user";
		} else {
			return user.getLastname();
		}
	}

	public void setUser(CaseWorker user) {
		// for servlet role restrictions aka @RolesAllowed({ "admin" }) annotation
		this.user = user;
		roles.clear();
		if (user != null) {
			roles.add("user");
			if (user.isAdmin()) {
				roles.add("admin");
			}
		}
	}

	public List<String> getRoles() {
		return roles;
	}
}