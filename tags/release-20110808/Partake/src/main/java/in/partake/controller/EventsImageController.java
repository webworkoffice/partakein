package in.partake.controller;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.BinaryData;
import in.partake.service.EventService;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Logger;

public class EventsImageController extends PartakeActionSupport {
	static final String IMAGE_ID_PARAM_NAME = "imageId";
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(EventsImageController.class);

	private String contentType = null;
	private ByteArrayInputStream inputStream = null;
	
	public String show() {
		String imageId = getParameter(IMAGE_ID_PARAM_NAME);
		if (imageId == null) { return NOT_FOUND; }
		
		try {
			BinaryData data = EventService.get().getBinaryData(imageId);
			if (data == null) { return NOT_FOUND; }

			this.contentType = data.getType();
			this.inputStream = new ByteArrayInputStream(data.getData());

			return SUCCESS;
		} catch (DAOException e) {
			logger.warn("データベースエラーです。", e);
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
