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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.johnzon.mapper.JohnzonIgnore;

import cm.core.CaseModel;
import cm.core.CaseElement;
/**
 * <p>Class representing a CaseFile in CMMN. Container element for all {@link CaseFileItem}s.</p>
 * <p>See CMMN 1.1 specification sections 5.3 and 8.3 for more information.</p>
 * 
 * @author André Zensen
 *
 */
@Entity
public class CaseFile {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String cmId;
	@OneToMany(cascade=CascadeType.ALL)
	private List<CaseFileItem> caseFileItems;
	@ManyToOne
	protected CaseModel caseRef;

	public CaseFile() {

	}
	/**
	 * Constructs a new CaseFile with a reference to the given {@link CaseModel}. Any existing CaseFile of the given CaseModel is replaced.
	 * @param cm	the CaseModel this CaseFile is to be attached to
	 */
	public CaseFile(CaseModel cm) {
		this.caseRef = cm;
		this.caseRef.setCaseFile(this);
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

	public List<CaseFileItem> getCaseFileItems() {
		return caseFileItems;
	}

	public void setCaseFileItems(List<CaseFileItem> caseFileItems) {
		this.caseFileItems = caseFileItems;
	}
	/**
	 * <p>
	 * Adds one or more {@link CaseFileItem}s. Checks for duplicate {@link CaseElement#cmId}s. Does not add the
	 * child if its cmId is a duplicate.
	 * </p>
	 * 
	 * @param caseFileItems	one or more CaseFileItems to be added to the CaseFile
	 */
	public void addCaseFileItem(CaseFileItem... caseFileItems) {
		Set<String> caseFileItemIds = new HashSet<String>();
		if(this.caseFileItems == null) {
			this.caseFileItems = new ArrayList<CaseFileItem>();
		} else if (this.caseFileItems.size() > 0) {
			for(CaseFileItem currentcaseFileItem : this.caseFileItems) {
				caseFileItemIds.add(currentcaseFileItem.getCmId());
			}
		}
		for(CaseFileItem currentCaseFileItem : caseFileItems) {
			if(caseFileItemIds.add(currentCaseFileItem.getCmId())) {
				this.caseFileItems.add(currentCaseFileItem);

			} else {
				// TODO throw duplicate error
			}
		}
		
	}
	/**
	 * <p>Returns a CaseFileItem contained in the CaseFile by its cmId.</p>
	 * @param cmId	the cmId of the CaseFileItem to be returned
	 * @return		the CaseFileItem specified by its cmId, or null if none could be found by that cmId
	 */
	public CaseFileItem getCaseFileItemById(String cmId) {
		for(CaseFileItem caseFileItem : this.caseFileItems) {
			if(caseFileItem.getCmId().equals(cmId)) {
				return caseFileItem;
			}
		}
		return null;
	}

	public void deleteCaseFileItemById(String id) {
		for(int i = 0; i < this.caseFileItems.size(); i++) {
			if(this.caseFileItems.get(i).getCmId().equals(id)) {
				this.caseFileItems.remove(i);
				break;
			}
		}
	}

	public CaseModel getCaseRef() {
		return caseRef;
	}

	public void setCaseRef(CaseModel caseRef) {
		this.caseRef = caseRef;
	}
}// end CaseFile