package be.ac.ulb.infof403.parserltc;

public interface TokenStream {
	
	public Object poll();
	
	public Object peek();
	
	public Object previousValue();

}
