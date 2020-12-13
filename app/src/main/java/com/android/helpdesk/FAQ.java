package com.android.helpdesk;

public class FAQ {
    private int idFAQ;
    private String question, answer;

    public FAQ(int idFAQ, String question, String answer) {
        this.idFAQ = idFAQ;
        this.question = question;
        this.answer = answer;
    }

    public int getIdFAQ() {
        return idFAQ;
    }

    public void setIdFAQ(int idFAQ) {
        this.idFAQ = idFAQ;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
