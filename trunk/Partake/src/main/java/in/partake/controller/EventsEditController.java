package in.partake.controller;

import in.partake.model.EventEx;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventCategory;
import in.partake.model.dto.EventRelation;
import in.partake.model.dto.UserPermission;
import in.partake.service.EventService;
import in.partake.util.KeyValuePair;
import in.partake.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;
import com.opensymphony.xwork2.validator.annotations.ValidatorType;



// ModelDriven にして annotation を Model の方に書きたいのだが、そのように書くときちんと動いてくれないので、ModelDriven はやめる
// implements ModelDriven<EventEmbryo> 
public class EventsEditController extends PartakeActionSupport implements Validateable {
	/** */
	private static final long serialVersionUID = 1L;			
    private static final Logger logger = Logger.getLogger(EventsEditController.class);

	private String shortId;     // event short id
	private String eventId;		// event id
    private String title;		// event title
    private String summary;     // event summary
    private String category;	// event category
    
    private int syear;
    private int smonth;
    private int sday;
    private int shour;
    private int smin;
    
    private boolean usesEndDate;
	private int eyear;
    private int emonth;
    private int eday;
    private int ehour;
    private int emin;
    
    private boolean usesDeadline;
    private int dyear;
    private int dmonth;
    private int dday;
    private int dhour;
    private int dmin;
    
    private int capacity;		// how many people can attend?
    private String url;			// URL
    private String place;		// event place
    private String address;		// event address;
    private String description;	// event description
    private String hashTag;
    private String ownerId;			// owner
    private boolean secret;	    // true if the event is private.
    private String passcode;	// passcode to show (if not public)
    private String managers;
	
    // 掲載画像
    private boolean removingForeImage;
    private String foreImageId;
    private File foreImage;
    private String foreImageContentType;
    private boolean removingBackImage;
    private String backImageId;
    private File backImage;
    private String backImageContentType;
    
    // 関連イベント
    // TODO: あーこのへん超汚い。もっときれいな解決策があるはず。
    private String  relatedEventID1, relatedEventID2, relatedEventID3;
    private boolean relatedEventRequired1, relatedEventRequired2, relatedEventRequired3;
    private boolean relatedEventPriority1, relatedEventPriority2, relatedEventPriority3;
    
    
    // NOTE: required for showing jsp. TODO: really? 
    public List<KeyValuePair> getCategories() {
        return EventCategory.CATEGORIES;
    }
    
