package in.partake.model.dto.auxiliary;

// TODO: should be renamed to MessagePostingType.
public enum DirectMessagePostingType {
    POSTING_TWITTER_DIRECT, // receiverId へ direct message を送る
    POSTING_TWITTER,        // senderId が message をつぶやく
}
