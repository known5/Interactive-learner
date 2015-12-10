package objecten;

import java.util.HashMap;
import java.util.Map;

public class Features {

	public int observatieTotaalAantal;
    
    public Map<String, Map<String, Integer>> featureCategoryJointCount;
    
    public Map<String, Integer> categoryCounts;

    public Features() {
    	observatieTotaalAantal = 0;
        featureCategoryJointCount = new HashMap<>();
        categoryCounts = new HashMap<>();
    }
	
}
