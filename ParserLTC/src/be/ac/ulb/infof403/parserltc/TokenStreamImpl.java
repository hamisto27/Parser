package be.ac.ulb.infof403.parserltc;

import java.io.IOException;

public class TokenStreamImpl implements TokenStream{

	private Scanner scanner;
	private Symbol<?> currentToken;

	public TokenStreamImpl(Scanner scanner) {		
		this.scanner = scanner;
	}
	
	@Override
	public Object poll() throws IOException {
		// TODO Auto-generated method stub
		this.currentToken = this.scanner.next_token();
		return currentToken;		
	}

	@Override
	public Object peek() throws IOException {
		// TODO Auto-generated method stub
		if(this.currentToken == null) {
		    this.currentToken = this.scanner.next_token();
		}
		return currentToken;
		
	}
}
