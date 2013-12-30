package be.ac.ulb.infof403.parserltc;

import java.io.IOException;

public interface TokenStream {
	
	public Symbol<?> poll() throws IOException;
	
	public Symbol<?> peek() throws IOException;

}
