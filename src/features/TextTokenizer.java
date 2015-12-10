package features;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import objecten.Document;

public class TextTokenizer {

	 public static String preprocess(String text) {
	        return text.replaceAll("\\p{P}", " ").replaceAll("\\s+", " ").toLowerCase(Locale.getDefault());
	    }
	
	 public static String[] extractKeywords(String text) {
	        return text.split(" ");
	    }
	
	 public static Map<String, Integer> getKeywordCounts(String[] keywordArray) {
	        Map<String, Integer> counts = new HashMap<>();
	        
	        Integer counter;
	        for(int i=0;i<keywordArray.length;++i) {
	            counter = counts.get(keywordArray[i]);
	            if(counter==null) {
	                counter=0;
	            }
	            counts.put(keywordArray[i], ++counter); //increase counter for the keyword
	        }
	        
	        return counts;
	    }
	 
	 public static Document tokenize(String text) {
	        String preprocessedText = preprocess(text);
	        String[] keywordArray = extractKeywords(preprocessedText);
	        
	        Document doc = new Document();
	        doc.tokens = getKeywordCounts(keywordArray);
	        return doc;
	    }
}
