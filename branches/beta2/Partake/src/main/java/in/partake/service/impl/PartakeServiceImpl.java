package in.partake.service.impl;

import in.partake.service.IBitlyService;
import in.partake.service.PartakeService;

//TODO: should be non-public class?
public class PartakeServiceImpl extends PartakeService {

    @Override
    protected IBitlyService createBitlyService() {
        return new BitlyService();
    }
}
