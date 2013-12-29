import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;


public class Parser {

	static  Parser instance;
	private Scanner scanner;
	private Symbol<String> currentToken;
	private Map<String,Symbol<?>> tableOfSymbols;
	
	private static final String FILE_TEST_PATH = "test.txt";
	public Parser() throws IOException{
		
		InputStream scannerStream = null;
	    // open input stream test.txt for reading purpose.
		scannerStream = new FileInputStream(FILE_TEST_PATH);
		//load grammar
		Grammar.getInstance().loadGrammar();
		
        //print the grammar
		for (Map.Entry<String, ArrayList<String>> entry : Grammar.getInstance().entrySet()) {
			String key = entry.getKey();
		    ArrayList<String> value = entry.getValue();
		    for(String aString : value){
		        System.out.println("key : " + key + " value : " + aString);
		    }
		}
		
		this.scanner = new Scanner(scannerStream);
		this.tableOfSymbols = scanner.getTableOfSymbols();
		Symbol lexicalUnit; 
		do{
			lexicalUnit = scanner.next_token();
			if(lexicalUnit != null){
				System.out.println("token: "+lexicalUnit.getValue() + " \tlexical unit: " + lexicalUnit.unit.toString());
			}
		}while(lexicalUnit == null || !lexicalUnit.unit.equals(LexicalUnit.EOF));
	}
	
	public static Parser parse() throws IOException {
		if (instance == null) {

			instance = new Parser();
		}

		return instance;

	}
	
}
