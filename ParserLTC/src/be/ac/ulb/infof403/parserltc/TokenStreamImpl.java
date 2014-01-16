package be.ac.ulb.infof403.parserltc;

import java.io.IOException;

public class TokenStreamImpl implements TokenStream{

	private Scanner scanner;
	private Symbol<String> currentToken;
	private Symbol<String> previousToken;
	private String programID;

	public TokenStreamImpl(Scanner scanner) {		
		this.scanner = scanner;
		
	}
	
	@Override
	public Symbol<String> poll() throws IOException {
		// TODO Auto-generated method stub
		this.previousToken = this.currentToken;
		this.currentToken = this.scanner.next_token();
		return currentToken;		
	}

	@Override
	public Symbol<String> peek() throws IOException {
		// TODO Auto-generated method stub
		if(this.currentToken == null) {
		    this.currentToken = this.scanner.next_token();
		}
		return currentToken;
		
	}
	
	public Boolean checkID() {
		
		 if(this.currentToken.getUnitType() == LexicalUnit.IDENTIFIER){
	    	   if(this.programID == null)
	    		   this.programID = this.currentToken.getValue();
	    	   else
	    		   return this.programID.equals(this.currentToken.getValue());
	    		   
	       }
		 
		 return false;		
	}
	

	public Symbol<String> getCurrentToken() {
		return currentToken;
	}

	public void setCurrentToken(Symbol<String> currentToken) {
		this.currentToken = currentToken;
	}

	public void setPreviousToken(Symbol<String> previousToken) {
		this.previousToken = previousToken;
	}

	public Symbol<String> getPreviousToken() {
		return previousToken;
	}
}
