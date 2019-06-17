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
package cm.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import cm.core.tasks.HumanTask;
import cm.core.tasks.Task;
/**
 * <p>Class representing case workers in CMMN.</p>
 * <p>See CMMN 1.1 specification section 4.1 for more information.</p>
 * 
 * @author André Zensen
 *
 */
@Entity
public class CaseWorker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String user;
	private String pass;
	private String firstname;
	private String lastname;
	private boolean isAdmin;

	@OneToMany
	private Set<CaseRole> caseRole;

	public Set<CaseRole> getCaseRole() {
		return caseRole;
	}

	public void setCaseRole(Set<CaseRole> caseRole) {
		this.caseRole = caseRole;
	}

	public void addCaseRole(CaseRole caseRole) {
		if (this.caseRole == null) {
			this.caseRole = new HashSet<>();
		}
		this.caseRole.add(caseRole);
	}

	public void removeCaseRole(CaseRole caseRole) {
		if (this.caseRole != null) {
			this.caseRole.remove(caseRole);
		}
	}

	public List<String> getCaseRolesAsString() {
		List<String> roleNames = new ArrayList<String>();
		for (CaseRole role : this.caseRole) {
			roleNames.add(role.getRoleName());
		}
		return roleNames;
	}

	@OneToMany // (mappedBy="claimant")
	private List<HumanTask> taskList;

	public CaseWorker(String user, String pass, String firstname, String lastname, boolean isAdmin) {
		this.user = user;
		this.pass = pass;
		this.firstname = firstname;
		this.lastname = lastname;
		this.isAdmin = isAdmin;
	}

	public CaseWorker() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public List<HumanTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<HumanTask> taskList) {
		this.taskList = taskList;
	}
}
