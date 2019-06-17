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

import java.util.List;

import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import cm.core.data.CaseFileItemAttachment;

public class AttachmentGrid extends VerticalLayout {

	private Button viewBtn;
	private Button deleteBtn;
	private Extension filedownloaderEx;
	
	private Grid<CaseFileItemAttachment> grid = new Grid<>(CaseFileItemAttachment.class);
	
	private CaseFileItemAttachment selectedAttachment;
//	private Link link = new Link("Download Attachment", null);
	
	public AttachmentGrid(List<CaseFileItemAttachment> attachmentList, Button download, Button delete) {
		this.viewBtn = download;
		this.deleteBtn = delete;
		grid.setColumns("name", "type");
		if(attachmentList != null) {
			grid.setItems(attachmentList);
		}
		this.addComponent(this.grid);
//		this.addComponent(this.link);
		
		
		this.setWidth("100%");
		this.setSizeFull();
		
		grid.asSingleSelect().addValueChangeListener(event -> {

			if (event.getValue() == null) {
				this.selectedAttachment = null;
				this.viewBtn.setEnabled(false);
				this.deleteBtn.setEnabled(false);
//				this.link.setEnabled(false);
			} else if (event.getValue() != null){
				this.selectedAttachment = event.getValue();
				this.deleteBtn.setEnabled(true);
				if(selectedAttachment != null) {
					if(this.filedownloaderEx != null) {
						this.viewBtn.removeExtension(this.filedownloaderEx);
					}
					CaseFileItemAttachment att = selectedAttachment;
					StreamSource attachmentResource = new AttachmentSource(att);
					StreamResource streamResource = new StreamResource(attachmentResource, att.getName());
					FileDownloader fileDownloader = new FileDownloader(streamResource);
					fileDownloader.extend(viewBtn);
					this.filedownloaderEx = fileDownloader;
					this.viewBtn.setEnabled(true);
				}				
			}
		});
	}
	
	public void updateList(List<CaseFileItemAttachment> attachmentList) {		
		this.grid.setItems(attachmentList);
	}
	
	public CaseFileItemAttachment getSelectedAttachment() {
		return this.selectedAttachment;
	}
	
}
