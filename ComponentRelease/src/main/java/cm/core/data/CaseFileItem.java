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
package cm.core.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.CaseElement;
import cm.core.interfaces.ICaseFileItemObservable;
import cm.core.interfaces.ICaseFileItemObserver;
import cm.core.sentries.CaseFileItemOnPart;
import cm.core.states.CaseFileItemAvailable;
import cm.core.states.CaseFileItemDiscarded;
import cm.core.states.CaseFileItemInitial;
import cm.core.states.CaseFileItemTransition;
import cm.core.states.ICaseFileItemState;

/**
 * <p>
 * Class representing a CaseFileItem in CMMN. Container element for
 * {@link SimpleProperty}s and {@link CaseFileItemAttachment}s as well as child
 * CaseFileItems.
 * </p>
 * <p>
 * See CMMN 1.1 specification sections 5.3.2, 8.3 and 8.5 for more information.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CaseFileItem implements ICaseFileItemObservable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String cmId;
	private String multiplicity;
	private String name;
	private String state;
	@OneToMany(cascade = CascadeType.ALL)
	private List<CaseFileItem> children;
	@OneToMany(cascade = CascadeType.ALL)
	private List<CaseFileItemAttachment> attachments;
	@OneToMany(cascade = CascadeType.ALL)
	private List<SimpleProperty> properties;
	// private CaseFileItem parent;
	// private CaseFileItem sourceRef;
	// private List<CaseFileItem> targetRefs;
	// TODO unnecessary?!
	@JohnzonIgnore
	@OneToMany
	private List<CaseFileItemOnPart> observers;

	@JohnzonIgnore
	@Transient
	private ICaseFileItemState contextState;

	public CaseFileItem() {

	}

	/**
	 * <p>
	 * Constructs a new CaseFileItem with a cmId, its multiplicity and
	 * human-readable name.
	 * <p>
	 * 
	 * @param cmId         a cmId which can be based on a .CMMN file markup
	 * @param multiplicity a multiplicity based on {@link MultiplicityEnum} as a
	 *                     String value
	 * @param name         a human-readable name
	 */
	public CaseFileItem(String cmId, String multiplicity, String name) {
		this.cmId = cmId;
		this.multiplicity = multiplicity;
		this.name = name;
		this.state = CaseFileItemStates.INITIAL.toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCmId() {
		return cmId;
	}

	public void setCmId(String cmId) {
		this.cmId = cmId;
	}

	// specifies the number of potential instances of this CaseFileItem
	// in the context of a particular Case instance, e.g. 4 photographs
	public String getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CaseFileItem> getChildren() {
		return children;
	}

	public void setChildren(List<CaseFileItem> children) {
		this.children = children;
	}

	public void addChild(CaseFileItem child) {
		if (this.children == null) {
			this.children = new ArrayList<CaseFileItem>();
			this.children.add(child);
		} else if (this.children != null) {
			if (!this.children.contains(child)) {
				this.children.add(child);
			}
		}

	}

	public void removeChild(CaseFileItem child) {
		int idx = this.children.indexOf(child);
		if (idx > -1) {
			this.children.remove(idx);
		}
	}

	public List<CaseFileItemOnPart> getObservers() {
		return observers;
	}

	public void setObservers(List<CaseFileItemOnPart> observers) {
		this.observers = observers;
	}

	/**
	 * <p>
	 * Sets the state of a CaseFileItem using a transition. Notifies any
	 * {@link CaseFileItemOnPart}s observing the CaseFileItem of the specified
	 * transition.
	 * </p>
	 * <p>
	 * See CMMN 1.1 specification section 8.3 for more information.
	 * </p>
	 * 
	 * @param newState   a specified state, i.e. from enum
	 *                   {@link CaseFileItemStates}
	 * @param transition a specified transition, i.e. from enum
	 *                   {@link CaseFileItemTransition}
	 */
	public void setState(String state, String transition) {
		this.state = state;
		if (this.observers != null) {
			for (ICaseFileItemObserver o : this.observers) {
				o.updateCaseFileItemObserver(transition);
			}
		}
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<CaseFileItemAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<CaseFileItemAttachment> attachments) {
		this.attachments = attachments;
	}

	public void addAttachment(CaseFileItemAttachment attachment) {
		if (this.attachments == null) {
			this.attachments = new ArrayList<CaseFileItemAttachment>();
			this.attachments.add(attachment);
		} else if (this.attachments != null) {
			if (!this.attachments.contains(attachment)) {
				this.attachments.add(attachment);
			}
		}

	}

	public void removeAttachment(CaseFileItemAttachment attachment) {
		for (int i = 0; i < this.attachments.size() + 1; i++) {
			if (this.attachments.get(i).getId() == attachment.getId()) {
				this.attachments.remove(i);
				break;
			}
		}
	}

	public List<SimpleProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<SimpleProperty> properties) {
		this.properties = properties;
	}

	/**
	 * <p>
	 * Returns a property based on its name. A {@link SimpleProperty} needs to be
	 * cast from String to its original type.
	 * </p>
	 * 
	 * @param name the name of the property to look for
	 * @return the SimpleProperty
	 */
	public SimpleProperty getProperty(String name) {
		SimpleProperty property = null;
		if (this.properties != null) {
			for (SimpleProperty prop : this.properties) {
				if (prop.getName().equals(name)) {
					property = prop;
				}
			}
		}
		return property;
	}

	/**
	 * 
	 * 
	 */

	/**
	 * <p>
	 * Adds one or more {@link SimpleProperty}s. Checks for duplicate
	 * {@link SimpleProperty#getName()}s. Does not add the child if its name is a
	 * duplicate.
	 * </p>
	 * 
	 * @param properties one ore more SimplePropertys
	 */
	public void addProperty(SimpleProperty... properties) {
		Set<String> propIds = new HashSet<String>();
		if (this.properties == null) {
			this.properties = new ArrayList<SimpleProperty>();
		} else if (this.properties.size() > 0) {
			for (SimpleProperty prop : this.properties) {
				propIds.add(prop.getName());
			}
		}
		for (SimpleProperty property : properties) {
			if (propIds.add(property.getName())) {
				this.properties.add(property);
			} else {
				// TODO throw duplicate error
			}
		}

	}

	public void removeProperty(SimpleProperty property) {
		if (this.properties != null) {
			this.removeProperty(property);
//			this.properties.remove(property.getName());
		}
	}

	public void removeChildElement(CaseElement child) {
		if (this.properties != null) {
			for (int i = 0; i < this.properties.size(); i++) {
				if (this.properties.get(i).getName().equals(child.getName()))
					this.properties.remove(i);
			}
		}
	}

	/**
	 * <p>
	 * Returns an implementation of {@link ICaseFileItemState} based on the current
	 * state. The implementation offers all transitions, but only implements those
	 * permissible.
	 * </p>
	 * <p>
	 * Used as a state pattern adaptation. Each implementation captures permissible
	 * transitions from a state. See CMMN 1.1 specification section 8.3 for more
	 * information.
	 * </p>
	 * 
	 * @return an implementation of {@link ICaseFileItemState} based on the current
	 *         state, e.g. AVAILABLE
	 * @see {@link CaseFileItem#loadContextState()}
	 */
	public ICaseFileItemState getContextState() {
		loadContextState();
		return contextState;
	}

	public void setContextState(ICaseFileItemState contextState) {
		this.contextState = contextState;
	}

	private void loadContextState() {
		if (state.equals(CaseFileItemStates.INITIAL.toString())) {
			setContextState(new CaseFileItemInitial(this));
		}
		if (state.equals(CaseFileItemStates.AVAILABLE.toString())) {
			setContextState(new CaseFileItemAvailable(this));
		}
		if (state.equals(CaseFileItemStates.DISCARDED.toString())) {
			setContextState(new CaseFileItemDiscarded(this));
		}
	}

	/**
	 * 
	 * @param o
	 */
	public void registerCaseFileItemObserver(CaseFileItemOnPart o) {
		if (this.observers == null) {
			this.observers = new ArrayList<CaseFileItemOnPart>();
		}
		this.observers.add(o);
	}

	/**
	 * 
	 * @param o
	 */
	public void unregisterCaseFileItemObserver(CaseFileItemOnPart o) {
		int idx = this.observers.indexOf(o);
		if (idx > -1) {
			this.observers.remove(idx);
		}
	}
}