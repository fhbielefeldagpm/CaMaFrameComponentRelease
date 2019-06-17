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
package cm.core.services;

import java.util.List;

import cm.core.CaseModel;
import cm.core.data.CaseFile;
import cm.core.data.CaseFileItem;
import cm.core.data.CaseFileItemAttachment;
import cm.core.data.SimpleProperty;
import cm.core.states.CaseFileItemTransition;

/**
 * <p>
 * Provides interfaces to access data of {@link CaseModel}s to manipulate and
 * transition {@link CaseFileItem}s.
 * </p>
 * <p>
 * Implementations use an EntityManager and PersistenceContext.
 * </p>
 * 
 * @author André Zensen
 *
 */
public interface CaseFileService {
	/**
	 * Get a CaseFile as a central entry point to the data of a CaseModel.
	 * 
	 * @param cm a CaseModel object as a reference (its persistence id is enough)
	 * @return the CaseFile of the CaseModel
	 */
	public CaseFile getCaseFile(CaseModel cm);

	/**
	 * Get a CaseFileItem as an entry point to the properties of it.
	 * 
	 * @param cm       a CaseModel object as a reference (its persistence id is
	 *                 enough)
	 * @param itemName the name of the CaseFileItem
	 * @return the CaseFileItem in the CaseModel
	 */
	public CaseFileItem getCaseFileItem(CaseModel cm, String itemName);
	
	public CaseFileItem getCaseFileItem(long persistenceId);

	public List<CaseFileItem> getAllCaseFileItems(CaseModel cm);

	public ServiceMessage updateCaseFileItem(CaseFileItem cfi);

	public ServiceMessage replaceCaseFileItem(CaseFileItem cfi);

	public ServiceMessage deleteCaseFileItem(CaseFileItem cfi);
	
	public SimpleProperty getPropertyById(long id);

	public ServiceMessage addProperty(CaseFileItem cfi, SimpleProperty property);
	
	public ServiceMessage updatePropertyByName(CaseFileItem cfi, SimpleProperty property);
	
	public ServiceMessage updateProperty(SimpleProperty property);
	
	public ServiceMessage deleteProperty(CaseFileItem cfi, SimpleProperty property);
	
	public ServiceMessage deleteProperty(SimpleProperty property);
	
	public CaseFileItemAttachment getAttachmentById(long id);
	
	public ServiceMessage saveAttachment(CaseFileItem cfi, CaseFileItemAttachment attachment);
	
	public ServiceMessage deleteAttachment(CaseFileItem cfi, CaseFileItemAttachment attachment);

	public ServiceMessage transitionCaseFileItem(CaseFileItem cfi, CaseFileItemTransition transition);
}
