package be.ac.ulb.infof403.parserltc;

import java.io.IOException;

public class TokenStreamImpl implements TokenStream{

	private Scanner scanner;
	private Symbol<?> currentToken;
	private Symbol<?> previousToken;

	public TokenStreamImpl(Scanner scanner) {		
		this.scanner = scanner;
	}
	
	@Override
	public Symbol<?> poll() throws IOException {
		// TODO Auto-generated method stub
		this.previousToken = this.currentToken;
		this.currentToken = this.scanner.next_token();
		return currentToken;		
	}

	@Override
	public Symbol<?> peek() throws IOException {
		// TODO Auto-generated method stub
		if(this.currentToken == null) {
		    this.currentToken = this.scanner.next_token();
		}
		return currentToken;
		
	}

	public Symbol<?> getPreviousToken() {
		return previousToken;
	}
}
