package in.partake.model.dto;

import in.partake.model.dto.pk.QuestionnaireAnswerPK;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(QuestionnaireAnswerPK.class)
public class QuestionnaireAnswer extends PartakeModel<QuestionnaireAnswer> {
    @Id
    private String questionnaireId;
    @Id
    private String userId;
    @Column
    private String answer;
    
    public QuestionnaireAnswer() {
        
    }
    
    public QuestionnaireAnswer(String questionnaireId, String userId, String answer) {
        this.questionnaireId = questionnaireId;
        this.userId = userId;
        this.answer = answer;
    }
    
    
    public QuestionnaireAnswer(QuestionnaireAnswer q) {
        this(q.questionnaireId, q.userId, q.answer);
    }
    
    @Override
    public QuestionnaireAnswer copy() {
        return new QuestionnaireAnswer(this);
    }
    
    @Override
    public Object getPrimaryKey() {
        return new QuestionnaireAnswerPK(questionnaireId, userId);
    }
    
    // ----------------------------------------------------------------------
    
    public String getQuestionnaireId() {
        return questionnaireId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setQuestionnaireId(String questionnaireId) {
        checkFrozen();
        this.questionnaireId = questionnaireId;
    }
    
    public void setUserId(String userId) {
        checkFrozen();
        this.userId = userId;
    }
    
    public void setAnswer(String answer) {
        checkFrozen();
        this.answer = answer;
    }
    
}
