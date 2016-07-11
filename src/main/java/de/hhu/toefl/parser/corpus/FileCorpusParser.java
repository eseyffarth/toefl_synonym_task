package de.hhu.toefl.parser.corpus;

import de.hhu.toefl.model.WordFrequencyVector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Esther on 2016-06-04.
 */
public class FileCorpusParser implements CorpusParser {

    private String corpusPath;

    public FileCorpusParser(String corpusPath) {
        this.corpusPath = corpusPath;
    }

    @Override
    public Map<String, WordFrequencyVector> createWordVectorsFromCorpusContent(Set<String> toeflWords, Integer windowSize, boolean normalizeCase) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(this.corpusPath));
        String line;
        Map<String, WordFrequencyVector> allWordVectors = new HashMap<>();
        List<String> slidingWindow = new ArrayList<>();
        Map<String, Integer> wordFrequencies = new HashMap<>();
        Integer totalCorpusSize = 0;

        // Process each line of the file
        while ((line = br.readLine()) != null) {

            if (!line.startsWith("CURRENT URL")) {

                // Tokenization uses Java's naive StringTokenizer, splitting the input sequence at whitespaces
                StringTokenizer st = new StringTokenizer(line);


                // Process next token from the current line
                while (st.hasMoreElements()) {

                    String nextToken = st.nextToken();
                    totalCorpusSize += 1;
                    if (normalizeCase) {
                        nextToken = nextToken.toLowerCase();
                    }

                    if (!wordFrequencies.containsKey(nextToken)) {
                        wordFrequencies.put(nextToken, 0);
                    }
                    wordFrequencies.put(nextToken, wordFrequencies.get(nextToken) + 1);

                    if (slidingWindow.size() < (2 * windowSize) + 1) {
                        slidingWindow.add(nextToken);
                    } else {
                        String wordForVector = slidingWindow.get(windowSize);
                        if (toeflWords.contains(wordForVector)) {
                            WordFrequencyVector vectorForCurrentWord;
                            if (!allWordVectors.keySet().contains(wordForVector)) {
                                vectorForCurrentWord = new WordFrequencyVector(wordForVector);
                            } else {
                                vectorForCurrentWord = allWordVectors.get(wordForVector);
                            }

                            vectorForCurrentWord.updateCooccurences(slidingWindow);
                            allWordVectors.put(wordForVector, vectorForCurrentWord);
                        }

                        // add next token and remove first element of sliding window
                        slidingWindow.add(nextToken);
                        slidingWindow.remove(0);
                    }

                }

            }


        }

        // process the remaining slidingWindow after the corpus is empty
        for (int i = 0; i < slidingWindow.size() - windowSize; i++) {
            String wordForVector = slidingWindow.get(i + windowSize);

            if (toeflWords.contains(wordForVector)) {
                WordFrequencyVector vectorForCurrentWord;
                if (!allWordVectors.keySet().contains(wordForVector)) {
                    vectorForCurrentWord = new WordFrequencyVector(wordForVector);
                } else {
                    vectorForCurrentWord = allWordVectors.get(wordForVector);
                }

                vectorForCurrentWord.updateCooccurences(slidingWindow.subList(i - windowSize, slidingWindow.size()));
                allWordVectors.put(wordForVector, vectorForCurrentWord);
            }

        }


        // some toefl words may not occur in the corpus. create vectors with empty cooccurrence maps for these words
        for (String toeflWord : toeflWords) {
            if (!allWordVectors.containsKey(toeflWord)) {
                WordFrequencyVector placeholderVector = new WordFrequencyVector(toeflWord);
                allWordVectors.put(toeflWord, placeholderVector);
            }
        }


        // change all values in all vectors to their PPMI values
        for (WordFrequencyVector wordFrequencyVector : allWordVectors.values()) {
            wordFrequencyVector.changeCountsToPPMIValues(wordFrequencies, totalCorpusSize, windowSize);
        }


        // write vector content to file for introspection
        for (String toeflWord : allWordVectors.keySet()) {
            FileWriter toeflWordVectorFileWriter = new FileWriter("./wordvectors/" + toeflWord + ".vec");

            WordFrequencyVector toeflVector = allWordVectors.get(toeflWord);
            Map<String, Double> toeflCooccurrences = toeflVector.getCooccurences();

            for (String cooccuringWord : toeflCooccurrences.keySet()) {
                toeflWordVectorFileWriter.write(cooccuringWord
                        + "\t"
                        + toeflCooccurrences.get(cooccuringWord).toString()
                        + "\n");
            }
        }


        return allWordVectors;
    }


}
