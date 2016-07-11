package de.hhu.toefl.parser.question;

import de.hhu.toefl.model.ToeflQuestion;

import java.io.IOException;
import java.util.List;

/**
 * Created by Esther on 2016-06-04.
 */
public interface QuestionParser {

    List<ToeflQuestion> getAllToeflQuestions() throws IOException;
}
