package classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import features.FeatureExtraction;
import features.TextTokenizer;
import objecten.Document;
import objecten.Features;
import objecten.TrainingData;

public class Classifier {

	private double chisquareCriticalValue = 10.83;

	private TrainingData trainingData;

	public Classifier(TrainingData trainingDataArg) {
		this.trainingData = trainingDataArg;
	}

	public Classifier() {
		this(null);
	}

	public TrainingData getTrainingData() {
		return trainingData;
	}

	public double getChisquareCriticalValue() {
		return chisquareCriticalValue;
	}

	public void setChisquareCriticalValue(double chisquareCriticalValue) {
		this.chisquareCriticalValue = chisquareCriticalValue;
	}

	public List<Document> preprocessDataset(
			Map<String, String[]> trainingDataset) {
		List<Document> dataset = new ArrayList<>();
		String categorie;
		String[] voorbeelden;
		Document document;

		Iterator<Map.Entry<String, String[]>> iterator = trainingDataset
				.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, String[]> entry = iterator.next();
			categorie = entry.getKey();
			voorbeelden = entry.getValue();

			for (int i = 0; i < voorbeelden.length; i++) {
				document = TextTokenizer.tokenize(voorbeelden[i]);
				document.categorie = categorie;
				dataset.add(document);
			}

		}

		return dataset;

	}

	private Features selectFeatures(List<Document> dataset) {
		FeatureExtraction featureExtractor = new FeatureExtraction();
		Features stats = featureExtractor.extractFeatureStats(dataset);
		Map<String, Double> selectedFeatures = featureExtractor.chisquare(
				stats, chisquareCriticalValue);

		Iterator<Map.Entry<String, Map<String, Integer>>> it = stats.featureCategoryJointCount
				.entrySet().iterator();
		while (it.hasNext()) {
			String feature = it.next().getKey();
			if (selectedFeatures.containsKey(feature) == false) {
				it.remove();
			}
		}

		return stats;
	}

	public void TrainClassifier(Map<String, String[]> trainingDataset,
			Map<String, Double> categoryPriors) throws IllegalArgumentException {
		List<Document> dataset = preprocessDataset(trainingDataset);
		Features featureStats = selectFeatures(dataset);
		trainingData = new TrainingData();
		trainingData.observatieAantal = featureStats.observatieTotaalAantal;
		trainingData.featureAantal = featureStats.featureCategoryJointCount
				.size();
		if (categoryPriors == null) {

			trainingData.categorieAantal = featureStats.categoryCounts.size();
			trainingData.logGegeven = new HashMap<>();

			String category;
			int count;
			for (Map.Entry<String, Integer> entry : featureStats.categoryCounts
					.entrySet()) {
				category = entry.getKey();
				count = entry.getValue();

				trainingData.logGegeven.put(category, Math.log((double) count
						/ trainingData.observatieAantal));
			}
		} else {

			trainingData.categorieAantal = categoryPriors.size();

			if (trainingData.categorieAantal != featureStats.categoryCounts
					.size()) {
				throw new IllegalArgumentException(
						"Invalid priors Array: Make sure you pass a prior probability for every supported category.");
			}

			String category;
			Double priorProbability;
			for (Map.Entry<String, Double> entry : categoryPriors.entrySet()) {
				category = entry.getKey();
				priorProbability = entry.getValue();
				if (priorProbability == null) {
					throw new IllegalArgumentException(
							"Invalid priors Array: Make sure you pass a prior probability for every supported category.");
				} else if (priorProbability < 0 || priorProbability > 1) {
					throw new IllegalArgumentException(
							"Invalid priors Array: Prior probabilities should be between 0 and 1.");
				}

				trainingData.logGegeven.put(category,
						Math.log(priorProbability));
			}
		}

		Map<String, Double> featureOccurrencesInCategory = new HashMap<>();

		Integer occurrences;
		Double featureOccSum;
		for (String category : trainingData.logGegeven.keySet()) {
			featureOccSum = 0.0;
			for (Map<String, Integer> categoryListOccurrences : featureStats.featureCategoryJointCount
					.values()) {
				occurrences = categoryListOccurrences.get(category);
				if (occurrences != null) {
					featureOccSum += occurrences;
				}
			}
			featureOccurrencesInCategory.put(category, featureOccSum);
		}

		String feature;
		Integer count;
		Map<String, Integer> featureCategoryCounts;
		double logLikelihood;
		for (String category : trainingData.logGegeven.keySet()) {
			for (Map.Entry<String, Map<String, Integer>> entry : featureStats.featureCategoryJointCount
					.entrySet()) {
				feature = entry.getKey();
				featureCategoryCounts = entry.getValue();
				count = featureCategoryCounts.get(category);
				if (count == null) {
					count = 0;
				}

				logLikelihood = Math
						.log((count + 1.0)
								/ (featureOccurrencesInCategory.get(category) + trainingData.featureAantal));
				if (trainingData.logWaarschijnlijkheden.containsKey(feature) == false) {
					trainingData.logWaarschijnlijkheden.put(feature,
							new HashMap<String, Double>());
				}
				trainingData.logWaarschijnlijkheden.get(feature).put(category,
						logLikelihood);
			}
		}
		featureOccurrencesInCategory = null;

	}

	public String Classify(String Arg) throws IllegalArgumentException {
		Document doc = TextTokenizer.tokenize(Arg);
		String category;
		String feature;
		Integer occurrences;
		Double logprob;
		String maxScoreCategory = null;
		Double maxScore = Double.NEGATIVE_INFINITY;
		if (trainingData == null) {
			throw new IllegalArgumentException(
					"TrainingData missing: Make sure you train first");
		}

		for (Map.Entry<String, Double> entry1 : trainingData.logGegeven
				.entrySet()) {
			category = entry1.getKey();
			logprob = entry1.getValue();

			for (Map.Entry<String, Integer> entry2 : doc.tokens.entrySet()) {
				feature = entry2.getKey();

				if (!trainingData.logWaarschijnlijkheden.containsKey(feature)) {
					continue;
				}

				occurrences = entry2.getValue();
				logprob += occurrences
						* trainingData.logWaarschijnlijkheden.get(feature).get(
								category);
			}

			if (logprob > maxScore) {
				maxScore = logprob;
				maxScoreCategory = category;
			}
		}

		return maxScoreCategory;
	}

}
