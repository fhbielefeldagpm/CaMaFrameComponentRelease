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
package cm.core.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import cm.core.CaseModel;
import cm.core.data.CaseFile;
import cm.core.data.CaseFileItem;
import cm.core.data.CaseFileItemAttachment;
import cm.core.data.CaseFileItemStates;
import cm.core.data.SimpleProperty;
import cm.core.services.CaseFileService;
import cm.core.services.ServiceMessage;
import cm.core.services.commands.casefileitem.CaseFileItemTransitionCommand;
import cm.core.services.commands.casefileitem.CaseFileItemTransitionCommandFactory;
import cm.core.services.commands.casefileitem.CaseFileItemTransitionController;
import cm.core.states.CaseFileItemTransition;
import cm.core.utils.PersistenceSettings;

@Stateless
public class CaseFileServiceImpl implements CaseFileService {

	@PersistenceContext(unitName = PersistenceSettings.persistenceContextUnitName)
	EntityManager em;

	private CaseFileServiceImpl() {

	}

	@Override
	public CaseFile getCaseFile(CaseModel cm) {
		TypedQuery<CaseFile> query = em.createQuery("SELECT cf FROM CaseFile cf WHERE cf.caseRef.id = :caseId",
				CaseFile.class);
		long caseId = cm.getId();
		query.setParameter("caseId", caseId);
		CaseFile caseFile = query.getSingleResult();
		return caseFile;
	}

	@Override
	public CaseFileItem getCaseFileItem(CaseModel cm, String itemName) {
		CaseFile cf = getCaseFile(cm);
		CaseFileItem item = cf.getCaseFileItemById(itemName);
		return item;
	}

	@Override
	public CaseFileItem getCaseFileItem(long persistenceId) {
		TypedQuery<CaseFileItem> query = em
				.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :persistenceId", CaseFileItem.class);
		query.setParameter("persistenceId", persistenceId);
		List<CaseFileItem> items = query.getResultList();
		CaseFileItem fetched = null;
		if (items.size() > 0) {
			fetched = items.get(0);
		}
		return fetched;
	}

	@Override
	public List<CaseFileItem> getAllCaseFileItems(CaseModel cm) {
		CaseFile cf = getCaseFile(cm);
		List<CaseFileItem> items = cf.getCaseFileItems();
		return items;
	}

