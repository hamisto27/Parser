package be.ac.ulb.infof403.parserltc;

import java.io.IOException;

public interface TokenStream {
	
	public Object poll() throws IOException;
	
	public Object peek() throws IOException;

}
