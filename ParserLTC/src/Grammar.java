import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public class Grammar extends LinkedHashMap<String, ArrayList<String>> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Grammar instance;
	private static final String FILE_GRAMMAR_PATH = "grammar.txt";

	public static Grammar getInstance() {
		if (instance == null) {

			instance = new Grammar();
		}

		return instance;

	}

	public void loadGrammar() throws FileNotFoundException {

		String[] splitted;
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				FILE_GRAMMAR_PATH));
		String line;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				// process the line.
				splitted = line.split("->", 2);
				addToGrammar(splitted[0].trim(), splitted[1].trim());

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private synchronized void addToGrammar(String mapKey, String ruleValue) {
		
	    ArrayList<String> rulesList = this.get(mapKey);

	    // if list does not exist create it
	    if(rulesList == null) {
	    	rulesList = new ArrayList<String>();
	    	rulesList.add(ruleValue);
	    	this.put(mapKey, rulesList);
	    } else {
	        // add if item is not already in list
	        if(!rulesList.contains(ruleValue)) rulesList.add(ruleValue);
	    }
	}
}
