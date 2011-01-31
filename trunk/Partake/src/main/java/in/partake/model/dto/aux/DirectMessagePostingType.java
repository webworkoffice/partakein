package in.partake.model.dto.aux;

public enum DirectMessagePostingType {
    POSTING_TWITTER_DIRECT, // receiverId へ direct message を送る
    POSTING_TWITTER,        // senderId が message をつぶやく
}