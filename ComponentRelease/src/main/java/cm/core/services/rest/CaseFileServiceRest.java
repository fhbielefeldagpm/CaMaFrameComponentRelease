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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import cm.core.data.CaseFile;
import cm.core.data.CaseFileItem;
import cm.core.data.CaseFileItemAttachment;
import cm.core.data.SimpleProperty;
import cm.core.services.CaseService;
import cm.core.services.impl.CaseServiceImpl;

/**
 * <p>
 * Provides REST interfaces to list, start, transition and delete
 * {@link CaseFile} and {@link CaseFileItem}s.
 * </p>
 * <p>
 * Implementations use the {@link CaseService} interface and
 * {@link CaseServiceImpl} implementation.
 * </p>
 * 
 * @author André Zensen
 *
 */
@Path("/rest/data")
public interface CaseFileServiceRest {

	@GET
	@Path("/{cfiId}")
	@Produces(MediaType.APPLICATION_JSON)
	public CaseFileItem getCaseFileItem(@PathParam("cfiId") long cfiId);

	@GET
	@Path("/{cfiId}/properties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SimpleProperty> getCaseFileItemProperties(@PathParam("cfiId") long cfiId);

	@GET
	@Path("/{cfiId}/properties/{propName}")
	@Produces(MediaType.APPLICATION_JSON)
	public SimpleProperty getCaseFileItemPropertyByName(@PathParam("cfiId") long cfiId, @PathParam("propName") String name);

	@POST
	@Path("/{cfiId}/properties/{propName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateCaseFileItemPropertyByName(@PathParam("cfiId") long cfiId, @PathParam("propName") String name,
			SimpleProperty property);

	@DELETE
	@Path("/{cfiId}/properties/{propName}")
	public void deleteCaseFileItemPropertyByName(@PathParam("cfiId") long cfiId, @PathParam("propName") String name);

	@PUT
	@Path("/{cfiId}/properties/")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addCaseFileItemProperty(@PathParam("cfiId") long cfiId, SimpleProperty property);
	
	// requires more JPA tuning; for example @DELETE does not remove intermediate mapping table for FK constraints
	// the parent CaseFileItem parent entity does not remove its reference 
//	@GET
//	@Path("/properties/{propId}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public SimpleProperty getCaseFileItemPropertyById(@PathParam("propId") long propId);
//	
//	@POST
//	@Path("/properties/{propId}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void updateCaseFileItemPropertyById(@PathParam("propId") long propId, SimpleProperty property);
//	
//	@DELETE
//	@Path("/properties/{propId}")
//	public void deleteCaseFileItemPropertyById(@PathParam("propId") long propId);

	@GET
	@Path("/{cfiId}/attachments")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseFileItemAttachment> getCaseFileItemAttachments(@PathParam("cfiId") long cfiId);

	@PUT
	@Path("/{cfiId}/attachments")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void addCaseFileItemAttachment(@Multipart("file") Attachment att, @PathParam("cfiId") long cfiId);

	@GET
	@Path("/{cfiId}/attachments/{attId}")
	public Response getCaseFileItemAttachment(@PathParam("attId") long fileId);

	@DELETE
	@Path("/{cfiId}/attachments/{attId}")
	public void deleteCaseFileItemAttachment(@PathParam("cfiId") long cfiId, @PathParam("attId") long attId);
}
