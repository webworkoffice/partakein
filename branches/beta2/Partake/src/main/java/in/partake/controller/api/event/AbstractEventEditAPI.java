package in.partake.controller.api.event;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.EventRelation;
import in.partake.model.dto.auxiliary.TicketType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractEventEditAPI extends AbstractPartakeAPI {
    private static final long serialVersionUID = 1L;

    protected void updateTicketsFromParameter(UserEx user, Event event, List<EventTicket> tickets, JSONObject invalidParameters) {
        String[] ticketId = getParameters("ticketIds[]");
        String[] ticketName = getParameters("ticketName[]");
        String[] ticketApplicationStart = getParameters("ticketApplicationStart[]");
        String[] ticketApplicationStartDay = getParameters("ticketApplicationStartDay[]");
        String[] ticketApplicationEnd = getParameters("ticketApplicationEnd[]");
        String[] ticketApplicationEndDay = getParameters("ticketApplicationEndDay[]");
        String[] ticketPrice = getParameters("ticketPrice[]");
        // String[] ticketPriceText = getParameters("ticketPriceText[]");
        String[] ticketAmount = getParameters("ticketAmount[]");
        String[] ticketAmountText = getParameters("ticketAmountText[]");

        // These parameters should have the same length.
        int N = ticketId != null ? ticketId.length : 0;
        try {
            for (int i = 0; i < N; ++i) {
                String name = ticketName[i];
                if (StringUtils.isBlank(name))
                    invalidParameters.put("ticketName[" + i + "]", "チケット名が空白です。");
                if (Util.codePointCount(name) > 10)
                    invalidParameters.put("ticketName[" + i + "]", "チケット名は 10 文字以下でなければなりません。");

                TicketType ticketType = null;
                if ("free".equals(ticketPrice[i]))
                    ticketType = TicketType.FREE_TICKET;
                else if ("nonFree".equals(ticketPrice[i]))
                    ticketType = TicketType.NONFREE_TICKET;
                if (ticketType == null)
                    invalidParameters.put("ticketType[" + i + "]", "チケットタイプが不正です。");

                int amount = -1;
                if ("unlimited".equals(ticketAmount[i]))
                    amount = 0;
                else if ("limited".equals(ticketAmount[i])) {
                    try {
                        amount = Integer.parseInt(ticketAmountText[i]);
                    } catch (NumberFormatException e) {
                        amount = -1;
                    }
                }
                if (amount < 0)
                    invalidParameters.put("ticketAmountText[" + i + "]", "チケット数が不正です。");

                DateTime acceptsFrom = null;
                if ("fromNow".equals(ticketApplicationStart[i]))
                    acceptsFrom = TimeUtil.getCurrentDateTime();
                else if ("beforeNDays".equals(ticketApplicationStart[i])) {
                    int n = -1;
                    try {
                        n = Integer.parseInt(ticketApplicationStartDay[i]);
                    } catch (NumberFormatException e) {
                    }
                    if (n < 0 || 366 < n)
                        invalidParameters.put("ticketApplicationStart[" + i + "]", "チケットの募集期間が不正です");
                    acceptsFrom = new DateTime(event.getBeginDate().getTime() - n * 24 * 3600 * 1000);
                } else {
                    invalidParameters.put("ticketApplicationStart[" + i + "]", "チケットの募集期間が不正です。まだカスタム実装してないよ！");
                    throw new RuntimeException("Not implemented yet");
                }

                DateTime acceptsTill = null;
                if ("justBeforeEvent".equals(ticketApplicationEnd[i])) {
                    acceptsTill = event.getBeginDate();
                } else if ("justAfterEvent".equals(ticketApplicationEnd[i])) {
                    if (event.getEndDate() != null)
                        acceptsTill = event.getEndDate();
                    else
                        acceptsTill = event.getBeginDate();
                } else if ("beforeNDays".equals(ticketApplicationEnd[i])) {
                    int n = -1;
                    try {
                        n = Integer.parseInt(ticketApplicationEndDay[i]);
                    } catch (NumberFormatException e) {
                    }
                    if (n < 0 || 366 < n)
                        invalidParameters.put("ticketApplicationEnd[" + i + "]", "チケットの募集期間が不正です");
                    acceptsTill = new DateTime(event.getBeginDate().getTime() - n * 24 * 3600 * 1000);
                } else {
                    invalidParameters.put("ticketApplicationEnd[" + i + "]", "チケットの募集期間が不正です。まだカスタム実装してないよ！");
                    throw new RuntimeException("Not implemented yet");
                }

                assert acceptsFrom != null;
                assert acceptsTill != null;

                if (acceptsTill.isBefore(acceptsFrom))
                    invalidParameters.put("ticketApplicationEnd[" + i + "]", "チケットの募集期間が不正です。");

                EventTicket ticket = new EventTicket(null, null, name, ticketType, amount, acceptsFrom, acceptsTill, TimeUtil.getCurrentDateTime(), null);
                tickets.add(ticket);
            }
        } catch (IndexOutOfBoundsException e) {
            invalidParameters.put("tickets", "チケット関連のパラメータが不正です。");
        }
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
