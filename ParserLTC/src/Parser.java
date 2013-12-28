import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class Parser {

	static Parser instance;
	private Scanner scanner;
	
	
	private Symbol<String> currentToken;
	private static final String FILE_PATH = "test.txt";
	private Map<String,Symbol<?>> tableOfSymbols;
	
	
	public static Parser getInstance() {
		if (instance == null) {

			instance = new Parser();
		}

		return instance;

	}
	
	public void Start() throws IOException{
		
		InputStream scannerStream = null;
	    // open input stream test.txt for reading purpose.
		scannerStream = new FileInputStream(FILE_PATH);
		
		this.scanner = new Scanner(scannerStream);
		this.tableOfSymbols = scanner.getTableOfSymbols();
		Symbol lexicalUnit; 
	    checkGrammar();
		do{
			lexicalUnit = scanner.next_token();
			if(lexicalUnit != null){
				System.out.println("token: "+lexicalUnit.getValue() + " \tlexical unit: " + lexicalUnit.unit.toString());
			}
		}while(lexicalUnit == null || !lexicalUnit.unit.equals(LexicalUnit.EOF));
	}
	
	private void checkGrammar() {
		
	}
        
	
}