    @Override
    public void validate() {
    	// annotation で書ききれなかった validation を行う
    	
    	// short id は英数字のみ
    	if (!StringUtils.isEmpty(shortId)) {
    	    if (!shortId.matches("[A-Za-z0-9_]*")) {
    	        addFieldError("shortId", "ショート ID は英数およびアンダースコアのみで指定してください。");
    	    }
    	    if (shortId.length() > 15) {
    	        addFieldError("shortId", "ショート ID は 15 文字以内で入力してください。");
    	    }
    	}
    	
        // カテゴリーのチェック
        if (!EventCategory.isValidCategoryName(category)) {
            addFieldError("category", "不正なカテゴリーが指定されました");
        }
    	
    	// 開始/終了が日付として不正でないかチェック。２月３０日とか...
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	
    	calendar.setLenient(false);
    	Date sdate = null, edate = null, ddate = null;
    	
    	try {
    		// NOTE: 月は 0 から始まる。
    		calendar.set(syear, smonth - 1, sday, shour, smin, 0);
    		// set のときには例外が投げられないが get すると投げられるという仕様らしい
    		calendar.get(Calendar.YEAR);
    		calendar.get(Calendar.MONTH);
    		calendar.get(Calendar.DATE);
    		calendar.get(Calendar.HOUR_OF_DAY);
    		calendar.get(Calendar.MINUTE);
    		
    		sdate = calendar.getTime();
    		
    		// 今より昔のイベントはナシ -- と思ったけどまあ、いいや。これがあると結構うざかった。
//    		if (sdate.before(new Date())) {
//        		addFieldError("sday", "開始日時が現在より前です");
//    		}
    		
    	} catch (IllegalArgumentException e) {
    		addFieldError("sday", "不正な開始日時です");
    	}
    	
    	if (isUsesEndDate()) {
        	try {
        		calendar.set(eyear, emonth - 1, eday, ehour, emin, 0);
        		calendar.get(Calendar.YEAR);
        		calendar.get(Calendar.MONTH);
        		calendar.get(Calendar.DATE);
        		calendar.get(Calendar.HOUR_OF_DAY);
        		calendar.get(Calendar.MINUTE);
        		
        		// この時点で edate のチェックは終わりなので、sdate <= edate をここでチェックできるはず
        		edate = calendar.getTime();
        		if (sdate != null && edate != null) {
        			if (!sdate.before(edate)) {
        				addFieldError("eyear", "終了時刻が開始時刻より前になっています");
        			}
        		}
        	} catch (IllegalArgumentException e) {
        		addFieldError("eyear", "不正な終了日時です");
        	}
    	}
    	
    	// deadline の設定
    	if (isUsesDeadline()) {
        	try {
        		calendar.set(dyear, dmonth - 1, dday, dhour, dmin, 0);
        		calendar.get(Calendar.YEAR);
        		calendar.get(Calendar.MONTH);
        		calendar.get(Calendar.DATE);
        		calendar.get(Calendar.HOUR_OF_DAY);
        		calendar.get(Calendar.MINUTE);
        		
        		// deadline は、開始日時より前でなければならない。
        		ddate = calendar.getTime();
        		if (sdate != null && ddate != null) {
        			if (!(ddate.before(sdate) || ddate.equals(sdate))) {
        				addFieldError("dyear", "締切時刻が開始時刻よりも後になっています");
        			}
        		}
        	} catch (IllegalArgumentException e) {
        		addFieldError("dyear", "不正な締切日時です");
        	}
    	}    	
    	
    	
    	// secret が設定されているのに passcode が空であってはならない。
    	if (isSecret()) {
    		if (StringUtils.isEmpty(getPasscode())) {
    			addFieldError("passcode", "パスコードが空です");
    		}
    	} else {
    		
    	}
    	
    	if (!StringUtils.isEmpty(url)) {
    		// OK if the url starts with http or https.
    		if (!url.startsWith("http://") && !url.startsWith("https://")) {
    			addFieldError("url", "URL が不正です");
    		}
    	}
    	
    	// hashtag は # から始まり、 a-zA-Z0-9_- のいずれかで構成されているべき
    	if (!StringUtils.isEmpty(hashTag) && !Util.isValidHashtag(hashTag)) {
    		addFieldError("hashtag", "ハッシュタグは # から始まる英数字のみが指定できます。");
    	}
    	
    	// TODO: 画像のチェック？
    	
    	super.validate();
    }
    
    // ------------------------------
    // 新しい event を作成
    
	// GET /orders/new
    @SkipValidation
    public String editNew() {
		Calendar oneDayAfter = Calendar.getInstance();	// TODO use Locale
		oneDayAfter.setTimeInMillis(new Date().getTime() + 1000 * 3600 * 24);

		syear  = eyear  = dyear  = oneDayAfter.get(Calendar.YEAR);
		smonth = emonth = dmonth = oneDayAfter.get(Calendar.MONTH) + 1;
		sday   = eday   = dday   = oneDayAfter.get(Calendar.DAY_OF_MONTH);
		shour  = ehour  = dhour  = oneDayAfter.get(Calendar.HOUR_OF_DAY);
		smin   = emin   = dmin   = 0;
		
    	return INPUT;
    }

    
    // POST /events/create
	// TODO: create と update の関数が似ているので結構マージ出来るはず。
    public String create() {
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.MILLISECOND, 0);

    	calendar.set(syear, smonth - 1, sday, shour, smin, 0);
    	Date beginDate = calendar.getTime();
    	
    	Date endDate;
    	if (usesEndDate) {
    		calendar.set(eyear, emonth - 1, eday, ehour, emin, 0);
    		endDate = calendar.getTime();
    	} else {
    		endDate = null;
    	}
    	
    	Date deadline;
    	if (usesDeadline) {
    		calendar.set(dyear, dmonth - 1, dday, dhour, dmin, 0);
    		deadline = calendar.getTime();
    	} else {
    		deadline = null;
    	}
    	
