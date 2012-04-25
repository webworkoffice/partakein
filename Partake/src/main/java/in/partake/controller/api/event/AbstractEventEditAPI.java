package in.partake.controller.api.event;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.model.dto.auxiliary.EventRelation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractEventEditAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    protected void updateEventFromParameter(UserEx user, Event event, JSONObject invalidParameters) {
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
        DateTime beginDate = getDateTimeParameter("beginDate");
        {
            if (beginDate == null)
                invalidParameters.put("beginDate", "開始日時は必ず入力してください。");
            else {
                Calendar beginCalendar = TimeUtil.calendar(beginDate.toDate());
                if (beginCalendar.get(Calendar.YEAR) < 2000 || 2100 < beginCalendar.get(Calendar.YEAR))
                    invalidParameters.put("beginDate", "開始日時の範囲が不正です。");
                event.setBeginDate(beginDate);
            }
        }

        // EndDate
        {
            DateTime endDate = getDateTimeParameter("endDate");
            if (endDate == null)
                event.setEndDate(null);
            else {
                Calendar endCalendar = TimeUtil.calendar(endDate.toDate());
                if (endCalendar.get(Calendar.YEAR) < 2000 || 2100 < endCalendar.get(Calendar.YEAR))
                    invalidParameters.put("endDate", "終了日時の範囲が不正です。");
                else if (beginDate != null && endDate.isBefore(beginDate))
                    invalidParameters.put("endDate", "終了日時が開始日時より前になっています。");
                else
                    event.setEndDate(endDate);
            }
        }

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
            if (description != null && 1000000 < description.length())
                invalidParameters.put("description", "説明は 1000000 文字以下で入力してください。");
            else
                event.setDescription(description);
        }

        {
            String hashTag = getParameter("hashTag");
            if (StringUtils.isBlank(hashTag))
                event.setHashTag(null);
            else if (100 < hashTag.length())
                invalidParameters.put("hashTag", "ハッシュタグは１００文字以内で記述してください。");
            else if (!Util.isValidHashtag(hashTag))
                invalidParameters.put("hashTag", "ハッシュタグは # から始まる英数字や日本語が指定できます。記号は使えません。");
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
            if (StringUtils.isBlank(foreImageId)) {
                event.setForeImageId(null);
            } else if (!Util.isUUID(foreImageId)) {
                invalidParameters.put("foreImageId", "画像IDが不正です。");
            } else {
                event.setForeImageId(foreImageId);
            }
        }

        {
            String backImageId = getParameter("backImageId");
            if (StringUtils.isBlank(backImageId)) {
                event.setBackImageId(null);
            } else if (!Util.isUUID(backImageId)) {
                invalidParameters.put("backImageId", "画像IDが不正です。");
            } else {
                event.setBackImageId(backImageId);
            }
        }
    }

    protected void updateTicketsFromParameter(UserEx user, List<EventTicket> tickets, JSONObject invalidParaemeters) {
        // TODO: If event was published, Ticket cannot be removed.

        String[] ticketIds = getParameters("ticketIds[]");
        String[] ticketTypes = getParameters("ticketTypes[]");
        String[] ticketNames = getParameters("ticketNames[]");
        String[] ticketAmounts = getParameters("ticketAmounts[]");
        String[] ticketAcceptsFrom = getParameters("ticketAcceptsFrom[]");
        String[] ticketAcceptsTill = getParameters("ticketAcceptsTill[]");

        throw new RuntimeException("Not implemented yet!");
    }

    protected void updateEventRelationFromParameter(UserEx user, List<EventRelation> relations, JSONObject invalidParameters) {
        // TOOD: このコードだと、relations は消されないんじゃない？

        String[] relatedEventIDs = getParameters("relatedEventID[]");
        if (relatedEventIDs == null)
            return;

        int size = relatedEventIDs.length;

        String[] relatedEventRequired = getParameters("relatedEventRequired[]");
        if (relatedEventRequired == null || relatedEventRequired.length != size)
            invalidParameters.put("relatedEvents", "関連イベントのパラメータに誤りがあります。");

        String[] relatedEventPriority = getParameters("relatedEventPriority[]");
        if (relatedEventPriority == null || relatedEventPriority.length != size)
            invalidParameters.put("relatedEvents", "関連イベントのパラメータに誤りがあります。");

        Set<String> visitedEventIds = new HashSet<String>();
        for (int i = 0; i < size; ++i) {
            if (StringUtils.isBlank(relatedEventIDs[i]))
                continue;

            if (!Util.isUUID(relatedEventIDs[i])) {
                invalidParameters.put("relatedEvents", "関連イベントのパラメータに誤りがあります。");
                break;
            }

            String dstEventId = relatedEventIDs[i];
            boolean required = Util.parseBooleanParameter(relatedEventRequired[i]);
            boolean priority = Util.parseBooleanParameter(relatedEventPriority[i]);

            if (visitedEventIds.contains(dstEventId)) {
                invalidParameters.put("relatedEvents", "関連イベントが重複しています。");
                break;
            }

            EventRelation relation = new EventRelation(dstEventId, required, priority);
            relations.add(relation);
            visitedEventIds.add(dstEventId);
        }
    }
}
