package de.hhu.toefl.model;

import java.util.Map;

/**
 * Created by Esther on 2016-06-04.
 */
public class ToeflQuestion {

    private String prompt;
    private Map<String, String> choices;

    public ToeflQuestion(String prompt, Map<String, String> choices) {
        this.prompt = prompt;
        this.choices = choices;
    }

    public String getPrompt() {
        return prompt;
    }

    public Map<String, String> getChoices() {
        return choices;
    }
}
