package in.partake.controller.api.event;

import in.partake.app.PartakeApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserImage;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.UserErrorCode;
import in.partake.service.IEventSearchService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public class ModifyAPI extends AbstractEventEditAPI {
    private static final long serialVersionUID = 1L;

    @Override
    protected String doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();
        Map<String, Object> params = getParameters();

        ModifyTransaction transaction = new ModifyTransaction(user, eventId, params);
        transaction.execute();

        Event event = transaction.getEvent();
        List<EventTicket> tickets = transaction.getEventTickets();

        // If the event is already published, We update event search index.
        IEventSearchService searchService = PartakeApp.getEventSearchService();
        if (!event.isSearchable())
            searchService.remove(eventId);
        else if (searchService.hasIndexed(eventId))
            searchService.update(event, tickets);
        else
            searchService.create(event, tickets);

        JSONObject obj = new JSONObject();
        obj.put("eventId", eventId);
        return renderOK(obj);
    }
}

class ModifyTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private Map<String, Object> params;

    private Event event;
    private List<EventTicket> tickets;

    public ModifyTransaction(UserEx user, String eventId, Map<String, Object> params) {
        this.user = user;
        this.eventId = eventId;
        this.params = params;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
        if (!EventEditPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        event = new Event(event);
        updateEvent(con, daos);

        event.setModifiedAt(TimeUtil.getCurrentDateTime());
        EventDAOFacade.modify(con, daos, event);

        return null;
    }

    private void updateEvent(PartakeConnection con, IPartakeDAOs daos) throws PartakeException, DAOException {
        if (params.containsKey("title")) {
            String title = getString("title");
            if (StringUtils.isBlank(title) || title.length() > 100)
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "title", "タイトルは 100 文字以下で必ず入力してください。");
            else
                event.setTitle(title);
        }

        if (params.containsKey("summary")) {
            String summary = getString("summary");
            if (summary != null && summary.length() > 100)
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "summary", "概要は 100 文字以下で入力してください。");
            else
                event.setSummary(summary);
        }

        if (params.containsKey("category")) {
            String category = getString("category");
            if (category == null || !EventCategory.isValidCategoryName(category))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "category", "カテゴリーは正しいものを必ず入力してください。");
            else
                event.setCategory(category);
        }

        if (params.containsKey("beginDate")) {
            DateTime beginDate = getDateTime("beginDate");
            if (beginDate == null)
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "beginDate", "開始日時は必ず入力してください。");

            Calendar beginCalendar = TimeUtil.calendar(beginDate.toDate());
            if (beginCalendar.get(Calendar.YEAR) < 2000 || 2100 < beginCalendar.get(Calendar.YEAR))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "beginDate", "開始日時の範囲が不正です。");
            else
                event.setBeginDate(beginDate);
        }

        if (params.containsKey("endDate")) {
            DateTime endDate = getDateTime("endDate");
            if (endDate == null)
                event.setEndDate(endDate);
            else {
                Calendar endCalendar = TimeUtil.calendar(endDate.toDate());
                if (endCalendar.get(Calendar.YEAR) < 2000 || 2100 < endCalendar.get(Calendar.YEAR))
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "endDate", "終了日時の範囲が不正です。");
                else if (event.getBeginDate() != null && endDate.isBefore(event.getBeginDate()))
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "endDate", "終了日時が開始日時より前になっています。");
                else
                    event.setEndDate(endDate);
            }
        }

        if (params.containsKey("url")) {
            String urlStr = getString("url");
            if (StringUtils.isBlank(urlStr))
                event.setUrl(urlStr);
            else if (3000 < urlStr.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "url", "URL が長すぎます。");
            else if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://"))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "url", "URL が不正です。");
            else {
                try {
                    new URL(urlStr);  // Confirms URL is not malformed.
                    event.setUrl(urlStr);
                } catch (MalformedURLException e) {
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "url", "URL が不正です。");
                }
            }
        }

        if (params.containsKey("place")) {
            String place = getString("place");
            if (place != null && 300 < place.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "place", "場所が長すぎます");
            else
                event.setPlace(place);
        }

        if (params.containsKey("address")) {
            String address = getString("address");
            if (address != null && 300 < address.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "address", "住所が長すぎます。");
            else
                event.setAddress(address);
        }

        if (params.containsKey("description")) {
            String description = getString("description");
            if (description != null && 1000000 < description.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "description", "説明は 1000000 文字以下で入力してください。");
            else
                event.setDescription(description);
        }

        if (params.containsKey("hashTag")) {
            String hashTag = getString("hashTag");
            if (StringUtils.isBlank(hashTag))
                event.setHashTag(null);
            else if (100 < hashTag.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "hashTag", "ハッシュタグは１００文字以内で記述してください。");
            else if (!Util.isValidHashtag(hashTag))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "hashTag", "ハッシュタグは # から始まる英数字や日本語が指定できます。記号は使えません。");
            else
                event.setHashTag(hashTag);
        }

        if (params.containsKey("passcode")) {
            String passcode = getString("passcode");
            if (StringUtils.isBlank(passcode))
                event.setPasscode(null);
            else if (20 < passcode.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "passcode", "パスコードは20文字以下で記入してください。");
            else
                event.setPasscode(passcode);
        }

        if (params.containsKey("editors")) {
            String editors = getString("editors");
            if (editors != null && 120 < editors.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "editors", "編集者が多すぎます。");
            else
                event.setManagerScreenNames(editors);
        }

        if (params.containsKey("foreImageId")) {
            String foreImageId = getString("foreImageId");
            if (StringUtils.isBlank(foreImageId) || "null".equals(foreImageId))
                event.setForeImageId(null);
            else if (!Util.isUUID(foreImageId))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "foreImageId", "画像IDが不正です。");
            else {
                // Check foreImageId is owned by the owner.
                UserImage image = daos.getImageAccess().find(con, foreImageId);
                if (image == null)
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "foreImageId", "画像IDが不正です。");
                if (!user.getId().equals(image.getUserId()))
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "foreImageId", "あなたが所持していない画像の ID が指定されています。");

                // OK.
                event.setForeImageId(foreImageId);
            }
        }

        if (params.containsKey("backImageId")) {
            String backImageId = getString("backImageId");
            if (StringUtils.isBlank(backImageId) || "null".equals(backImageId))
                event.setBackImageId(null);
            else if (!Util.isUUID(backImageId))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "backImageId", "画像IDが不正です。");
            else {
                // Check foreImageId is owned by the owner.
                UserImage image = daos.getImageAccess().find(con, backImageId);
                if (image == null)
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "backImageId", "画像IDが不正です。");
                if (!user.getId().equals(image.getUserId()))
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "backImageId", "あなたが所持していない画像の ID が指定されています。");

                // OK.
                event.setBackImageId(backImageId);
            }
        }
    }

    private String getString(String key) {
        Object obj = params.get(key);
        if (obj instanceof String)
            return (String) obj;
        else if (obj instanceof String[] && ((String[]) obj).length > 0)
            return ((String[]) obj)[0];
        else
            return null;
    }

    private String[] getStrings(String key) {
        Object obj = params.get(key);
        if (obj instanceof String)
            return new String[] { (String) obj };
        else if (obj instanceof String[])
            return (String[]) obj;
        else
            return null;
    }

    private DateTime getDateTime(String key) {
        String value = getString(key);
        if (value == null)
            return null;

        DateTime date = TimeUtil.parseForEvent(value);
        if (date != null)
            return date;

        // Try parse it as long.
        try {
            long time = Long.valueOf(value);
            return new DateTime(time);
        } catch (NumberFormatException e) {
            // Do nothing.
        }

        return null;
    }

    public Event getEvent() {
        return event;
    }

    public List<EventTicket> getEventTickets() {
        return tickets;
    }
}
