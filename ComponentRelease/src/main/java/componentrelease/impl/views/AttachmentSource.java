package componentrelease.impl.views;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.server.StreamResource.StreamSource;

import cm.core.data.CaseFileItemAttachment;

public class AttachmentSource implements StreamSource {

	private CaseFileItemAttachment attachment;
	
	public AttachmentSource(CaseFileItemAttachment attachment) {
		this.attachment = attachment;
	}
	
	@Override
	public InputStream getStream() {
        if (this.attachment != null) {
			// Write to buffer
			byte[] content = this.attachment.getData();
			InputStream targetStream = new ByteArrayInputStream(content);
			return targetStream;
		}
        return null;
	}

}
