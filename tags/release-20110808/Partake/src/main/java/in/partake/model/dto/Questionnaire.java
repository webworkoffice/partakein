package in.partake.model.dto;

import java.util.Comparator;

import in.partake.model.dto.auxiliary.QuestionnaireType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.jdbc.Index;

@Entity(name = "Questionnaires")
public class Questionnaire extends PartakeModel<Questionnaire> {
    @Id
    private String id;
    @Column @Index
    private String eventId;         // イベント
    @Column(length = 2048)
    private String question;        // 質問文
    @Column
    private int questionNo;         // 問題番号 (questionNo でソートされる) / 同じものが複数あれば未定義    
    @Column
    private QuestionnaireType type;
    @Column(length = 4096)
    private String answerTexts;     // 答えの列。

    public Questionnaire() {
    }

    public Questionnaire(String id, String eventId, String question, int questionNo, QuestionnaireType type, String answerTexts) {
        this.id = id;
        this.eventId = eventId;
        this.question = question;
        this.questionNo = questionNo;
        this.type = type;
        this.answerTexts = answerTexts;
    }

    public Questionnaire(Questionnaire q) {
        this(q.id, q.eventId, q.question, q.questionNo, q.type, q.answerTexts);
    }

    @Override
    public String getPrimaryKey() {
        return id;
    }

    @Override
    public Questionnaire copy() {
        return new Questionnaire(this);
    }

    // ----------------------------------------------------------------------

    @Override
    public int hashCode() {
        int code = 0;
        
        code = code * 37 + ObjectUtils.hashCode(id);
        code = code * 37 + ObjectUtils.hashCode(eventId);
        code = code * 37 + ObjectUtils.hashCode(question);
        code = code * 37 + ObjectUtils.hashCode(questionNo);
        code = code * 37 + ObjectUtils.hashCode(type);
        code = code * 37 + ObjectUtils.hashCode(answerTexts);
        
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Questionnaire)) { return false; }

        Questionnaire lhs = this;
        Questionnaire rhs = (Questionnaire) obj;

        if (!ObjectUtils.equals(lhs.id, rhs.id)) { return false; }
        if (!ObjectUtils.equals(lhs.eventId, rhs.eventId)) { return false; }
        if (!ObjectUtils.equals(lhs.question, rhs.question)) { return false; }
        if (!ObjectUtils.equals(lhs.questionNo, rhs.questionNo)) { return false; }
        if (!ObjectUtils.equals(lhs.type, rhs.type)) { return false; }
        if (!ObjectUtils.equals(lhs.answerTexts, rhs.answerTexts)) { return false; }

        return true;
    }

    // ----------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getQuestion() {
        return question;
    }

    public int getQuestionNo() {
        return questionNo;
    }

    public QuestionnaireType getType() {
        return type;
    }

    public String getAnswerTexts() {
        return answerTexts;
    }

    public void setId(String id) {
        checkFrozen();
        this.id = id;
    }

    public void setEventId(String eventId) {
        checkFrozen();
        this.eventId = eventId;
    }

    public void setQuestion(String question) {
        checkFrozen();
        this.question = question;
    }

    public void setQuestionNo(int questionNo) {
        checkFrozen();
        this.questionNo = questionNo;
    }

    public void setType(QuestionnaireType type) {
        checkFrozen();
        this.type = type;
    }

    public void setAnswerTexts(String answerTexts) {
        checkFrozen();
        this.answerTexts = answerTexts;
    }

    public static Comparator<Questionnaire> getComparatorQuestionNoAsc() {
        return new Comparator<Questionnaire>() {
            @Override
            public int compare(Questionnaire lhs, Questionnaire rhs) {
                if (lhs == rhs) { return 0; }
                if (lhs == null) { return -1; }
                if (rhs == null) { return 1; }
                return Integer.valueOf(lhs.getQuestionNo()).compareTo(rhs.getQuestionNo());
            }
        };
    }
}
