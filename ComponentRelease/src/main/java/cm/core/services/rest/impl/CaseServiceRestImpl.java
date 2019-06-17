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
package cm.core.services.rest.impl;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import cm.core.CaseModel;
import cm.core.CaseElement;
import cm.core.data.CaseFileItem;
import cm.core.services.CaseFileService;
import cm.core.services.CaseService;
import cm.core.services.impl.CaseServiceImpl;
import cm.core.services.rest.CaseServiceRest;
import cm.core.tasks.Task;
import cm.core.utils.CaseFactory;

/**
 * <p>
 * Provides REST interfaces to list, start and delete
 * {@link CaseModel}s.
 * </p>
 * <p>
 * Implementations use the {@link CaseService} interface and
 * {@link CaseServiceImpl} implementation.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Path("/rest/cases")
public class CaseServiceRestImpl implements CaseServiceRest {

	@Inject
	private CaseService cService;
	@Inject
	private CaseFileService cfService;

	@Override
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseModel> getAllCases() {
		List<CaseModel> caseList = cService.getAllCases();
		return caseList;
	}
	
	@Override
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
	public List<CaseModel> getAllPrimaryCases(@DefaultValue("ACTIVE") @QueryParam("state") String state) {
		List<CaseModel> caseList = cService.getPrimaryCases(state);
		return caseList;
	}
    
	@Override
    @GET
    @Path("/{id}/subcases")
    @Produces(MediaType.APPLICATION_JSON)
	public List<CaseModel> getAllSecondaryCases(@PathParam("id") long id) {
		CaseModel shallowCase = new CaseModel();
		shallowCase.setId(id);
		List<CaseModel> caseList = cService.getSecondaryCases(shallowCase);
		return caseList;
	}

	@Override
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public CaseModel getCaseById(@PathParam("id") long id) {
		CaseModel shallowCaseModel = new CaseModel();
		shallowCaseModel.setId(id);
		CaseModel foundCase = cService.getCaseById(shallowCaseModel);
		return foundCase;
	}

	@Override
	@GET
	@Path("/blueprints")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCaseBlueprintNames() {
		List<String> instantiableBlueprintNames = CaseFactory.getInstantiableCaseModelNames();
		return instantiableBlueprintNames;
	}

	@Override
	@POST
	@Path("/start")
	@Consumes(MediaType.TEXT_PLAIN)
	public void startCase(String caseName) {
		List<String> instantiableBlueprintNames = CaseFactory.getInstantiableCaseModelNames();
		if (instantiableBlueprintNames.contains(caseName)) {
			CaseModel cm = CaseFactory.getCaseModelByName(caseName);
			cService.persistCase(cm);
		}

	}

	@Override
	@DELETE
	@Path("/{id}")
	public void deleteCase(@PathParam("id") long id) {
		CaseModel shallowCaseModel = new CaseModel();
		shallowCaseModel.setId(id);
		cService.deleteCase(shallowCaseModel);

	}
	
	@Override
	@GET
	@Path("/{id}/data")
	@Produces(MediaType.APPLICATION_JSON)
//	public Response getCaseFileItems(@PathParam("id") long id) {
	public List<CaseFileItem> getCaseFileItems(@PathParam("id") long id) {
		CaseModel shallowCase = new CaseModel();
		shallowCase.setId(id);
		List<CaseFileItem> cfi = cfService.getAllCaseFileItems(shallowCase);
//        GenericEntity<List<CaseFileItem>> list = new GenericEntity<List<CaseFileItem>>(cfi) {};
//        return Response.ok(list).build();
		return cfi;
	}

	@Override
	@GET
	@Path("/{id}/elements")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseElement> getElementsInCase(@PathParam("id") long id) {
		CaseModel caseId = new CaseModel();
		caseId.setId(id);
		return cService.getElementsInCase(caseId);
	}

	@Override
	@GET
	@Path("/{id}/tasks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Task> getTasksInCase(@PathParam("id") long id) {
		CaseModel caseId = new CaseModel();
		caseId.setId(id);
		return cService.getTasksInCase(caseId);
	}

}
