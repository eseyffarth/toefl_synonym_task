package de.hhu.toefl.parser.corpus;

import de.hhu.toefl.model.WordFrequencyVector;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Esther on 2016-06-04.
 */
public interface CorpusParser {

    public Map<String, WordFrequencyVector> createWordVectorsFromCorpusContent(Set<String> toeflWords, Integer windowSize, boolean normalizeCase) throws IOException;
}
