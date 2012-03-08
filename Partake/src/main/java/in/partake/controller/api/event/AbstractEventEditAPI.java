package in.partake.controller.api.event;

import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;
import in.partake.model.daofacade.deprecated.DeprecatedEventDAOFacade;
import in.partake.model.dto.BinaryData;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractEventEditAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    protected boolean updateEventFromParameter(UserEx user, Event event, JSONObject invalidParameters) throws DAOException {        
        // Title
        String title = getParameter("title");
        if (StringUtils.isBlank(title) || title.length() > 100)
            invalidParameters.put("title", "タイトルは 100 文字以下で必ず入力してください。");
        else
            event.setTitle(title);

        // Summary
        String summary = getParameter("summary");
        if (summary != null && summary.length() > 100)
            invalidParameters.put("summary", "概要は 100 文字以下で入力してください。");
        else
            event.setSummary(summary);
        
        // Category
        String category = getParameter("category");
        if (category == null || !EventCategory.isValidCategoryName(category))
            invalidParameters.put("category", "カテゴリーは正しいものを必ず入力してください。");
        else
            event.setCategory(category);

        // BeginDate
        Date beginDate = getDateParameter("beginDate");
        {
            if (beginDate == null)
                invalidParameters.put("beginDate", "開始日時は必ず入力してください。");
            else {
                Calendar beginCalendar = TimeUtil.calendar(beginDate);
                if (beginCalendar.get(Calendar.YEAR) < 2000 || 2100 < beginCalendar.get(Calendar.YEAR))
                    invalidParameters.put("beginDate", "開始日時の範囲が不正です。");
                event.setBeginDate(beginDate);
            }
        }
        
        // EndDate
        {
            Date endDate = getDateParameter("endDate");
            Calendar endCalendar = TimeUtil.calendar(endDate);
            if (endDate == null)
                event.setEndDate(null);
            else if (endCalendar.get(Calendar.YEAR) < 2000 || 2100 < endCalendar.get(Calendar.YEAR))
                invalidParameters.put("endDate", "終了日時の範囲が不正です。");
            else if (beginDate != null && endDate.before(beginDate))
                invalidParameters.put("endDate", "終了日時が開始日時より前になっています。");
            else
                event.setEndDate(endDate);
        }
        
        // Deadline
        {
            Date deadline = getDateParameter("deadline");
            Calendar deadlineCalendar = TimeUtil.calendar(deadline);
            if (deadline == null)                
                event.setDeadline(null);
            else if (deadlineCalendar.get(Calendar.YEAR) < 2000 || 2100 < deadlineCalendar.get(Calendar.YEAR))
                invalidParameters.put("deadline", "締切日時が範囲外の値になっています。");
            else
                event.setDeadline(deadline);
        }
        
        // Capacity
        Integer capacity = getIntegerParameter("capacity");
        if (capacity != null && 0 <= capacity)
            event.setCapacity(capacity);
        else if (capacity == null)
            event.setCapacity(0);
        else
            invalidParameters.put("capacity", "定員が範囲外の値になっています。");

        // URL
        {
            String urlStr = getParameter("url");
            if (StringUtils.isBlank(urlStr))
                event.setUrl(urlStr);
            else if (3000 < urlStr.length())
                invalidParameters.put("url", "URL が長すぎます");
            else if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://"))
                invalidParameters.put("url", "URL が不正です。");
            else {
                try {
                    new URL(urlStr);  // Confirms URL is not malformed. 
                    event.setUrl(urlStr);
                } catch (MalformedURLException e) {
                    invalidParameters.put("url", "URL が不正です。");
                }
            }
        }        
        
        // Field
        {
            String place = getParameter("place");
            if (place != null && 300 < place.length())
                invalidParameters.put("place", "場所が長すぎます");
            else
                event.setPlace(place);
        }
        
        // Address
        {
            String address = getParameter("address");
            if (address != null && 300 < address.length())
                invalidParameters.put("address", "住所が長すぎます。");
            else
                event.setAddress(address);
        }

        {
            String description = getParameter("description");
            if (description != null && 100000 < description.length())
                invalidParameters.put("description", "説明は 100000 文字以下で入力してください。");
            else
                event.setDescription(description);
        }

        {
            String hashTag = getParameter("hashTag");
            if (hashTag != null && 100 < hashTag.length())
                invalidParameters.put("hashTag", "ハッシュタグは１００文字以内で記述してください。");
            else
                event.setHashTag(hashTag);
        }

        {
            Boolean secret = getBooleanParameter("secret");
            if (secret != null && secret)
                event.setPrivate(true);
            else
                event.setPrivate(false);
        }

        if (event.isPrivate()) {
            String passcode = getParameter("passcode");
            if (passcode == null)
                invalidParameters.put("passcode", "パスコードを設定してください。");
            else if (20 < passcode.length())
                invalidParameters.put("passcode", "パスコードは20文字以下で記入してください。");
            else
                event.setPasscode(passcode);
        }

        {
            String editors = getParameter("editors");
            if (editors != null && 120 < editors.length())
                invalidParameters.put("editors", "編集者が多すぎます。");
            else
                event.setManagerScreenNames(editors);
        }
        
        {
            String foreImageId = getParameter("foreImageId");
            if (foreImageId != null && Util.isUUID(foreImageId)) {
                // Checks foreImageId is one of your images.
                // TODO: We can do this in light-weight way. 
                BinaryData data = DeprecatedEventDAOFacade.get().getBinaryData(foreImageId);
                if (data == null)
                    invalidParameters.put("foreImageId", "画像IDが不正です。");
                else if (StringUtils.equals(foreImageId, event.getForeImageId()))
                    event.setForeImageId(foreImageId);
                else if (!StringUtils.equals(user.getId(), data.getUserId()))
                    invalidParameters.put("foreImageId", "画像IDが不正です。");
                else
                    event.setForeImageId(foreImageId);
            } else if (foreImageId != null) {
                invalidParameters.put("foreImageId", "画像IDが不正です。");
            } else {
                event.setForeImageId(null);
            }
        }
        
        {
            String backImageId = getParameter("backImageId");
            if (backImageId != null && Util.isUUID(backImageId)) {
                // Checks foreImageId is one of your images.
                // TODO: We can do this in light-weight way. 
                BinaryData data = DeprecatedEventDAOFacade.get().getBinaryData(backImageId);
                if (data == null)
                    invalidParameters.put("backImageId", "画像IDが不正です。");
                else if (StringUtils.equals(backImageId, event.getForeImageId()))
                    event.setForeImageId(backImageId);
                else if (!StringUtils.equals(user.getId(), data.getUserId()))
                    invalidParameters.put("backImageId", "画像IDが不正です。");
                else
                    event.setForeImageId(backImageId);
            } else if (backImageId != null) {
                invalidParameters.put("backImageId", "画像IDが不正です。");
            } else {
                event.setBackImageId(null);
            }
        }

//        // hashtag は # から始まり、 a-zA-Z0-9_- のいずれかで構成されているべき
//        if (!StringUtils.isEmpty(hashTag) && !Util.isValidHashtag(hashTag)) {
//            addFieldError("hashtag", "ハッシュタグは # から始まる英数字や日本語が指定できます。記号は使えません。");
//        }
//
//        // EventRelation は、同じ eventId が複数でてはならない
//        {
//            Set<String> set = new HashSet<String>();
//            String[] relatedEventIDs = new String[] { relatedEventID1, relatedEventID2, relatedEventID3 };
//            for (int i = 0; i < relatedEventIDs.length; ++i){
//                if (!StringUtils.isEmpty(relatedEventIDs[i])) {
//                    if (set.contains(relatedEventIDs[i])) {
//                        addFieldError("relatedEventID" + (i + 1), "ID が重複しています。");
//                    } else {
//                        set.add(relatedEventIDs[i]);
//                    }
//                }
//            }
//        }
        
//        
//
//        public void setRelatedEventID1(String id) { relatedEventID1 = id; }
//        public void setRelatedEventID2(String id) { relatedEventID2 = id; }
//        public void setRelatedEventID3(String id) { relatedEventID3 = id; }
//
//        public void setRelatedEventRequired1(boolean b) { relatedEventRequired1 = b; }
//        public void setRelatedEventRequired2(boolean b) { relatedEventRequired2 = b; }
//        public void setRelatedEventRequired3(boolean b) { relatedEventRequired3 = b; }
//
//        public void setRelatedEventPriority1(boolean b) { relatedEventPriority1 = b; }
//        public void setRelatedEventPriority2(boolean b) { relatedEventPriority2 = b; }
//        public void setRelatedEventPriority3(boolean b) { relatedEventPriority3 = b; }
        
        
        return invalidParameters.isEmpty();
    }
}
