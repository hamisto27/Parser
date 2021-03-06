package be.ac.ulb.infof403.parserltc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

	private interface Rule {
		void match(TokenStreamImpl tokenStreamImpl) throws MatchException,
				IOException;
	}
	private class TokenTypeRule implements Rule{

		private final LexicalUnit tokenType;

		public TokenTypeRule(LexicalUnit tokenType) {
			this.tokenType = tokenType;
		}

		@Override
		public void match(TokenStreamImpl tokenStreamImpl)
				throws MatchException, IOException {
			System.out.println("scanner token type:" + tokenStreamImpl.peek().getUnitType());
			System.out.println("lexical type:" + this.tokenType);

			if (!tokenStreamImpl.peek().getUnitType().equals(this.tokenType)) {
				throw new MatchException();
		    }
	       System.out.println("MATCH");
	       
	    
	       programID = tokenStreamImpl.checkID();       
		   tokenStreamImpl.poll();   
		   
		}


		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "@ " + tokenType;
		}

	}

	private class TokenValueRule implements Rule{

		private final String value;
		private LexicalUnit lexicalValue;

		public TokenValueRule(String value) {
			this.value = value;
			if (this.value.equals(".\\n")) {
				this.lexicalValue = LexicalUnit.END_OF_INSTRUCTION;
			} else {
				this.lexicalValue = LexicalUnit.convert(this.value);
			}
		}

		@Override
		public void match(TokenStreamImpl tokenStreamImpl)
				throws MatchException, IOException {
			
			if(this.lexicalValue == null){
				;
			}
			else{
				System.out.println("scanner token value:"
						+ tokenStreamImpl.peek().getUnitType());
				System.out.println("lexical value: " + this.lexicalValue);
				if (!tokenStreamImpl.peek().getUnitType().equals(this.lexicalValue)) {

					throw new MatchException();
				}
				System.out.println("MATCH");
				tokenStreamImpl.poll();
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "@ " + lexicalValue;
		}
	}

	private class PatternRule implements Rule {

		private Rule[] pattern;
		private List<String> names;
		private String pattern_string;

		public PatternRule(Rule[] pattern, ArrayList<String> names,
				String pattern_string) {
			super();
			this.pattern = pattern;
			this.names = names;
			this.pattern_string = pattern_string;
		}

		public void match(TokenStreamImpl tokenStreamImpl)
				throws MatchException, IOException {

			for (String name : this.names) {
				rules.get(name).match(tokenStreamImpl);
			}
		}

		@Override
		public String toString() {

			return this.getClass().getSimpleName() + "@: " + pattern_string;
		}

	}

	private class MultiRule implements Rule {

		private List<Rule> rules;

		public MultiRule() {
			rules = new ArrayList<Rule>();
		}

		// It means that function can receive multiple rules arguments.
		public MultiRule(Rule... rules) {
			this();
			for (Rule r : rules) {
				assert r != null;
				this.rules.add(r);
			}
		}

		private void add(Rule rule) {
			this.rules.add(rule);
		}

		@Override
		public void match(TokenStreamImpl tokenStreamImpl)
				throws MatchException, IOException {
			boolean match = false;
			for (Rule rule : rules) {
				try {
						if(match == true){
							break;
						}
						rule.match(tokenStreamImpl);
						match = true;
				} catch (Exception e) {
					;
				}
			}
			if (match == false) {
				throw new MatchException();
			}
		}
		
		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "@" + rules;
		}
	}

	private class RuleProxy implements Rule {

		private String name;
		private Rule target;

		public RuleProxy(String name) {
			this.name = name;
		}

		@Override
		public void match(TokenStreamImpl tokenStreamImpl)
				throws MatchException, IOException {
			if (target != null) {
				target.match(tokenStreamImpl);
			} else {
				throw new RuntimeException(
						"Questo proxy sarebbe dovuto essere inizializzato con regola corrispondente a "
								+ name);
			}
		}

		public String getName() {
			return name;
		}

		public Rule getTarget() {
			return target;
		}

		public void setTarget(Rule target) {
			this.target = target;
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "@" + name + "->" + target;
		}
	}

	private HashMap<String, RuleProxy> rules;

	private Scanner scanner;

	private TokenStreamImpl tokenStreamImpl;

	private Map<String, Symbol<?>> tableOfSymbols;
	
	private boolean programID = false;

	private static final String TEST_FILE_PATH = "test.txt";
	private static final String GRAMMAR_FILE_PATH = "grammar.txt";

	private static final int NAME = 0;
	private static final int PATTERN = 1;

	private static final Pattern TOKEN_PATTERN = Pattern
			.compile("(<?[\\w-\\\\]+>?|\\.|[<>]=?|[\\+*/=,)(])");

	private static final String ROLE_TOKEN_PREFIX = "<";
	private static final String ROLE_TOKEN_SUFFIX = ">";

	public Parser() throws IOException {
		loadRules(new BufferedReader(new FileReader(GRAMMAR_FILE_PATH)));
	}

	public void parse() throws IOException, MatchException {
		
		InputStream scannerStream = null;
		// open input stream test.txt for reading purpose.
		scannerStream = new FileInputStream(TEST_FILE_PATH);

		this.scanner = new Scanner(scannerStream);
		this.tableOfSymbols = scanner.getTableOfSymbols();
		// delegate class
		this.tokenStreamImpl = new TokenStreamImpl(scanner);

		rules.get("<PROGRAM>").match(tokenStreamImpl);
		
		//check id of program
		if(!programID)
			throw new MatchException();
	
		for (String key : this.tableOfSymbols.keySet()) {

			Symbol<?> symbol = this.tableOfSymbols.get(key);
			if(symbol != null)
				System.out.println("TYPE: " + symbol.getUnitType() + " VALUE: " + symbol.getValue());
		}
		/*
		 * System.out.println(rules.get("<END_INST>").toString()); if
		 * (rules.get("<END_INST>").getTarget() instanceof PatternRule){
		 * for(String name2:
		 * ((PatternRule)rules.get(rules.get("<END_INST>").getName
		 * ()).getTarget()).getNames()){ if(rules.get(name2).getTarget()
		 * instanceof TokenValueRule){
		 * System.out.println(rules.get(name2).toString()); } } }
		 */
		// if (startRule.getTarget() instanceof PatternRule){
		// startRule.getTarget().match(tokenStreamImpl);
		// System.out.println("PATTERN  VALUE: " + ((PatternRule)
		// startRule.getTarget()).toString());
		// System.out.println("RULES:");
		/*
		 * for(String name: ((PatternRule) startRule.getTarget()).getNames()){
		 * if(rules.get(name).getTarget() instanceof PatternRule){
		 * System.out.println("PATTERN NAME: " + rules.get(name).getName() +
		 * " PATTERN  VALUE: " + rules.get(name).getTarget().toString());
		 * //rules.get(name).match(tokenStreamImpl); for(String name2:
		 * ((PatternRule)rules.get(name).getTarget()).getNames()){
		 * if(rules.get(name2).getTarget() instanceof TokenValueRule){
		 * System.out.println(rules.get(name2).toString()); } } }
		 * 
		 * }
		 */
		// }

		for (String key : this.rules.keySet()) {

			RuleProxy rule = rules.get(key);
			if(rule.getTarget() instanceof MultiRule){
				
			}
			/*
			 * if(rule.getTarget() instanceof MultiRule){
			 * System.out.println("MULTI RULE rule-name: " + rule.getName() +
			 * "   rule-key: " + key); } else if (rule.getTarget() instanceof
			 * TokenTypeRule){ System.out.println("TOKEN TYPE RULE rule-name: "
			 * + rule.getName() + "   rule-key: " + key); } else if
			 * (rule.getTarget() instanceof TokenValueRule){
			 * System.out.println("TOKEN VALUE RULE rule-name: " +
			 * rule.getName() + "   rule-key: " + key);
			 * System.out.println("token-value: " + ((TokenValueRule)
			 * rule.getTarget()).toString()); } else if (rule.getTarget()
			 * instanceof PatternRule){
			 * System.out.println("PATTERN  RULE rule-name: " + rule.getName() +
			 * "   rule-key: " + key); System.out.println("PATTERN  VALUE: " +
			 * ((PatternRule) rule.getTarget()).toString()); }
			 */

		}
		Symbol<?> lexicalUnit;
		do {
			lexicalUnit = tokenStreamImpl.poll();
			if (lexicalUnit != null) {
				// System.out.println("token: " + lexicalUnit.getValue()
				// + " \tlexical unit: " + lexicalUnit.getUnitType());
			}
		} while (lexicalUnit == null
				|| !lexicalUnit.getUnitType().equals(LexicalUnit.EOF));
	}

	private void loadRules(BufferedReader reader) {

		// changed hashmap to linkedhashmap.
		rules = new LinkedHashMap<String, Parser.RuleProxy>();
		// load built in rules
		Rule ruleInteger = addRule("<INTEGER>", new TokenTypeRule(LexicalUnit.INTEGER));
		addRule("<ID>", new MultiRule(new TokenTypeRule(LexicalUnit.IDENTIFIER), ruleInteger));
		addRule("<IMAGE>", new TokenTypeRule(LexicalUnit.IMAGE));

		// load file rules
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				// process the line.
				String[] splitted = line.split("->");

				splitted[NAME] = sanitizeRuleName(splitted[NAME]);
				splitted[PATTERN] = sanitizePatternString(splitted[PATTERN]);
				Matcher m = TOKEN_PATTERN.matcher(splitted[PATTERN]);
				LinkedList<String> patternTokens = new LinkedList<String>();
				
				while (m.find()) {
					patternTokens.add(m.group());
				}

				if (patternTokens.size() == 2
						&& patternTokens.get(0).equals(".")
						&& patternTokens.get(1).equals("\\n")) {
					Rule[] patternRules = new Rule[1];
					ArrayList<String> names = new ArrayList<String>();
					String total_pattern = ".\\n";
					names.add(".\\n");
					patternRules[0] = getOrcreateTokenRule(".\\n");
					addRule(sanitizeRuleName(splitted[NAME]), new PatternRule(
							patternRules, names, total_pattern));
				} else if (patternTokens.size() > 1) {
					// this is a pattern

					Rule[] patternRules = new Rule[patternTokens.size() - 1];
					ArrayList<String> names = new ArrayList<String>();
					int i = 0;
					String total_pattern = "";
					for (String token : patternTokens) {
						patternRules[i] = getOrcreateTokenRule(token);
						names.add(token);
						total_pattern = total_pattern + token + " ";
						// assert patternRules[i] != null;
					}
					addRule(sanitizeRuleName(splitted[NAME]), new PatternRule(
							patternRules, names, total_pattern));
				} else {
					addRule(splitted[NAME],
							getOrcreateTokenRule(splitted[PATTERN]));
				}
			}
			// ho finito l'analisi del file, controllo che tutti i proxy
			// siano inizializzati
			for (RuleProxy p : this.rules.values()) {
				if (p.getTarget() == null) {
					throw new RuntimeException("Impossibile trovare la regola "
							+ p.getName());
				} else {
					;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String sanitizePatternString(String string) {
		// matches one or many whitespaces
		return string.trim().replaceAll("\\s+", " ");
	}

	private String sanitizeRuleName(String name) {
		return name.trim();
	}

	private Rule getOrcreateTokenRule(String token) {
		if (this.rules.containsKey(token)) {
			// e' una regola definita in precedenza, la inserisco
			return this.rules.get(token);
		} else if (token.startsWith(ROLE_TOKEN_PREFIX)
				&& token.endsWith(ROLE_TOKEN_SUFFIX)) {
			// richiama un altra regola, che al momento potrebbe non essere
			// ancora stata definita
			return getRule(token, true);
		} else {
			// e' un literal
			return addRule(token, new TokenValueRule(token));
		}
	}

	private RuleProxy getRule(String ruleName, boolean createIfNotPresent) {
		if (createIfNotPresent) {
			return addRule(ruleName, null);
		} else {
			return this.rules.get(ruleName);
		}
	}

	private RuleProxy addRule(String ruleName, Rule rule) {
		// System.out.println("addRule(" + ruleName + " -> " + rule + ")");
		RuleProxy proxy = this.rules.get(ruleName);
		if (proxy == null) {
			proxy = new RuleProxy(ruleName);
			this.rules.put(ruleName, proxy);
		}

		if (rule != null) {
			Rule mappedRule = proxy.getTarget();
			if (mappedRule instanceof MultiRule) {
				((MultiRule) mappedRule).add(rule);
			} else if (mappedRule == null) {
				proxy.setTarget(rule);
			} else {
				// ua regola esiste gia, e non e' una MultiRule, quindi prima la
				// creo e le aggiungo entrambe
				proxy.setTarget(new MultiRule(mappedRule, rule));
			}
		}
		return proxy;
	}
}
