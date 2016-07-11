package de.hhu;

import de.hhu.toefl.model.ToeflQuestion;
import de.hhu.toefl.model.WordFrequencyVector;

import java.util.*;

/**
 * Created by Esther on 2016-06-04.
 */
public class ToeflSolver {

    private List<ToeflQuestion> allQuestions;

    public List<String> solveAllQuestions(List<ToeflQuestion> allQuestions, Map<String, WordFrequencyVector> allWordVectors) {
        this.allQuestions = allQuestions;

        List<String> answers = new ArrayList<>();

        for (ToeflQuestion question : allQuestions) {
            answers.add(solveQuestion(question, allWordVectors));
        }

        return answers;
    }

    private String solveQuestion(ToeflQuestion question, Map<String, WordFrequencyVector> allWordVectors) {
        WordFrequencyVector promptVector = allWordVectors.get(question.getPrompt());

        List<WordFrequencyVector> vectorsForChoices = new ArrayList<>();
        vectorsForChoices.add(allWordVectors.get(question.getChoices().get("a.")));
        vectorsForChoices.add(allWordVectors.get(question.getChoices().get("b.")));
        vectorsForChoices.add(allWordVectors.get(question.getChoices().get("c.")));
        vectorsForChoices.add(allWordVectors.get(question.getChoices().get("d.")));

        Set<String> relevantDimensions = new HashSet<>();
        relevantDimensions.addAll(promptVector.getCooccurences().keySet());
        for (WordFrequencyVector possibleChoiceVector : vectorsForChoices) {
            Map<String, Double> possibleChoiceWordVectorDimensions = possibleChoiceVector.getCooccurences();
            if (possibleChoiceWordVectorDimensions.size() > 0) {
                relevantDimensions.addAll(possibleChoiceWordVectorDimensions.keySet());
            }
        }

        double bestCosine = -2;
        String bestAnswer = "X";

        List<String> answerKeys = Arrays.asList("a", "b", "c", "d");

        for (int i = 0; i < vectorsForChoices.size(); i++) {
            WordFrequencyVector possibleChoiceVector = vectorsForChoices.get(i);
            double currentCosine = promptVector.cosine(possibleChoiceVector, relevantDimensions);
            if (currentCosine > bestCosine) {
                bestCosine = currentCosine;
                bestAnswer = answerKeys.get(i);
            }
        }
        return bestAnswer;
    }
}
