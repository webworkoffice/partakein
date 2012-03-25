package in.partake.service.impl;

import in.partake.service.IBitlyService;
import in.partake.service.IEventSearchService;
import in.partake.service.ITwitterService;
import in.partake.service.PartakeService;

//TODO: should be non-public class?
public class PartakeServiceImpl extends PartakeService {

    @Override
    protected IBitlyService createBitlyService() {
        return new BitlyService();
    }
    
    @Override
    protected IEventSearchService createEventSearchService() {
        return new EventSearchService();
    }
    
    @Override
    protected ITwitterService createTwitterService() {
        return new TwitterService();
    }
}
