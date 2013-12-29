package be.ac.ulb.infof403.parserltc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

public class Parser {

	private interface Rule {
		void match(Queue<Symbol> tokens) throws MatchException;
	}

	private class TokenTypeRule implements Rule {

		private final LexicalUnit tokenType;

		public TokenTypeRule(LexicalUnit tokenType) {
			this.tokenType = tokenType;
		}

		@Override
		public void match(Queue<Symbol> tokens) throws MatchException {
			if (!tokens.peek().getUnitType().equals(tokenType)) {
				throw new MatchException();
			}
			tokens.poll();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "@" + tokenType;
		}
	}

	private class TokenValueRule implements Rule {

		private final String value;

		public TokenValueRule(String value) {
			this.value = value;
		}

		@Override
		public void match(Queue<Symbol> tokens) throws MatchException {
			if (!tokens.peek().equals(value)) {
				throw new MatchException();
			}
			tokens.poll();
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "@" + value;
		}
	}

	private class PatternRule implements Rule {

		private Rule[] pattern;

		public PatternRule(Rule[] pattern) {
			super();
			this.pattern = pattern;
		}

		public void match(Queue<Symbol> tokens) throws MatchException {
			for (Rule r : pattern) {
				r.match(tokens);
			}
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + "@"
					+ Arrays.toString(pattern);
		}

	}

	private class MultiRule implements Rule {

		private List<Rule> rules;

		public MultiRule() {
			rules = new ArrayList<Rule>();
		}

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
		public void match(Queue<Symbol> tokens) throws MatchException {
			boolean match = false;
			for (Rule rule : rules) {
				try {
					rule.match(tokens);
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
		public void match(Queue<Symbol> tokens) throws MatchException {
			if (target != null) {
				target.match(tokens);
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

	private Map<String, Symbol<?>> tableOfSymbols;

	private static final String TEST_FILE_PATH = "test.txt";
	private static final String GRAMMAR_FILE_PATH = "grammar.txt";

	private static final int NAME = 0;
	private static final int PATTERN = 1;

	private static final Pattern TOKEN_PATTERN = Pattern
			.compile("(<?[\\w-\\\\]+>?|\\.|[<>]=?|[\\+*/=)(])");

	private static final String ROLE_TOKEN_PREFIX = "<";
	private static final String ROLE_TOKEN_SUFFIX = ">";

	public Parser() throws IOException {
		loadRules(new BufferedReader(new FileReader(GRAMMAR_FILE_PATH)));
	}

	public void parse() throws IOException {
		InputStream scannerStream = null;
		// open input stream test.txt for reading purpose.
		scannerStream = new FileInputStream(TEST_FILE_PATH);
		
		this.scanner = new Scanner(scannerStream);
		this.tableOfSymbols = scanner.getTableOfSymbols();
		Symbol<?> lexicalUnit;
		do {
			lexicalUnit = scanner.next_token();
			if (lexicalUnit != null) {
				System.out.println("token: " + lexicalUnit.getValue()
						+ " \tlexical unit: " + lexicalUnit.getUnitType());
			}
		} while (lexicalUnit == null
				|| !lexicalUnit.getUnitType().equals(LexicalUnit.EOF));
	}

	private void loadRules(BufferedReader reader) {

		rules = new HashMap<String, Parser.RuleProxy>();
		// load built in rules
		addRule("<INTEGER>", new TokenTypeRule(LexicalUnit.INTEGER));
		addRule("<ID>", new TokenTypeRule(LexicalUnit.IDENTIFIER));
		addRule("<IMAGE>", new TokenTypeRule(LexicalUnit.IMAGE));

		// load file rules
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				// process the line.
				String[] splitted = line.split("->");
				assert splitted.length == 2;

				splitted[NAME] = sanitizeRuleName(splitted[NAME]);
				splitted[PATTERN] = sanitizePatternString(splitted[PATTERN]);
				Matcher m = TOKEN_PATTERN.matcher(splitted[PATTERN]);
				LinkedList<String> patternTokens = new LinkedList<String>();

				while (m.find()) {
					patternTokens.add(m.group());
				}

				if (patternTokens.size() > 1) {
					// this is a pattern

					Rule[] patternRules = new Rule[patternTokens.size()-1];

					int i = 0;
					for (String token : patternTokens) {
						patternRules[i] = getOrcreateTokenRule(token);
						assert patternRules[i] != null;
					}
					addRule(sanitizeRuleName(splitted[NAME]), new PatternRule(
							patternRules));
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
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String sanitizePatternString(String string) {
		return string.trim().replaceAll("\\s+", " ");
	}

	private String sanitizeRuleName(String name) {
		return name.trim();
	}

	private Rule getOrcreateTokenRule(String token) {
		if (this.rules.containsKey(token)) {
			// e' una regola definita in precedenza, la inserisco
			return this.rules.get(token);
		} else if (token.startsWith(ROLE_TOKEN_PREFIX) && token.endsWith(ROLE_TOKEN_SUFFIX)) {
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
		System.out.println("addRule(" + ruleName + " -> " + rule + ")");
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
