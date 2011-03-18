package in.partake.page.event;

import in.partake.model.dao.DAOException;
import in.partake.model.dto.BinaryData;
import in.partake.page.base.PartakeBinaryPage;
import in.partake.service.EventService;

import org.apache.wicket.request.mapper.parameter.PageParameters;


public class ImageShowPage extends PartakeBinaryPage {
    private static final long serialVersionUID = 1L;

    public ImageShowPage() {
        renderNotFound();
    }
    
    public ImageShowPage(PageParameters params) {
        String id = params.get("id").toString();
        try {
            BinaryData binary = EventService.get().getBinaryData(id);
            
            if (binary == null) {
                renderNotFound();
                return;
            }
            
            renderBinary(binary.getType(), binary.getData());
        } catch (DAOException e) {
            renderDBError();
        }
    }    
}
