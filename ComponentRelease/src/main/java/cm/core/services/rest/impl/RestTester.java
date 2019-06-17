package cm.core.services.rest.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cm.core.CaseModel;
import cm.core.data.CaseFileItem;
import cm.core.services.CaseFileService;
import cm.core.services.CaseService;
import cm.core.utils.CaseFactory;
import cm.core.utils.PersistenceSettings;
import cm.core.utils.CaseFactory.CaseModelNames;

@Path("/cases/{id}")
public class RestTester {

	@PersistenceContext(unitName = PersistenceSettings.persistenceContextUnitName)
	private EntityManager em;
	
	@Inject
	CaseService cService;
	@Inject
	CaseFileService cfService;

	public RestTester() {

	}
	
	@Path("/d")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseFileItem> getCaseFileItems(@PathParam("id") long id) {
		CaseModel shallowCase = new CaseModel();
		shallowCase.setId(id);
		List<CaseFileItem> cfi = cfService.getAllCaseFileItems(shallowCase);
		return cfi;
	}
	
	@Path("/cService/caseNames")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCaseNames() {
		ArrayList<String> caseNames = new ArrayList<>();
		for(CaseModelNames modelName :CaseFactory.CaseModelNames.values()) {
			caseNames.add(modelName.toString());
		}
		return caseNames;
	}
	
	@Path("/cService")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseModel> getModelsViaInject() {
		List<CaseModel> caseList = cService.getAllCases();
		return caseList;
	}
	
	@Path("/hello")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String message() {
		return "Hello REST-World!";
	}
	
	public CaseModel getCaseById(CaseModel cm) {
		TypedQuery<CaseModel> query = em.createQuery("SELECT c FROM CaseModel c WHERE c.id = :caseId", CaseModel.class);
		query.setParameter("caseId", cm.getId());
		CaseModel fetchedCase = query.getSingleResult();
		return fetchedCase;
	}
//	@GET
//	public List<CaseModel> getAllCases(@QueryParam("first") @DefaultValue("0") int first,
//            @QueryParam("max") @DefaultValue("20") int max) {
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseModel> getAllCases() {
		
		List<CaseModel> cases = new ArrayList<>();
//		TypedQuery<CaseModel> query = em.createQuery("SELECT c FROM CaseModel c", CaseModel.class);
//		List<CaseModel> found = query.getResultList();
//		List<CaseModel> found = em.createNamedQuery("CaseModel.findAll", CaseModel.class).setFirstResult(first).setMaxResults(max).getResultList();
		List<CaseModel> found = em.createNamedQuery("CaseModel.findAll", CaseModel.class).getResultList();
		for(CaseModel model : found) {
			CaseModel modelToAdd = new CaseModel();
			modelToAdd.setId(model.getId());
			modelToAdd.setCmId(model.getCmId());
			modelToAdd.setName(model.getName());
			modelToAdd.setStateWithoutTransition(model.getState());
			
			cases.add(modelToAdd);
		}
		return cases;
//		return found;
	}
	
//	public void persistCase(CaseModel cm) {
//		em.persist(cm);
//	}
//
//	public void deleteCase(CaseModel cm) {
//		TypedQuery<CaseModel> query = em.createQuery("SELECT c FROM CaseModel c WHERE c.id = :caseId", CaseModel.class);
//		query.setParameter("caseId", cm.getId());
//		CaseModel fetchedCase = query.getSingleResult();
//		if(fetchedCase != null) {
//			em.remove(fetchedCase);
//		}
//	}
//
//	public void transitionCase(CaseModel cm, CaseWorker cw, CaseInstanceTransition transition) {
//		if(cw.isAdmin()) {
//			CaseModel cmInEm = em.find(CaseModel.class, cm.getId());
//			CaseModelTransitionController cmttctrl = new CaseModelTransitionController();
//			CaseModelTransitionCommand command = CaseModelTransitionCommandFactory.getCommand(transition, cmInEm);
//			cmttctrl.saveCommand(command);
//			cmttctrl.executeCommand();
//			em.merge(cmInEm);
//		} else {
//			// TODO notify via message that user is not admin
//		}
//	}
	
}