        String[] managerShortNamesArray = managers.split(",");
        List<String> managerShortNames = new ArrayList<String>();
        for (int i = 0; i < managerShortNamesArray.length; ++i) {
            String s = StringUtils.trim(managerShortNamesArray[i]);
            if ("".equals(s)) { continue; }
            managerShortNames.add(s);
        }
    	
    	// login しているはずなので owner が null なことはないはず
        UserEx owner = getLoginUser(); 
        if (owner == null) { return ERROR; }
        
        Date createdAt = new Date(); // now.
        
        // image を生成
        BinaryData foreImageEmbryo = createBinaryDataEmbryo(foreImage, foreImageContentType);
        BinaryData backImageEmbryo = createBinaryDataEmbryo(backImage, backImageContentType);
        
        Event embryo = new Event(
        		shortId, title, summary, category, deadline, beginDate, endDate, capacity, url, place, address, description,
        		hashTag, owner.getId(), managerShortNames,
        		secret, passcode, createdAt, null
        );
        
        // TODO: これはひどい
        // related event を登録        
        List<EventRelation> eventRelations = new ArrayList<EventRelation>();
        if (!StringUtils.isEmpty(relatedEventID1)) { eventRelations.add(new EventRelation(relatedEventID1, relatedEventRequired1, relatedEventPriority1)); }
        if (!StringUtils.isEmpty(relatedEventID2)) { eventRelations.add(new EventRelation(relatedEventID2, relatedEventRequired2, relatedEventPriority2)); }
        if (!StringUtils.isEmpty(relatedEventID3)) { eventRelations.add(new EventRelation(relatedEventID3, relatedEventRequired3, relatedEventPriority3)); }        
        
