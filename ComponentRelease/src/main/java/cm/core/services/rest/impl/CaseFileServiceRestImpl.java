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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.inject.Inject;
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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import cm.core.data.CaseFileItem;
import cm.core.data.CaseFileItemAttachment;
import cm.core.data.SimpleProperty;
import cm.core.services.CaseFileService;
import cm.core.services.rest.CaseFileServiceRest;

@Path("/rest/data")
public class CaseFileServiceRestImpl implements CaseFileServiceRest {

	@Inject
	private CaseFileService cfService;

	@Override
	@GET
	@Path("/{cfiId}")
	@Produces(MediaType.APPLICATION_JSON)
	public CaseFileItem getCaseFileItem(@PathParam("cfiId") long cfiId) {
		CaseFileItem foundItem = cfService.getCaseFileItem(cfiId);
		return foundItem;
	}

	@Override
	@GET
	@Path("/{cfiId}/properties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SimpleProperty> getCaseFileItemProperties(@PathParam("cfiId") long cfiId) {
		CaseFileItem cfi_single = cfService.getCaseFileItem(cfiId);
		ArrayList<SimpleProperty> props = new ArrayList<>(cfi_single.getProperties());
		return props;
	}

	@Override
	@GET
	@Path("/{cfiId}/properties/{propName}")
	@Produces(MediaType.APPLICATION_JSON)
	public SimpleProperty getCaseFileItemPropertyByName(@PathParam("cfiId") long cfiId, @PathParam("propName") String name) {

		CaseFileItem cfi_single = cfService.getCaseFileItem(cfiId);
		SimpleProperty property = null;
		for (SimpleProperty prop : cfi_single.getProperties()) {
			if (prop.getName().equals(name)) {
				property = prop;
			}
		}
		return property;
	}

	@Override
	@POST
	@Path("/{cfiId}/properties/{propName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateCaseFileItemPropertyByName(@PathParam("cfiId") long cfiId, @PathParam("propName") String name,
			SimpleProperty property) {

		CaseFileItem cfi_single = cfService.getCaseFileItem(cfiId);
		cfService.updatePropertyByName(cfi_single, property);
	}

	@Override
	@PUT
	@Path("/{cfiId}/properties/")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addCaseFileItemProperty(@PathParam("cfiId") long cfiId, SimpleProperty property) {

		CaseFileItem cfi_single = cfService.getCaseFileItem(cfiId);
		String sentName = property.getName();
		boolean exists = false;
		for (SimpleProperty prop : cfi_single.getProperties()) {
			if (prop.getName().equals(sentName)) {
				exists = true;
			}
		}
		if (!exists) {
			cfService.addProperty(cfi_single, property);
		}
	}
	
//	@Override
//	@GET
//	@Path("/properties/{propId}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public SimpleProperty getCaseFileItemPropertyById(@PathParam("propId") long propId) {
//		SimpleProperty found = cfService.getPropertyById(propId);
//		return found;
//	}
//	
//	@Override
//	@POST
//	@Path("/properties/{propId}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void updateCaseFileItemPropertyById(@PathParam("propId") long propId, SimpleProperty property) {
//		SimpleProperty found = cfService.getPropertyById(propId);
//		found.setValue(property.getValue());
//		cfService.updateProperty(found);
//	}
//	
//	@Override
//	@DELETE
//	@Path("/properties/{propId}")
//	public void deleteCaseFileItemPropertyById(@PathParam("propId") long propId) {
//		SimpleProperty found = cfService.getPropertyById(propId);
//		cfService.deleteProperty(found);
//	}

	@Override
	@DELETE
	@Path("/{cfiId}/properties/{propName}")
	public void deleteCaseFileItemPropertyByName(@PathParam("cfiId") long cfiId, @PathParam("propName") String name) {
		CaseFileItem shallowCfi = new CaseFileItem();
		shallowCfi.setId(cfiId);
		SimpleProperty shallowProp = new SimpleProperty();
		shallowProp.setName(name);
		cfService.deleteProperty(shallowCfi, shallowProp);
	}

	@Override
	@GET
	@Path("/{cfiId}/attachments")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CaseFileItemAttachment> getCaseFileItemAttachments(@PathParam("cfiId") long cfiId) {
		List<CaseFileItemAttachment> attachments = null;
		CaseFileItem item = cfService.getCaseFileItem(cfiId);
		attachments = item.getAttachments();
		return attachments;
	}

	@Override
	@PUT
	@Path("/{cfiId}/attachments")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void addCaseFileItemAttachment(@Multipart("file") Attachment att, @PathParam("cfiId") long cfiId) {
		Attachment attachment = att;
		String fileName = attachment.getContentDisposition().getFilename();
		String mimeType = attachment.getContentType().toString();

		CaseFileItemAttachment cfiA = new CaseFileItemAttachment();
		cfiA.setName(fileName);
		cfiA.setType(mimeType);

		byte[] data = null;
		DataHandler dataHandler = null;
		InputStream inputStream = null;
		dataHandler = attachment.getDataHandler();
		try {

			if (null != fileName && !"".equalsIgnoreCase(fileName)) {
				// write & upload file to database
				inputStream = dataHandler.getInputStream();
				data = getData(inputStream);
				cfiA.setData(data);
				cfiA.setSize((data.length / 1048576));
				inputStream.close();
				CaseFileItem cfi = new CaseFileItem();
				cfi.setId(cfiId);
				uploadFileToDatabase(cfi, cfiA);
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
		} finally {
			// release resources, if any
		}
	}

	@Override
	@GET
	@Path("/{cfiId}/attachments/{attId}")
	public Response getCaseFileItemAttachment(@PathParam("attId") long fileId) {
		CaseFileItemAttachment attachment = cfService.getAttachmentById(fileId);
		ResponseBuilder response = Response.ok((Object) attachment.getData(), attachment.getType());
		response.header("Content-Disposition", "attachment; filename=" + attachment.getName());
		return response.build();
	}

	@Override
	@DELETE
	@Path("/{cfiId}/attachments/{attId}")
	public void deleteCaseFileItemAttachment(@PathParam("cfiId") long cfiId, @PathParam("attId") long attId) {
		CaseFileItem shallowItem = new CaseFileItem();
		shallowItem.setId(cfiId);
		CaseFileItemAttachment shallowAttachment = new CaseFileItemAttachment();
		shallowAttachment.setId(attId);
		cfService.deleteAttachment(shallowItem, shallowAttachment);
	}

	// Helper methods to process attachment data
	
	private byte[] getData(InputStream inputStream) {
		int tenMB = 100 * 1024 * 1024;
		ByteArrayOutputStream outputStream = null;
		byte[] bytes = new byte[tenMB];
		try {
			outputStream = new ByteArrayOutputStream();
			int read = 0;

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.flush();
			bytes = outputStream.toByteArray();
			outputStream.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			// release resource, if any
		}
		return bytes;
	}

	private void uploadFileToDatabase(CaseFileItem cfi, CaseFileItemAttachment cfiattachment) {
		cfService.saveAttachment(cfi, cfiattachment);
	}

}
