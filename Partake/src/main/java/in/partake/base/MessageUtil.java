package in.partake.base;

import in.partake.model.EventEx;
import in.partake.model.UserEx;

public class MessageUtil {
    public static final int MESSAGE_MAX_CODEPOINTS = 140;
    public static final int MINIMUM_LENGTH_OF_TITLE = 10;
    private static final String MESSAGE_HEADER = "[PARTAKE] 「";
    private static final String MESSAGE_DESCRIPTOR = "」 %s の管理者(@%s)よりメッセージ：";

    /**
     * ダイレクトメッセージの文章を構築する。
     * 文章のコードポイント数は{@link #MESSAGE_MAX_CODEPOINTS}以下であることが保証される。
     *
     * @param sender メッセージ送信者（基本的にイベント管理者）
     * @param shortenedURL イベントの短縮URL
     * @param eventTitle イベントのタイトル
     * @param userInput メッセージの内容
     * @return ダイレクトメッセージの文章
     * @throws NullPointerException 引数のいずれか1つ以上がnullだった場合
     * @throws TooLongMessageException 文章のコードポイント数が{@link #MESSAGE_MAX_CODEPOINTS}より大きくなる場合
     */
    public static String buildMessage(UserEx sender, String shortenedURL, String eventTitle, String userInput) throws NullPointerException, TooLongMessageException {
        String messageFormat = MESSAGE_HEADER + "%s" + String.format(MESSAGE_DESCRIPTOR, shortenedURL, sender.getScreenName()) + userInput;
        int restCodePoints = MESSAGE_MAX_CODEPOINTS - Util.codePointCount(messageFormat) + "%s".length();
        if (restCodePoints < Math.min(Util.codePointCount(eventTitle), MINIMUM_LENGTH_OF_TITLE)) {
            throw new TooLongMessageException(MESSAGE_MAX_CODEPOINTS + Math.min(Util.codePointCount(eventTitle), MINIMUM_LENGTH_OF_TITLE) - restCodePoints);
        }
        String message = String.format(messageFormat, Util.shorten(eventTitle, restCodePoints));
        int finallyCodePoint = Util.codePointCount(message);
        if (finallyCodePoint > MESSAGE_MAX_CODEPOINTS) {
            throw new TooLongMessageException(finallyCodePoint);
        }
        return message;
    }

    /**
     * 何文字までメッセージを入れられるか数える。
     *
     * @param sender メッセージ送信者（基本的にイベント管理者）
     * @param event メッセージに関連するイベント
     * @return 何文字までメッセージ本文を入力できるか（入力できない場合は負の値を返す）
     * @throws NullPointerException 引数のいずれか1つ以上がnullだった場合
     */
    public static int calcRestCodePoints(UserEx sender, EventEx event) throws NullPointerException {
        assert sender != null;
        assert event != null;

        try {
            return MESSAGE_MAX_CODEPOINTS - Util.codePointCount(buildMessage(sender, event.getShortenedURL(), event.getTitle(), ""));
        } catch (TooLongMessageException e) {
            return 0;
        }
    }

    public static final class TooLongMessageException extends Exception {
        private static final long serialVersionUID = -2154591724564636569L;
        private final int codePoints;

        public TooLongMessageException(int codePoints) {
            super("too long message(" + codePoints + " code points)");
            this.codePoints = codePoints;
        }

        public int getCodePoints() {
            return this.codePoints;
        }
    }

}
