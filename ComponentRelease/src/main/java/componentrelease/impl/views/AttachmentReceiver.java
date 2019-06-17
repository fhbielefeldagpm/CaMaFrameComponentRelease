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
package componentrelease.impl.views;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import cm.core.CaseModel;
import cm.core.data.CaseFile;
import cm.core.data.CaseFileItem;
import cm.core.data.CaseFileItemAttachment;
import cm.core.services.CaseFileService;
import cm.core.tasks.HumanTask;

public class AttachmentReceiver implements Receiver, SucceededListener {

	private ByteArrayOutputStream baos;
//	private List<CaseFileItemAttachment> attachmentList = new ArrayList<>();
	private AttachmentGrid grid;
	private String filename;
	private String mimeType;
	
	private HumanTask taskRef;
	

	public AttachmentReceiver(HumanTask taskRef, AttachmentGrid grid) {
		this.taskRef = taskRef;
		this.grid = grid;
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		this.filename = new String(filename);
		this.mimeType = new String(mimeType);
		baos = new ByteArrayOutputStream(100 * 1024 * 1024);
		return baos;
	}
	
	public byte[] getBytes() {
		return baos.toByteArray();
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		CaseFileItemAttachment attachment = new CaseFileItemAttachment();
		attachment.setName(filename);
		attachment.setType(mimeType);
		attachment.setData(this.baos.toByteArray());

		// get CaseFileService via CDI-context, since @Inject does not work in a non-managed bean
		// apparently mechanics for persisting the new subcase work without persisting it via service
//		CaseService cService = CDI.current().select(CaseService.class).get();
//		cService.persistCase(subCase);
		CaseModel modelRef = this.taskRef.getCaseRef();
		
		CaseFileService cfService = CDI.current().select(CaseFileService.class).get();
		CaseFile caseFile = cfService.getCaseFile(modelRef);
		CaseFileItem item = caseFile.getCaseFileItemById("specifications");
		item.addAttachment(attachment);
		cfService.saveAttachment(item, attachment);
//		cfService.updateCaseFileItem(item);
	}

}