	// has already been created with init
	// @Override
	// public ServiceMessage createCaseFileItem(CaseFileItem cfi) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public ServiceMessage updateCaseFileItem(CaseFileItem cfi) {
		TypedQuery<CaseFileItem> query = em.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :id",
				CaseFileItem.class);
		query.setParameter("id", cfi.getId());
		CaseFileItem found = null;
		List<CaseFileItem> items = query.getResultList();
		if (items.size() > 0) {
			found = items.get(0);
			em.merge(cfi);
			if(found.getState().equals(CaseFileItemStates.INITIAL.toString())) {
				found.getContextState().create();
			}					
			transitionCaseFileItem(cfi, CaseFileItemTransition.update);
		}
		return null;
	}

	@Override
	public ServiceMessage replaceCaseFileItem(CaseFileItem cfi) {
		TypedQuery<CaseFileItem> query = em.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :id",
				CaseFileItem.class);
		query.setParameter("id", cfi.getId());
		List<CaseFileItem> items = query.getResultList();
		if (items.size() > 0) {
			CaseFileItem found = items.get(0);
			em.merge(cfi);
			transitionCaseFileItem(cfi, CaseFileItemTransition.replace);
		}
		return null;
	}

	@Override
	public ServiceMessage deleteCaseFileItem(CaseFileItem cfi) {
		TypedQuery<CaseFileItem> query = em.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :id",
				CaseFileItem.class);
		query.setParameter("id", cfi.getId());
		List<CaseFileItem> items = query.getResultList();
		if (items.size() > 0) {
			CaseFileItem found = items.get(0);
			transitionCaseFileItem(found, CaseFileItemTransition.delete);
			em.remove(found);
		}
		return null;
	}

	@Override
	public ServiceMessage transitionCaseFileItem(CaseFileItem cfi, CaseFileItemTransition transition) {
		// CaseFileItem item = em.find(CaseFileItem.class, cfi.getId());
		TypedQuery<CaseFileItem> query = em.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :id",
				CaseFileItem.class);
		query.setParameter("id", cfi.getId());
		List<CaseFileItem> items = query.getResultList();
		if (items.size() > 0) {
			CaseFileItem item = items.get(0);
			CaseFileItemTransitionController cfitc = new CaseFileItemTransitionController();
			CaseFileItemTransitionCommand command = CaseFileItemTransitionCommandFactory.getCommand(transition, item);
			cfitc.saveCommand(command);
			cfitc.executeCommand();
			em.merge(item);
		}
		return null;
	}

	@Override
	public SimpleProperty getPropertyById(long id) {
		TypedQuery<SimpleProperty> query = em.createQuery("SELECT p FROM SimpleProperty p WHERE p.id = :id",
				SimpleProperty.class);
		query.setParameter("id", id);
		List<SimpleProperty> properties = query.getResultList();
		if (properties.size() > 0) {
			return properties.get(0);
		}
		return null;
	}

	@Override
	public ServiceMessage addProperty(CaseFileItem cfi, SimpleProperty property) {
		TypedQuery<CaseFileItem> query = em.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :id",
				CaseFileItem.class);
		query.setParameter("id", cfi.getId());
		List<CaseFileItem> items = query.getResultList();
		if (items.size() > 0) {
			CaseFileItem item = items.get(0);
			List<SimpleProperty> itemProperties = item.getProperties();
			Set<String> existingNames = new HashSet<String>();
			for (SimpleProperty p : itemProperties) {
				existingNames.add(p.getName());
			}
			if (existingNames.add(property.getName())) {
				item.addProperty(property);
			}
			em.merge(item);
		}
		return null;
	}

	@Override
	public ServiceMessage updatePropertyByName(CaseFileItem cfi, SimpleProperty property) {
		TypedQuery<CaseFileItem> query = em.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :id",
				CaseFileItem.class);
		query.setParameter("id", cfi.getId());
		List<CaseFileItem> items = query.getResultList();
		if (items.size() > 0) {
			CaseFileItem item = items.get(0);
			List<SimpleProperty> itemProperties = item.getProperties();
			for (SimpleProperty p : itemProperties) {
				if (p.getName().equals(property.getName())) {
					p.setValue(property.getValue());
				}
			}
			em.merge(item);
		}
		return null;
	}

	@Override
	public ServiceMessage updateProperty(SimpleProperty property) {
		TypedQuery<SimpleProperty> query = em.createQuery("SELECT p FROM SimpleProperty p WHERE p.id = :id",
				SimpleProperty.class);
		query.setParameter("id", property.getId());
		SimpleProperty prop = query.getSingleResult();
		if (prop != null) {
			prop.setValue(property.getValue());
			em.merge(prop);
		}
		return null;
	}

	@Override
	public ServiceMessage deleteProperty(SimpleProperty property) {
		TypedQuery<SimpleProperty> query = em.createQuery("SELECT p FROM SimpleProperty p WHERE p.id = :id",
				SimpleProperty.class);
		query.setParameter("id", property.getId());
		SimpleProperty prop = query.getSingleResult();
		em.remove(prop);
		return null;
	}

	@Override
	public ServiceMessage deleteProperty(CaseFileItem cfi, SimpleProperty property) {
		TypedQuery<CaseFileItem> query = em.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :id",
				CaseFileItem.class);
		query.setParameter("id", cfi.getId());
		CaseFileItem item = query.getSingleResult();
		List<SimpleProperty> itemProperties = item.getProperties();

		int indexOfProperty = -1;
		for (int i = 0; i < itemProperties.size() + 1; i++) {
			if (itemProperties.get(i).getName().equals(property.getName())) {
				indexOfProperty = i;
				break;
			}
		}
		if (indexOfProperty > -1) {
			itemProperties.remove(indexOfProperty);
		}
		em.merge(item);
		return null;
	}

	@Override
	public CaseFileItemAttachment getAttachmentById(long id) {
		TypedQuery<CaseFileItemAttachment> query = em
				.createQuery("SELECT a FROM CaseFileItemAttachment a WHERE a.id = :id", CaseFileItemAttachment.class);
		query.setParameter("id", id);
		List<CaseFileItemAttachment> attachments = query.getResultList();
		if (attachments.size() > 0) {
			CaseFileItemAttachment attachment = attachments.get(0);
			return attachment;
		}
		return null;
	}

	@Override
	public ServiceMessage saveAttachment(CaseFileItem cfi, CaseFileItemAttachment attachment) {
		CaseFileItem item = this.getCaseFileItem(cfi.getId());
		item.addAttachment(attachment);
		em.merge(item);
		return null;
	}

	@Override
	public ServiceMessage deleteAttachment(CaseFileItem cfi, CaseFileItemAttachment attachment) {
		TypedQuery<CaseFileItem> query = em.createQuery("SELECT cfi FROM CaseFileItem cfi WHERE cfi.id = :cfiId", CaseFileItem.class);
		query.setParameter("cfiId", cfi.getId());
		CaseFileItem fetchedItem = query.getSingleResult();
		fetchedItem.removeAttachment(attachment);
		em.merge(fetchedItem);
		TypedQuery<CaseFileItemAttachment> queryAtt = em.createQuery(
				"SELECT cfiA FROM CaseFileItemAttachment cfiA WHERE cfiA.id = :attId", CaseFileItemAttachment.class);
		queryAtt.setParameter("attId", attachment.getId());
		CaseFileItemAttachment fetchedAttachment = queryAtt.getSingleResult();
		if (fetchedAttachment != null) {
			em.remove(fetchedAttachment);
		}
		return null;
	}

}
