package in.partake.model.dto;

import in.partake.model.dto.auxiliary.QuestionnaireType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.openjpa.persistence.jdbc.Index;

@Entity
public class Questionnaire extends PartakeModel<Questionnaire> {
    @Id
    private String id;              
    @Column @Index
    private String eventId;         // イベント
    @Column
    private String question;        // 質問文
    @Column
    private int questionNo;         // 問題番号 (questionNo でソートされる)
    @Column
    private QuestionnaireType type;
    @Column(length = 2048)
    private String answerTexts;     // 答えの列 TODO: どうやって保持しようかな / CSV 形式あたりで保持する？
    
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
}
