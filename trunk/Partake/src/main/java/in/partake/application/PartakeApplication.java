package in.partake.application;

import in.partake.page.ContactPage;
import in.partake.page.FeedListPage;
import in.partake.page.TermOfUsePage;
import in.partake.page.TopPage;
import in.partake.page.admin.AdminPage;
import in.partake.page.auth.LoginByTwitterPage;
import in.partake.page.auth.LogoutPage;
import in.partake.page.auth.VerifyForTwitterPage;
import in.partake.page.error.ErrorPage;
import in.partake.page.error.InvalidPage;
import in.partake.page.error.LoginRequiredPage;
import in.partake.page.error.NotFoundPage;
import in.partake.page.error.ProhibitedPage;
import in.partake.page.event.EventEditPage;
import in.partake.page.event.EventNewPage;
import in.partake.page.event.EventSearchPage;
import in.partake.page.event.EventShowPage;
import in.partake.page.event.ImageShowPage;
import in.partake.page.feed.CalendarAllEventPage;
import in.partake.page.feed.CalendarEventPage;
import in.partake.page.feed.CalendarUserPage;
import in.partake.page.feed.FeedAllEventPage;
import in.partake.page.feed.FeedEventPage;
import in.partake.page.user.MyPage;
import in.partake.page.user.PreferencePage;
import in.partake.page.user.UserShowPage;
import in.partake.page.user.UserShowPrivatePage;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

public class PartakeApplication extends WebApplication {
    public PartakeApplication() {
    }

    @Override
    protected void init() {
        super.init();
        
        // --- トップ
        mountPage("/termofuse", TermOfUsePage.class);
        mountPage("/contact", ContactPage.class);
        mountPage("/feedlist", FeedListPage.class);

        // --- auth
        mountPage("/auth/loginByTwitter", LoginByTwitterPage.class);
        mountPage("/auth/verifyForTwitter", VerifyForTwitterPage.class);
        // mountPage("/auth/loginByOpenID", LoginByOpenIDPage.class);
        // mountPage("/auth/connectWithOpenID", ConnectWithOpenIDPage.class);
        // mountPage("/auth/verifyForOpenID", VerifyForTwitterPagePage.class);

        
        mountPage("/auth/logout", LogoutPage.class);
        mountPage("/auth/loginRequired", LoginRequiredPage.class);
        
        // --- admin
        mountPage("/admin", AdminPage.class);
        
        // --- events
        mountPage("/events/${id}", EventShowPage.class);
        mountPage("/events/new", EventNewPage.class);
        mountPage("/events/edit", EventEditPage.class);
        mountPage("/events/search", EventSearchPage.class);
        // mountPage("/events/passcode", );
        // mountPage("/events/removed", );
        
        mountPage("/events/images/${id}", ImageShowPage.class);

        // --- user
        mountPage("/mypage", MyPage.class);
        mountPage("/preference", PreferencePage.class);
        mountPage("/users/private", UserShowPrivatePage.class);
        mountPage("/users/${id}", UserShowPage.class);
        
        // --- feed
        mountPage("/feed/all", FeedAllEventPage.class);
        mountPage("/feed/category/${category}", FeedEventPage.class);
        mountPage("/calendars/${id}", CalendarUserPage.class);
        mountPage("/calendars/all", CalendarAllEventPage.class);
        mountPage("/calendars/category/${category}", CalendarEventPage.class);
        
        // --- ERROR
        mountPage("/invalid", InvalidPage.class);
        mountPage("/error", ErrorPage.class);
        mountPage("/notfound", NotFoundPage.class);
        mountPage("/prohibited", ProhibitedPage.class);
        mountPage("/loginRequired", LoginRequiredPage.class);
        
        getRequestCycleSettings().setResponseRequestEncoding("utf-8");
        getMarkupSettings().setDefaultMarkupEncoding("utf-8");
    }
    
    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<TopPage> getHomePage() {
        return TopPage.class;
    }
    
    @Override
    public Session newSession(Request request, Response response) {
        System.out.println("newSession is created.");
        return new PartakeSession(request);        
    }
}