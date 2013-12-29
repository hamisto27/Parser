package be.ac.ulb.infof403.parserltc;

import java.io.InputStream;
import java.util.Arrays;

public class TokenStreamImpl implements TokenStream{

	private Scanner scanner;
	private Object[] mElements = new Object[] {};
	
	// call TokenStreamImpl.Scanner(in);
	public void Scanner(InputStream in) {	
		 scanner = new Scanner(in);
	}
	
	@Override
	public Object poll() {
		// TODO Auto-generated method stub
		Object result = null;
    	if (null != mElements && 0 < mElements.length) {
    		result = mElements[mElements.length - 1];
    		//remove element from array
    		mElements[mElements.length - 1] = null;
    	}
    	return result;
		
	}

	@Override
	public Object peek() {
		// TODO Auto-generated method stub
		
		if (null != mElements && 0 < mElements.length)
    		return mElements[mElements.length - 1];
    	else
    		return null;
		
	}

	@Override
	public Object previousValue() {
		
		
		return mElements;
		// TODO Auto-generated method stub
		
	}
}
