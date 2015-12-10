package objecten;

import java.util.HashMap;
import java.util.Map;

public class TrainingData {

	public int observatieAantal = 0;
	
	public int categorieAantal = 0;
	
	public int featureAantal = 0;
	
	public Map<String, Double> logGegeven = new HashMap<>();
	
	public Map<String, Map<String, Double>> logWaarschijnlijkheden = new HashMap<>();
	
	
}
