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
package cm.core.services.rest;

import java.util.List;

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
import cm.core.services.CaseService;
import cm.core.services.impl.CaseServiceImpl;
import cm.core.tasks.Task;

/**
 * <p>
 * Provides REST interfaces to list, start, transition and delete
 * {@link CaseModel} instances.
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
public interface CaseServiceRest {

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseModel> getAllCases();

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseModel> getAllPrimaryCases(@DefaultValue("ACTIVE") @QueryParam("state") String state);

	@GET
	@Path("/{id}/subcases")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseModel> getAllSecondaryCases(@PathParam("id") long id);

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public CaseModel getCaseById(@PathParam("id") long id);

	@GET
	@Path("/blueprints")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getCaseBlueprintNames();

	@POST
	@Path("/start")
	@Consumes(MediaType.TEXT_PLAIN)
	public void startCase(String caseName);

	@DELETE
	@Path("/{id}")
	public void deleteCase(@PathParam("id") long id);

	@GET
	@Path("/{id}/data")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseFileItem> getCaseFileItems(@PathParam("id") long id);
	
	@GET
	@Path("/{id}/elements")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseElement> getElementsInCase(@PathParam("id") long id);

	@GET
	@Path("/{id}/tasks")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Task> getTasksInCase(@PathParam("id") long id);

}
