package be.ac.ulb.infof403.parserltc;

import java.util.HashMap;

public class Symbol<ValueType> extends HashMap<String, Object> {
	
	public static final String LINE = "LINE";
	public static final String COLUMN = "COLUMN";
	public static final String CONTENT = "CONTENT";
	public static final String IMAGE = "IMAGE";

	private final LexicalUnit unitType;
	private ValueType value;

	public Symbol(final LexicalUnit typeOfUnit) {
		this.unitType = typeOfUnit;
	}

	public ValueType getValue() {
		return value;
	}

	public void setValue(ValueType value) {
		this.value = value;
	}

	public LexicalUnit getUnitType() {
		return unitType;
	}
}
