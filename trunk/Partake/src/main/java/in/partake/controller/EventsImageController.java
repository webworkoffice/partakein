package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.BinaryData;
import in.partake.service.EventService;

import java.io.ByteArrayInputStream;


public class EventsImageController extends PartakeActionSupport {
	private String contentType = null;
	private ByteArrayInputStream inputStream = null;
	
	public String show() {
		String imageId = getParameter("imageId");
		if (imageId == null) { return NOT_FOUND; }
		
		try {
		    BinaryData data = EventService.get().getBinaryData(imageId);
			
			this.contentType = data.getType();
			this.inputStream = new ByteArrayInputStream(data.getData());
			
			return SUCCESS;
		} catch (DAOException e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	public String getContentType() {
		return this.contentType;
	}
	
	public ByteArrayInputStream getInputStream() {
        return inputStream;
    }
}
