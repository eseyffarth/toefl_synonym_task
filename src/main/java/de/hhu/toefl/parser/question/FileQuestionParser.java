package de.hhu.toefl.parser.question;

import de.hhu.toefl.model.ToeflQuestion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Esther on 2016-06-04.
 */
public class FileQuestionParser implements QuestionParser {

    private File file;

    public FileQuestionParser(File file) {
        this.file = file;
    }

    public List<ToeflQuestion> getAllToeflQuestions() throws IOException {
        List<ToeflQuestion> output = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(this.file));
        String line;

        String prompt = "";
        Map<String, String> choices = new HashMap<>();

        // Process each line of the file
        while ((line = br.readLine()) != null) {


            if (!line.equals("")) {
                // current block is processed into one ToeflQuestion instance
                if (line.matches("^[0-9].*$")) {
                    // get the question word
                    String[] title = line.split("\\s");
                    prompt = Arrays.asList(title).get(title.length - 1);
                } else {
                    String[] nextChoice = line.split("\\s");
                    String nextChoiceIndex = Arrays.asList(nextChoice).get(0);
                    String nextChoiceWord = Arrays.asList(nextChoice).get(nextChoice.length - 1);
                    choices.put(nextChoiceIndex, nextChoiceWord);
                }
            } else {
                // create one ToeflQuestion instance
                ToeflQuestion nextToeflQuestion = new ToeflQuestion(prompt, choices);
                output.add(nextToeflQuestion);
                prompt = "";
                choices = new HashMap<>();
            }
        }

        if (!prompt.equals("") && choices.size() > 0) {
            ToeflQuestion nextToeflQuestion = new ToeflQuestion(prompt, choices);
            output.add(nextToeflQuestion);
        }
        return output;
    }
}
