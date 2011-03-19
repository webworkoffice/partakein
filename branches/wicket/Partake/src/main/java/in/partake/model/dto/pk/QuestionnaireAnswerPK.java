package in.partake.model.dto.pk;

import org.apache.commons.lang.ObjectUtils;

public class QuestionnaireAnswerPK {
    private String questionnaireId;
    private String userId;
    
    public QuestionnaireAnswerPK() {
        
    }

    public QuestionnaireAnswerPK(String questionnaireId, String userId) {
        this.questionnaireId = questionnaireId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QuestionnaireAnswerPK)) { return false; }
        
        QuestionnaireAnswerPK lhs = this;
        QuestionnaireAnswerPK rhs = (QuestionnaireAnswerPK) obj;
        
        if (!ObjectUtils.equals(lhs.questionnaireId, rhs.questionnaireId)) { return false; }
        if (!ObjectUtils.equals(lhs.userId, rhs.userId)) { return false; }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        
        code += code * 37 + ObjectUtils.hashCode(questionnaireId);
        code += code * 37 + ObjectUtils.hashCode(userId);
        
        return code;
    }
    
    
    public String getQuestionnaireId() {
        return questionnaireId;
    }

    public String getUserId() {
        return userId;
    }
}
