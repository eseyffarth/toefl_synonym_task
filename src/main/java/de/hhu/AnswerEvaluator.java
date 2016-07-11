package de.hhu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Esther on 2016-06-04.
 */
public class AnswerEvaluator {

    private List<String> correctAnswers;
    private List<Boolean> evaluationResults;

    public AnswerEvaluator(String filename) throws IOException {
        this.correctAnswers = readCorrectAnswers(filename);
    }

    private List<String> readCorrectAnswers(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List<String> correctAnswers = new ArrayList<>();

        // Process each line of the file that contains correct answers
        while ((line = br.readLine()) != null) {
            if (line.length() > 0) {
                String currentCorrectAnswer;
                String[] answerLine = line.split("\\s");
                currentCorrectAnswer = Arrays.asList(answerLine).get(answerLine.length - 1);
                correctAnswers.add(currentCorrectAnswer);
            }
        }
        return correctAnswers;
    }

    public void evaluate(List<String> givenAnswers) {
        evaluationResults = getEvaluationResults(givenAnswers);
    }

    private List<Boolean> getEvaluationResults(List<String> givenAnswers) {
        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < correctAnswers.size(); i++) {
            results.add(givenAnswers.get(i).equals(correctAnswers.get(i)));
        }
        return results;
    }

    public float getAccuracy() {
        long numberOfCorrectAnswers = evaluationResults.stream().filter(b -> b == true).count();

        return ((float) numberOfCorrectAnswers) / evaluationResults.size();
    }

}
