package de.hhu;

import de.hhu.toefl.model.ToeflQuestion;
import de.hhu.toefl.model.WordFrequencyVector;
import de.hhu.toefl.parser.corpus.FileCorpusParser;
import de.hhu.toefl.parser.question.FileQuestionParser;
import de.hhu.toefl.parser.question.QuestionParser;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Esther Seyffarth on 2016-06-04.
 * 
 * Project to automatically solve the TOEFL synonymity test using distributional semantics.
 * For more info on the task, see http://aclweb.org/aclwiki/index.php?title=TOEFL_Synonym_Questions_(State_of_the_art)
 */
public class Main {

    public static void main(String[] args) {
        QuestionParser parser = new FileQuestionParser(new File("./toefl.qst"));

        // specifying n-gram size
        final Integer WINDOWSIZEONEACHSIDE = 5;

        // specify whether to turn all words into lowercase
        final boolean ONLYLOWERCASE = false;


        try {
            List<ToeflQuestion> allToeflQuestions = parser.getAllToeflQuestions();
            System.out.println("Collected all known TOEFL questions and their possible answers!");

            Set<String> relevantWords = new HashSet<>();
            for (ToeflQuestion question : allToeflQuestions) {
                relevantWords.add(question.getPrompt());
                for (String choice : question.getChoices().values()) {
                    relevantWords.add(choice);
                }

            }
            System.out.println("Relevant words: ".concat(relevantWords.toString()));


            Map<String, WordFrequencyVector> allWordVectors = new HashMap<>();


            System.out.println("Now trying to parse corpus...");

            FileCorpusParser parserForTraining = new FileCorpusParser("./ukwac_subset_10M_untagged.txt");
            try {
                allWordVectors = parserForTraining.createWordVectorsFromCorpusContent(relevantWords, WINDOWSIZEONEACHSIDE, ONLYLOWERCASE);

            } catch (IOException e) {
                System.out.println("Error while reading training corpus.");
            }


            // solving all TOEFL questions, using the previously generated vectors
            ToeflSolver toeflSolver = new ToeflSolver();
            List<String> mySolutions = toeflSolver.solveAllQuestions(allToeflQuestions, allWordVectors);

            // evaluate my answers and output the results, along with the active settings
            AnswerEvaluator answerEvaluator = new AnswerEvaluator("./toefl.ans");
            answerEvaluator.evaluate(mySolutions);

            System.out.println("####################");
            System.out.println("Settings:");
            System.out.println("N-gram size:\t\t\t" + WINDOWSIZEONEACHSIDE);
            System.out.println("Case normalization:\t\t" + ONLYLOWERCASE);
            System.out.println("Accuracy:\t\t\t\t" + answerEvaluator.getAccuracy());


        } catch (IOException e) {
            System.out.println("Could not open file with Toefl questions.");
        }


    }

}
