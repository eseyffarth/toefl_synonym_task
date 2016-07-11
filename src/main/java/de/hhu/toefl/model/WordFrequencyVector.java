package de.hhu.toefl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Esther on 2016-06-04.
 */
public class WordFrequencyVector {

    private String word;
    private Map<String, Double> cooccurences;

    public WordFrequencyVector(String word) {
        this.word = word;
        this.cooccurences = new HashMap<>();

    }

    public WordFrequencyVector(String word, Map<String, Double> cooccurences) {
        this.word = word;
        this.cooccurences = cooccurences;

    }

    public Map<String, Double> getCooccurences() {
        return this.cooccurences;
    }

    public void updateCooccurences(List<String> windowAroundWord) {

        for (String cooccurringWord : windowAroundWord) {
            if (!this.cooccurences.containsKey(cooccurringWord)) {
                this.cooccurences.put(cooccurringWord, 0.0);
            }
            this.cooccurences.put(cooccurringWord, this.cooccurences.get(cooccurringWord) + 1);
        }

    }

    public double cosine(WordFrequencyVector otherVector, Set<String> relevantDimensions) {
        double currentCosine;
        double denominator = 0;
        for (String dimension : relevantDimensions) {
            if (otherVector.getCooccurences().containsKey(dimension) &&
                    this.getCooccurences().containsKey(dimension)) {
                denominator = denominator + (otherVector.getCooccurences().get(dimension) *
                        this.getCooccurences().get(dimension));
            }
        }
        double numerator;
        double promptVectorFactorForNumerator = 0;
        double possibleChoiceVectorFactorForNumerator = 0;

        for (String dimension : relevantDimensions) {
            if (this.getCooccurences().containsKey(dimension)) {
                promptVectorFactorForNumerator += Math.pow(this.getCooccurences().get(dimension), 2);
            }
            if (otherVector.getCooccurences().containsKey(dimension)) {
                possibleChoiceVectorFactorForNumerator += Math.pow(otherVector.getCooccurences().get(dimension), 2);
            }

        }

        numerator = Math.sqrt(promptVectorFactorForNumerator) * Math.sqrt(possibleChoiceVectorFactorForNumerator);

        if (numerator > 0) {
            currentCosine = denominator / numerator;
        } else {
            currentCosine = 0;
        }

        return currentCosine;
    }

    // Vectors are initialized with absolute cooccurrence counts; change these values to Positive Pointwise Mutual Information Scores
    public WordFrequencyVector changeCountsToPPMIValues(Map<String, Integer> wordFrequencies, Integer corpusSize, Integer windowSize) {
        Integer totalNumberOfWordCooccurrences = (corpusSize - 2 * windowSize) * windowSize * 2;

        for (int i = windowSize; i < 2 * windowSize; i++) {
            totalNumberOfWordCooccurrences += 2 * i;
        }

        for (String cooccurringWord : cooccurences.keySet()) {
            double pmi = 0;

            double currentCooccurrenceProbability = cooccurences.get(cooccurringWord) / totalNumberOfWordCooccurrences;
            if (wordFrequencies.get(this.word) != 0 && wordFrequencies.get(cooccurringWord) != 0) {
                double denominator = ((wordFrequencies.get(this.word) / ((double) corpusSize)) * (wordFrequencies.get(cooccurringWord) / ((double) corpusSize)));
                pmi = Math.log(currentCooccurrenceProbability / denominator);
                if (pmi <= 0) {
                    pmi = 0;
                }
            }
            cooccurences.put(cooccurringWord, pmi);
        }
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WordFrequencyVector that = (WordFrequencyVector) o;

        if (!word.equals(that.word)) return false;
        return cooccurences.equals(that.cooccurences);

    }

    @Override
    public int hashCode() {
        int result = word.hashCode();
        result = 31 * result + cooccurences.hashCode();
        return result;
    }
}
