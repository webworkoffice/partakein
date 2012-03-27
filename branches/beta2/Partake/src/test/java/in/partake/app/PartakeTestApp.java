package in.partake.app;

import in.partake.service.ITwitterService;

public class PartakeTestApp extends PartakeApp {

    public static void setTwitterService(ITwitterService twitterService) {
        PartakeApp.twitterService = twitterService;
    }
}
