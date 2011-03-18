package in.partake.page.event;

import in.partake.application.PartakeSession;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.page.base.PartakePanel;
import in.partake.resource.I18n;
import in.partake.service.EventService;
import in.partake.util.KeyValuePair;
import in.partake.util.Util;
import in.partake.wicket.component.AsIsComponent;
import in.partake.wicket.component.InvisibleComponent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class EventEditPanel extends PartakePanel {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(EventEditPanel.class);

    private static final List<KeyValuePair> YEARS = convertToKVP(new String[] {
            "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019"
            });
    private static final List<KeyValuePair> MONTHS = convertToKVP(new String[] {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
            });
    private static final List<KeyValuePair> DATES = convertToKVP(new String[] {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
            });
    private static final List<KeyValuePair> HOURS = convertToKVP(new String[] {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
            });
    private static final List<KeyValuePair> MINS = convertToKVP(new String[] {
            "0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"
            });
    
    // フィールドをデフォルト値で作成しておく。
    private final ChoiceRenderer<KeyValuePair> choiceRenderer = new ChoiceRenderer<KeyValuePair>("value", "key");
    private final TextField<String> titleField = new TextField<String>("title", new Model<String>(""));
    private final TextField<String> summaryField = new TextField<String>("summary", new Model<String>(""));
    private final DropDownChoice<KeyValuePair> categoryChoice = new DropDownChoice<KeyValuePair>("category", new Model<KeyValuePair>(EventCategory.CATEGORIES.get(0)), EventCategory.CATEGORIES, choiceRenderer); 
    private final TextArea<String> descriptionField = new TextArea<String>("description", new Model<String>(""));
    
    private final DropDownChoice<KeyValuePair> syearChoice = createDropDownChoice("syear", YEARS, YEARS.get(0));
    private final DropDownChoice<KeyValuePair> smonthChoice = createDropDownChoice("smonth", MONTHS, MONTHS.get(0));
    private final DropDownChoice<KeyValuePair> sdayChoice = createDropDownChoice("sday", DATES, DATES.get(0));
    private final DropDownChoice<KeyValuePair> shourChoice = createDropDownChoice("shour", HOURS, HOURS.get(0));
    private final DropDownChoice<KeyValuePair> sminChoice = createDropDownChoice("smin", MINS, MINS.get(0));
    
    private final CheckBox usesEndDateCheckBox = new CheckBox("edate.use", new Model<Boolean>(false));
    private final DropDownChoice<KeyValuePair> eyearChoice = createDropDownChoice("eyear", YEARS, YEARS.get(0)); 
    private final DropDownChoice<KeyValuePair> emonthChoice = createDropDownChoice("emonth", MONTHS, MONTHS.get(0));
    private final DropDownChoice<KeyValuePair> edayChoice = createDropDownChoice("eday", DATES, DATES.get(0));
    private final DropDownChoice<KeyValuePair> ehourChoice = createDropDownChoice("ehour", HOURS, HOURS.get(0));
    private final DropDownChoice<KeyValuePair> eminChoice = createDropDownChoice("emin", MINS, MINS.get(0));

    private final CheckBox usesDeadlineCheckBox = new CheckBox("ddate.use", new Model<Boolean>(false));
    private final DropDownChoice<KeyValuePair> dyearChoice = createDropDownChoice("dyear", YEARS, YEARS.get(0)); 
    private final DropDownChoice<KeyValuePair> dmonthChoice = createDropDownChoice("dmonth", MONTHS, MONTHS.get(0));
    private final DropDownChoice<KeyValuePair> ddayChoice = createDropDownChoice("dday", DATES, DATES.get(0));
    private final DropDownChoice<KeyValuePair> dhourChoice = createDropDownChoice("dhour", HOURS, HOURS.get(0));
    private final DropDownChoice<KeyValuePair> dminChoice = createDropDownChoice("dmin", MINS, MINS.get(0));

    private final TextField<String> capacityField = new TextField<String>("capacity", new Model<String>("0"));
    private final AsIsComponent noforeimage = new AsIsComponent("noforeimage");
    private final FileUploadField uploadForeImage = new FileUploadField("noforeimage.file", new Model<FileUpload>());
    private final InvisibleComponent foreimage = new InvisibleComponent("foreimage");

    private final AsIsComponent nobackimage = new AsIsComponent("nobackimage");
    private final FileUploadField uploadBackImage = new FileUploadField("nobackimage.file", new Model<FileUpload>());
    private final InvisibleComponent backimage = new InvisibleComponent("backimage");

    private final TextField<String> placeField = new TextField<String>("place", new Model<String>(""));
    private final TextField<String> addressField = new TextField<String>("address", new Model<String>(""));
    private final TextField<String> urlField = new TextField<String>("url", new Model<String>(""));
    private final TextField<String> hashTagField = new TextField<String>("hashtag", new Model<String>(""));
    private final CheckBox secretCheckBox = new CheckBox("secret", new Model<Boolean>(false));
    private final TextField<String> passcodeField = new TextField<String>("passcode", new Model<String>(""));
    
    private final TextField<String> relatedEventId1Field = new TextField<String>("related.1.event.id", new Model<String>(""));
    private final TextField<String> relatedEventId2Field = new TextField<String>("related.2.event.id", new Model<String>(""));
    private final TextField<String> relatedEventId3Field = new TextField<String>("related.3.event.id", new Model<String>(""));
    private final CheckBox relatedEventRequired1CheckBox = new CheckBox("related.1.event.required", new Model<Boolean>(false));
    private final CheckBox relatedEventRequired2CheckBox = new CheckBox("related.2.event.required", new Model<Boolean>(false));
    private final CheckBox relatedEventRequired3CheckBox = new CheckBox("related.3.event.required", new Model<Boolean>(false));
    private final CheckBox relatedEventPriority1CheckBox = new CheckBox("related.1.event.priority", new Model<Boolean>(false));
    private final CheckBox relatedEventPriority2CheckBox = new CheckBox("related.2.event.priority", new Model<Boolean>(false));
    private final CheckBox relatedEventPriority3CheckBox = new CheckBox("related.3.event.priority", new Model<Boolean>(false));

    private final TextField<String> managersField = new TextField<String>("managers", new Model<String>(""));    
    private final ImageButton submitButton = new ImageButton("submit", "/images/dummy");
    
    
    /**
     * 新規イベント作成
     * @param id
     * @param event
     */
    public EventEditPanel(String id) {
        super(id);
        
        PartakeSession session = PartakeSession.get();
        final UserEx user = session.getCurrentUser();
        if (user == null) {
            renderLoginRequired();
            return;
        }
        
        // まずデフォルトの
        
        Date now = new Date();
        Date oneDayAfter = new Date(now.getTime() + 1000 * 3600 * 24);
        oneDayAfter.setMinutes(0); // FIXME: minutes を 0 へセット。deprecated を使わない。
        Calendar date = new GregorianCalendar();
        date.setTime(oneDayAfter);
        
        syearChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.YEAR)), YEARS));
        smonthChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.MONTH + 1)), MONTHS));
        sdayChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.DATE)), DATES));
        shourChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.HOUR_OF_DAY)), HOURS));
        sminChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.MINUTE)), MINS));

        eyearChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.YEAR)), YEARS));
        emonthChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.MONTH + 1)), MONTHS));
        edayChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.DATE)), DATES));
        ehourChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.HOUR_OF_DAY)), HOURS));
        eminChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.MINUTE)), MINS));

        dyearChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.YEAR)), YEARS));
        dmonthChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.MONTH + 1)), MONTHS));
        ddayChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.DATE)), DATES));
        dhourChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.HOUR_OF_DAY)), HOURS));
        dminChoice.setModel(createKVPModel(String.valueOf(date.get(Calendar.MINUTE)), MINS));

        submitButton.add(new SimpleAttributeModifier("src", "/images/button-eventform.png"));
        
        Form<Event> form = new Form<Event>("eventform") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                try {
                    PartakeSession session = PartakeSession.get();
                    Date now = new Date();
                    
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    
                    Date beginDate;
                    {
                        int syear  = Integer.parseInt(syearChoice.getModelValue());
                        int smonth = Integer.parseInt(smonthChoice.getModelValue());
                        int sday   = Integer.parseInt(sdayChoice.getModelValue());
                        int shour  = Integer.parseInt(shourChoice.getModelValue());
                        int smin   = Integer.parseInt(sminChoice.getModelValue());
                        calendar.set(syear, smonth - 1, sday, shour, smin, 0);
                        beginDate = calendar.getTime();
                    }
                    
                    Date endDate;
                    if (usesEndDateCheckBox.getModelObject()) {
                        int eyear  = Integer.parseInt(eyearChoice.getModelValue());
                        int emonth = Integer.parseInt(emonthChoice.getModelValue());
                        int eday   = Integer.parseInt(edayChoice.getModelValue());
                        int ehour  = Integer.parseInt(ehourChoice.getModelValue());
                        int emin   = Integer.parseInt(eminChoice.getModelValue());
                        calendar.set(eyear, emonth - 1, eday, ehour, emin, 0);
                        endDate = calendar.getTime();
                    } else {
                        endDate = null;
                    }
                    
                    Date deadline;
                    if (usesDeadlineCheckBox.getModelObject()) {
                        int dyear  = Integer.parseInt(dyearChoice.getModelValue());
                        int dmonth = Integer.parseInt(dmonthChoice.getModelValue());
                        int dday   = Integer.parseInt(ddayChoice.getModelValue());
                        int dhour  = Integer.parseInt(dhourChoice.getModelValue());
                        int dmin   = Integer.parseInt(dminChoice.getModelValue());
                        calendar.set(dyear, dmonth - 1, dday, dhour, dmin, 0);
                        deadline = calendar.getTime();
                    } else {
                        deadline = null;
                    }


                    // 新規イベントの作成
                    // image を生成
                    BinaryData foreImageEmbryo = createBinaryDataEmbryo(uploadForeImage.getFileUpload());
                    BinaryData backImageEmbryo = createBinaryDataEmbryo(uploadBackImage.getFileUpload());

                    
                    Event embryo = new Event(
                            "", titleField.getModelObject(), summaryField.getModelObject(), categoryChoice.getModelObject().getKey(),
                            deadline, beginDate, endDate,
                            Integer.parseInt(capacityField.getModelObject()), urlField.getModelObject(), placeField.getModelObject(), addressField.getModelObject(), descriptionField.getModelObject(),
                            hashTagField.getModelObject(), user.getId(), managersField.getModelObject(), secretCheckBox.getModelObject(), passcodeField.getModelObject(), false, false,
                            now, null);

                    try {
                        String eventId = EventService.get().create(embryo, foreImageEmbryo, backImageEmbryo); 
                        embryo.setId(eventId);
                        
//                        // TODO: これはひどい
//                        // related event を登録        
//                        List<EventRelation> eventRelations = new ArrayList<EventRelation>();
//                        Set<EventRelationPK> eventRelationPKs = new HashSet<EventRelationPK>();
//                        if (!StringUtils.isEmpty(relatedEventID1) && !eventRelationPKs.contains(new EventRelationPK(eventId, Util.removeURLFragment(relatedEventID1)))) {
//                            eventRelations.add(new EventRelation(eventId, Util.removeURLFragment(relatedEventID1), relatedEventRequired1, relatedEventPriority1));
//                            eventRelationPKs.add(new EventRelationPK(eventId, Util.removeURLFragment(relatedEventID1)));
//                        }
//                        if (!StringUtils.isEmpty(relatedEventID2) && !eventRelationPKs.contains(new EventRelationPK(eventId, Util.removeURLFragment(relatedEventID2)))) {
//                            eventRelations.add(new EventRelation(eventId, Util.removeURLFragment(relatedEventID2), relatedEventRequired2, relatedEventPriority2));
//                            eventRelationPKs.add(new EventRelationPK(eventId, Util.removeURLFragment(relatedEventID1)));
//                        }
//                        if (!StringUtils.isEmpty(relatedEventID3) && !eventRelationPKs.contains(new EventRelationPK(eventId, Util.removeURLFragment(relatedEventID3)))) {
//                            eventRelations.add(new EventRelation(eventId, Util.removeURLFragment(relatedEventID3), relatedEventRequired3, relatedEventPriority3));
//                            eventRelationPKs.add(new EventRelationPK(eventId, Util.removeURLFragment(relatedEventID1)));
//                        }
//                        
//                        EventService.get().setEventRelations(eventId, eventRelations);
                        
                        session.addMessage("新しいイベントが作成されました");
                        setResponsePage(EventShowPage.class, new PageParameters().add("id", eventId));
                    } catch (DAOException e) {
                        logger.error(I18n.t(I18n.DATABASE_ERROR), e);
                        renderDBError();
                    }
                } catch (NumberFormatException e) {
                    renderInvalidRequest("不正な文字列が指定されました。");
                    return;
                }
            }
        };
        
        noforeimage.add(uploadForeImage);
        nobackimage.add(uploadBackImage);
        
        form.add(titleField, summaryField, categoryChoice, descriptionField);
        form.add(syearChoice, smonthChoice, sdayChoice, shourChoice, sminChoice);
        form.add(usesEndDateCheckBox,  eyearChoice, emonthChoice, edayChoice, ehourChoice, eminChoice);
        form.add(usesDeadlineCheckBox, dyearChoice, dmonthChoice, ddayChoice, dhourChoice, dminChoice);
        form.add(capacityField);
        form.add(noforeimage, foreimage);
        form.add(nobackimage, backimage);
        form.add(placeField, addressField, urlField, hashTagField, secretCheckBox, passcodeField);
        form.add(relatedEventId1Field, relatedEventId2Field, relatedEventId3Field);
        form.add(relatedEventRequired1CheckBox, relatedEventRequired2CheckBox, relatedEventRequired3CheckBox);
        form.add(relatedEventPriority1CheckBox, relatedEventPriority2CheckBox, relatedEventPriority3CheckBox);       
        form.add(managersField);
        form.add(submitButton);
        add(form);
        
        
    }
    
    private static List<KeyValuePair> convertToKVP(String[] values) {
        List<KeyValuePair> list = new ArrayList<KeyValuePair>();
        for (String s : values) {
            list.add(new KeyValuePair(s, s));
        }
        return list;
    }
    
    private DropDownChoice<KeyValuePair> createDropDownChoice(String id, List<KeyValuePair> list, KeyValuePair kv) {
        ChoiceRenderer<KeyValuePair> choiceRenderer = new ChoiceRenderer<KeyValuePair>("value", "key");
        return new DropDownChoice<KeyValuePair>(id, createKVPModel(kv.getKey(), list), list, choiceRenderer);
    }
    
    private KeyValuePair findKeyValuePair(String key, List<KeyValuePair> keyValues) {
        for (KeyValuePair keyValue : keyValues) {
            if (keyValue.getKey().equals(key)) {
                return keyValue;
            }
        }
        return keyValues.get(0);
    }
            
    
    /**
     * keyValues から key があれば、それを用いる。なければデフォルト値を用いる。
     * @param key
     * @param keyValues
     * @return
     */
    private Model<KeyValuePair> createKVPModel(String key, List<KeyValuePair> keyValues) {
        return new Model<KeyValuePair>(findKeyValuePair(key, keyValues));
    }
    
    private BinaryData createBinaryDataEmbryo(FileUpload fileUpload) {
        if (fileUpload == null) { return null; }
        
        BinaryData imageEmbryo = null;
        try {
            String contentType = fileUpload.getContentType();
            InputStream inputStream = new BufferedInputStream(uploadForeImage.getFileUpload().getInputStream()); 
            
            if (inputStream != null && Util.isImageContentType(contentType)) {
                byte[] foreImageByteArray = Util.getContentOfStream(inputStream);
                if ("image/pjpeg".equals(contentType)) {
                    imageEmbryo = new BinaryData("image/jpeg", foreImageByteArray);
                } else {
                    imageEmbryo = new BinaryData(contentType, foreImageByteArray);
                }
                
                return imageEmbryo;
            }
        } catch (IOException e) {
            // e.printStackTrace();
            imageEmbryo = null;
        }
        
        return imageEmbryo;
    }
}