        try {
        	this.eventId = EventService.get().create(embryo, foreImageEmbryo, backImageEmbryo);
        	EventService.get().setEventRelations(eventId, eventRelations);
        	
        	addActionMessage("新しいイベントが作成されました。");
        	return SUCCESS;
        } catch (DAOException e) {
            e.printStackTrace();
        	return ERROR;
        }
    }
    
    // event を edit
	@SkipValidation
	public String edit() {
	    UserEx partakeUser = getLoginUser();
        if (partakeUser == null) { return LOGIN; }
	    
        EventEx event;
        List<EventRelation> eventRelations;
		try {
		    if (eventId == null) { return ERROR; }
			event = EventService.get().getEventExById(eventId);
			if (event == null) { return NOT_FOUND; }
			eventRelations = EventService.get().getEventRelations(eventId);			
        } catch (DAOException e) {
            e.printStackTrace();
            return ERROR;
        }
        
		// check permission
		if (!event.hasPermission(partakeUser, UserPermission.EVENT_EDIT)) { return PROHIBITED; }
		
		// event から data を copy して input
		copyFromEvent(event);
		copyFromEventRelation(eventRelations);

		// TODO: edate, ddate はなんとかきれいにしてあげたい。		
		
		return INPUT;
	}
	
	// event を edit して commit
	public String commit() {
	    UserEx user = getLoginUser();
		if (user == null) { return LOGIN; }
    	
    	EventEx event;
    	try {
    	    if (eventId == null) { return ERROR; }
    		event = EventService.get().getEventExById(eventId);
    		if (event == null) { return ERROR; }
    	} catch (DAOException e) {
    		e.printStackTrace();
    		return ERROR;
    	}
    	
    	// check permission
    	if (!event.hasPermission(user, UserPermission.EVENT_EDIT)) { return PROHIBITED; }
    	
    	GregorianCalendar calendar = new GregorianCalendar();
    	calendar.set(syear, smonth - 1, sday, shour, smin, 0);
		calendar.set(Calendar.MILLISECOND, 0);
    	Date beginDate = calendar.getTime();
    	
    	Date endDate;
    	if (usesEndDate) {
    		calendar.set(eyear, emonth - 1, eday, ehour, emin, 0);
    		calendar.set(Calendar.MILLISECOND, 0);
    		endDate = calendar.getTime();
    	} else {
    		endDate = null;
    	}
    	
    	Date deadline;
    	if (usesDeadline) {
    		calendar.set(dyear, dmonth - 1, dday, dhour, dmin, 0);
    		calendar.set(Calendar.MILLISECOND, 0);
    		deadline = calendar.getTime();
    	} else {
    		deadline = null;
    	}
    	
        String[] managerShortNamesArray = managers.split(",");
        List<String> managerShortNames = new ArrayList<String>();
        for (int i = 0; i < managerShortNamesArray.length; ++i) {
            String s = StringUtils.trim(managerShortNamesArray[i]);
            if ("".equals(s)) { continue; }
            managerShortNames.add(s);
        }
    	
    	// 
    	try {
    		Date now = new Date();
    		BinaryData foreImageEmbryo = createBinaryDataEmbryo(foreImage, foreImageContentType);
    		BinaryData backImageEmbryo = createBinaryDataEmbryo(backImage, backImageContentType);
	        Event eventEmbryo = new Event(
	        		shortId, title, summary, category, deadline, beginDate, endDate, capacity, url, place, address, description,
	        		hashTag, event.getOwnerId(), managerShortNames,
	        		secret, passcode, event.getCreatedAt(), now
	        );

	        // TODO: これはひどい
	        // related event を登録        
	        List<EventRelation> eventRelations = new ArrayList<EventRelation>();
	        if (!StringUtils.isEmpty(relatedEventID1)) { eventRelations.add(new EventRelation(Util.removeHash(relatedEventID1), relatedEventRequired1, relatedEventPriority1)); }
	        if (!StringUtils.isEmpty(relatedEventID2)) { eventRelations.add(new EventRelation(Util.removeHash(relatedEventID2), relatedEventRequired2, relatedEventPriority2)); }
	        if (!StringUtils.isEmpty(relatedEventID3)) { eventRelations.add(new EventRelation(Util.removeHash(relatedEventID3), relatedEventRequired3, relatedEventPriority3)); }

	        EventService.get().update(event, eventEmbryo,
	        		foreImageEmbryo != null || removingForeImage, foreImageEmbryo, 
	        		backImageEmbryo != null || removingBackImage, backImageEmbryo);
	        EventService.get().setEventRelations(event.getId(), eventRelations);
	        
        	addActionMessage("イベント情報が変更されました。");
        	this.eventId = event.getId();
        	return SUCCESS;
        } catch (DAOException e) {
            e.printStackTrace();
        	return ERROR;
        }
	}
	
    // POST /events/destroy
	// destroy の時は validation する必要がない
	@SkipValidation
    public String destroy() {
	    UserEx user = getLoginUser();
	    if (user == null) { return LOGIN; }
	    
	    if (eventId == null) { return ERROR; }
	    
	    EventEx event;
	    try {
    	    event = EventService.get().getEventExById(eventId);
    	    
            if (event == null) { return NOT_FOUND; }
            if (!event.hasPermission(user, UserPermission.EVENT_REMOVE)) { return PROHIBITED; }
            
            EventService.get().remove(event);
            
            return SUCCESS;
        } catch (DAOException e) {
            logger.warn("destroy() failed.", e);
            return ERROR;
        }
    }

	// ----------------------------------------------------------------------
	
	private BinaryData createBinaryDataEmbryo(File imageFile, String contentType) {
		BinaryData imageEmbryo = null;
        try {
        	if (imageFile != null && Util.isImageContentType(contentType)) {
        		byte[] foreImageByteArray = Util.getContentOfFile(imageFile);
        		if ("image/pjpeg".equals(contentType)) {
        			imageEmbryo = new BinaryData("image/jpeg", foreImageByteArray);
        		} else {
        			imageEmbryo = new BinaryData(contentType, foreImageByteArray);
        		}
        		
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        	imageEmbryo = null;
        }
        
        return imageEmbryo;
	}
	
	
    // ======================================================================
    // setter and getters

	// TODO: これ Event をそのままもてばえんじゃないの？
	// TODO: date 系は汚いよなあ、なんとかなるはず。
	// copy constructor でなんとかなりませんか。
	private void copyFromEvent(Event event) {
		assert (event != null);
		
		this.shortId = event.getShortId();
		this.eventId = event.getId();
		this.title = event.getTitle();
		this.summary = event.getSummary();
		this.category = event.getCategory();
		
		{
			Calendar date = new GregorianCalendar();
			date.setTime(event.getBeginDate());
			
			this.syear = date.get(Calendar.YEAR);
			this.smonth = date.get(Calendar.MONTH) + 1;
			this.sday = date.get(Calendar.DATE);
			this.shour = date.get(Calendar.HOUR_OF_DAY);
			this.smin = date.get(Calendar.MINUTE);
		}
		
		if (event.getEndDate() != null) {
			this.usesEndDate = true;
			Calendar date = new GregorianCalendar();
			date.setTime(event.getEndDate());
			
			this.eyear = date.get(Calendar.YEAR);
			this.emonth = date.get(Calendar.MONTH) + 1;
			this.eday = date.get(Calendar.DATE);
			this.ehour = date.get(Calendar.HOUR_OF_DAY);
			this.emin = date.get(Calendar.MINUTE);
		} else {
			this.usesEndDate = false;
		}
		
		if (event.getDeadline() != null) {
			this.usesDeadline = true; 
			Calendar date = new GregorianCalendar();
			date.setTime(event.getDeadline());
			
			this.dyear = date.get(Calendar.YEAR);
			this.dmonth = date.get(Calendar.MONTH) + 1;
			this.dday = date.get(Calendar.DATE);
			this.dhour = date.get(Calendar.HOUR_OF_DAY);
			this.dmin = date.get(Calendar.MINUTE);
		} else {
			this.usesDeadline = false;
		}

		this.capacity = event.getCapacity();
		this.place = event.getPlace();
		this.address = event.getAddress();
		this.url = event.getUrl();
		this.description = event.getDescription();
		this.ownerId = event.getOwnerId();
		this.managers = "";
		if (event.getManagerScreenNames() != null) {
		    this.managers = StringUtils.join(event.getManagerScreenNames(), ",");		    
		}
		this.foreImageId = event.getForeImageId();
		this.backImageId = event.getBackImageId();
		this.secret = event.isPrivate();
		this.passcode = event.getPasscode();
		this.hashTag = event.getHashTag();
	}
	
	private void copyFromEventRelation(List<EventRelation> eventRelations) {
		// TODO: これは本当にひどい。目が腐る。みちゃだめー！！！
		if (eventRelations.size() >= 1) {
			relatedEventID1       = eventRelations.get(0).getEventId();
			relatedEventRequired1 = eventRelations.get(0).isRequired();
			relatedEventPriority1 = eventRelations.get(0).hasPriority();
		}

		if (eventRelations.size() >= 2) {
			relatedEventID2       = eventRelations.get(1).getEventId();
			relatedEventRequired2 = eventRelations.get(1).isRequired();
			relatedEventPriority2 = eventRelations.get(1).hasPriority();
		}

		if (eventRelations.size() >= 3) {
			relatedEventID3       = eventRelations.get(2).getEventId();
			relatedEventRequired3 = eventRelations.get(2).isRequired();
			relatedEventPriority3 = eventRelations.get(2).hasPriority();
		}
	}
	
    // ======================================================================
    // setter and getters
	
    public String getEventId() {
		return eventId;
	}

    public String getShortId() {
        return shortId;
    }
    
	public String getTitle() {
		return title;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public String getCategory() {
		return category;
	}

	/**
     * 開始日時の年を西暦で返す
     * @return 開始日時の年
     */
	public int getSyear() {
		return syear;
	}

	public int getSmonth() {
		return smonth;
	}

	public int getSday() {
		return sday;
	}

	public int getShour() {
		return shour;
	}

	public int getSmin() {
		return smin;
	}
	
	// TODO: 英語おかしいんだけどこうしないとちゃんと取ってこれないのよねえ。field 名変えたい
	// TODO: isUsing だったらよかったのに
    public boolean isUsesEndDate() {
		return usesEndDate;
	}

    /**
     * 終了日時の年を西暦で返す
     * @return 終了日時の年
     */
	public int getEyear() {
		return eyear;
	}

	public int getEmonth() {
		return emonth;
	}

	public int getEday() {
		return eday;
	}

	public int getEhour() {
		return ehour;
	}

	public int getEmin() {
		return emin;
	}
	
    public boolean isUsesDeadline() {
		return usesDeadline;
	}

    /**
     * 申込締切時刻の年を西暦で返す
     * @return 申込締切時刻の年
     */
	public int getDyear() {
		return dyear;
	}

	public int getDmonth() {
		return dmonth;
	}

	public int getDday() {
		return dday;
	}

	public int getDhour() {
		return dhour;
	}

	public int getDmin() {
		return dmin;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getUrl() {
		return url;
	}

	public String getPlace() {
		return place;
	}
	
	public String getAddress() {
		return address;
	}

	public String getDescription() {
		return description;
	}
	
	public String getHashTag() {
		return hashTag;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public boolean isSecret() {
		return secret;
	}

	public String getPasscode() {
		return passcode;
	}
	
	public String getManagers() {
	    return managers;
	}
	
	public String getForeImageId() {
		return foreImageId;
	}
	
	public File getForeImage() {
		return foreImage;
	}
	
	public String getForeImageContentType() {
		return foreImageContentType;
	}
	
	public String getBackImageId() {
		return backImageId;
	}
	
	public File getBackImage() {
		return backImage;
	}
	
	public String getBackImageContentType() {
		return backImageContentType;
	}
	
	public boolean isRemovingForeImage() {
		return removingForeImage;
	}

	public boolean isRemovingBackImage() {
		return removingBackImage;
	}
	
	public String getRelatedEventID1() { return relatedEventID1; }
	public String getRelatedEventID2() { return relatedEventID2; }
	public String getRelatedEventID3() { return relatedEventID3; }
	
	public boolean getRelatedEventRequired1() { return relatedEventRequired1; }
	public boolean getRelatedEventRequired2() { return relatedEventRequired2; }
	public boolean getRelatedEventRequired3() { return relatedEventRequired3; }

	public boolean getRelatedEventPriority1() { return relatedEventPriority1; }
	public boolean getRelatedEventPriority2() { return relatedEventPriority2; }
	public boolean getRelatedEventPriority3() { return relatedEventPriority3; }
	
	// ----------------------------------------------------------------------
	// setters
	
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	public void setShortId(String shortId) {
	    this.shortId = shortId;
	}

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "タイトルが必要です")
    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "100", message = "タイトルのながさは 100 文字以下です")
    @RequiredStringValidator(type = ValidatorType.FIELD, message = "タイトルが必要です")
	public void setTitle(String title) {
    	this.title = title;
	}
	
    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "100", message = "概要のながさは 100 文字以下です")
	public void setSummary(String summary) {
		this.summary = summary;
	}
    
    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "カテゴリの指定が必要です")
    public void setCategory(String category) {
    	this.category = category;
    }

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "開始年が必要です")
    @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "2000", max = "2200", message = "開始年が範囲外です")
	public void setSyear(int syear) {
		this.syear = syear;
	}

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "開始月が必要です")
    @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "1", max = "12", message = "開始月が範囲外です")
	public void setSmonth(int smonth) {
		this.smonth = smonth;
	}

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "開始日が必要です")
    @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "1", max = "31", message = "開始日が範囲外です")
	public void setSday(int sday) {
		this.sday = sday;
	}

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "開始時が必要です")
    @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "0", max = "23", message = "開始時が範囲外です")
	public void setShour(int shour) {
		this.shour = shour;
	}

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "開始分が必要です")
    @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "0", max = "59", message = "開始分が範囲外です")
	public void setSmin(int smin) {
		this.smin = smin;
	}

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "終了日付を使う・使わないの設定が必要です")
	public void setUsesEndDate(boolean usesEndDate) {
		this.usesEndDate = usesEndDate;
	}

    // @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "2000", max = "2200", message = "終了年が範囲外です")
	public void setEyear(int eyear) {
		this.eyear = eyear;
	}

	// @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "1", max = "12", message = "終了月が範囲外です")
	public void setEmonth(int emonth) {
		this.emonth = emonth;
	}

	// @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "1", max = "31", message = "終了日が範囲外です")
	public void setEday(int eday) {
		this.eday = eday;
	}

	// @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "0", max = "23", message = "終了時が範囲外です")
	public void setEhour(int ehour) {
		this.ehour = ehour;
	}

	// @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "0", max = "59", message = "終了分が範囲外です")
	public void setEmin(int emin) {
		this.emin = emin;
	}

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "締切日付を使う・使わないの設定が必要です")
	public void setUsesDeadline(boolean usesDeadline) {
		this.usesDeadline = usesDeadline;
	}

    // @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "2000", max = "2200", message = "締切年が範囲外です")
	public void setDyear(int dyear) {
		this.dyear = dyear;
	}

	// @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "1", max = "12", message = "締切月が範囲外です")
	public void setDmonth(int dmonth) {
		this.dmonth = dmonth;
	}

	// @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "1", max = "31", message = "締切日が範囲外です")
	public void setDday(int dday) {
		this.dday = dday;
	}

	// @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "0", max = "23", message = "締切時が範囲外です")
	public void setDhour(int dhour) {
		this.dhour = dhour;
	}

	// @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "0", max = "59", message = "締切分が範囲外です")
	public void setDmin(int dmin) {
		this.dmin = dmin;
	}

    
    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "参加人数の上限の設定が必要です")
    @IntRangeFieldValidator(type = ValidatorType.FIELD, min = "0", max = "1000", message = "参加人数の上限の定数が範囲外です")
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

    @UrlValidator(type = ValidatorType.FIELD, message = "URL が不正です")
	public void setUrl(String url) {
		this.url = url;
	}

    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "100", message = "場所は100文字以下で記入してください")
	public void setPlace(String place) {
		this.place = place;
	}
    
    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "100", message = "住所は100文字以下で記入してください")
	public void setAddress(String address) {
		this.address = address;
	}
    

    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "50000", message = "説明は(HTMLのタグを含めて)50000文字以下で記入してください")
	public void setDescription(String description) {
		this.description = description;
	}
    
    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "100", message = "ハッシュタグは１００文字以内で記述してください")
	public void setHashTag(String hashTag) {
    	this.hashTag = hashTag;		
	}
    

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

    @RequiredFieldValidator(type = ValidatorType.FIELD, message = "パスコードの必要・不要が不正です")
	public void setSecret(boolean secret) {
		this.secret = secret;
	}

    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "20", message = "パスコードは20文字以下で記入してください")
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
    
    @StringLengthFieldValidator(type = ValidatorType.FIELD, maxLength = "120", message = "マネージャーが多すぎます。")
    public void setManagers(String managers) {
        this.managers = managers;
    }
    
	public void setForeImage(File foreImage) {
		this.foreImage = foreImage;
	}
	
	public void setForeImageContentType(String foreImageContentType) {
		this.foreImageContentType = foreImageContentType;
	}
	
	public void setBackImage(File backImage) {
		this.backImage = backImage;
	}
	
	public void setBackImageContentType(String backImageContentType) {
		this.backImageContentType = backImageContentType;
	}
	
	public void setRemovingForeImage(boolean removingForeImage) {
		this.removingForeImage = removingForeImage;
	}

	public void setRemovingBackImage(boolean removingBackImage) {
		this.removingBackImage = removingBackImage;
	}
	
	public void setRelatedEventID1(String id) { relatedEventID1 = id; }
	public void setRelatedEventID2(String id) { relatedEventID2 = id; }
	public void setRelatedEventID3(String id) { relatedEventID3 = id; }
	
	public void setRelatedEventRequired1(boolean b) { relatedEventRequired1 = b; }
	public void setRelatedEventRequired2(boolean b) { relatedEventRequired2 = b; }
	public void setRelatedEventRequired3(boolean b) { relatedEventRequired3 = b; }

	public void setRelatedEventPriority1(boolean b) { relatedEventPriority1 = b; }
	public void setRelatedEventPriority2(boolean b) { relatedEventPriority2 = b; }
	public void setRelatedEventPriority3(boolean b) { relatedEventPriority3 = b; }
}
