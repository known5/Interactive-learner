package examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import objecten.TrainingData;

public class MaleFemale {

	
	
	public static String[] readLines(URL url) throws IOException {
		
		Reader fileReader = new InputStreamReader(url.openStream(), Charset.forName("UTF-8"));
		List<String> lines;
		try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			lines = new ArrayList<>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		}
		return lines.toArray(new String[lines.size()]);
	}
	
	
	
	public static void main(String[] args) throws IOException {
		
		Map<String, URL> trainingFiles = new HashMap<>();
		trainingFiles.put("Male", MaleFemale.class.getResource("/files/blogs/M/train")  );
		trainingFiles.put("Female",MaleFemale.class.getResource("files/blogs/F/train") );
		
		Map<String, String[]> trainingExamples = new HashMap<>();
		for (Map.Entry<String, URL> entry : trainingFiles.entrySet()) {
			trainingExamples.put(entry.getKey(), readLines(entry.getValue()));
		}
		
		Classifier maleFemale = new Classifier();
		maleFemale.TrainClassifier(trainingExamples, null);
		
		TrainingData dataSet = maleFemale.getTrainingData();
		
		maleFemale = null;
		trainingExamples = null;
		
		maleFemale = new Classifier(dataSet);
		String outputMale = maleFemale.Classify("Yo");
		print(outputMale);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	public static void print(String text) {
			if(text != null) {
				System.out.println(text);
			} else {
				System.out.println("have to give a input value");
			}
		}
	
	
}
