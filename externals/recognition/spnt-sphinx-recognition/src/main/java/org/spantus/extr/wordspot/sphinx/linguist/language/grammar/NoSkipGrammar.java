package org.spantus.extr.wordspot.sphinx.linguist.language.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.linguist.dictionary.Dictionary;
import edu.cmu.sphinx.linguist.language.grammar.Grammar;
import edu.cmu.sphinx.linguist.language.grammar.GrammarNode;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.LogMath;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Component;

/**
 * 
 * @author ewilded
 * 
 */
public class NoSkipGrammar extends Grammar implements ResultListener {

	@S4Component(type = LogMath.class)
	public final static String PROP_LOG_MATH = "logMath";

	protected GrammarNode finalNode;
	private LogMath logMath;

	private final List<List<String>> keywords = new ArrayList<List<String>>();

	public NoSkipGrammar() {

	}

	public NoSkipGrammar(final String text, final LogMath logMath,
			final boolean showGrammar, final boolean optimizeGrammar,
			final boolean addSilenceWords, final boolean addFillerWords,
			final Dictionary dictionary) {
		super(showGrammar, optimizeGrammar, addSilenceWords, addFillerWords,
				dictionary);
		this.logMath = logMath;
		addKeyword(text);
	}

	public void addKeyword(String text) {
		StringTokenizer st = new StringTokenizer(text);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token = token.toUpperCase();
			if (token.compareTo(" ") != 0) {
				tokens.add(token);
			}
		}
		keywords.add(tokens);
	}

	/*
	 * (non-Javadoc) We want a very strict grammar structure like the following:
	 * InitialNode ----> KW1 ---> KW2 .... ---> KWn ---> FinalNode
	 * ↑________________________________________________|
	 */
	protected GrammarNode createGrammar() {
		initialNode = createGrammarNode(Dictionary.SILENCE_SPELLING);
		finalNode = createGrammarNode(Dictionary.SILENCE_SPELLING);
		initialNode.add(finalNode, LogMath.getLogOne());
		finalNode.add(initialNode, LogMath.getLogOne());
		GrammarNode lastWordGrammarNode = initialNode;
		for (List<String> tokens : keywords) {
			for (String token : tokens) {
				GrammarNode currNode = createGrammarNode(token);
				float value;
				if (lastWordGrammarNode == initialNode) {
					value = logMath.linearToLog(1.0 / keywords.size());
				} else {
					value = LogMath.getLogOne();
				}
				lastWordGrammarNode.add(currNode, value);
				lastWordGrammarNode = currNode;
			}
			lastWordGrammarNode.add(finalNode, LogMath.getLogOne());
			lastWordGrammarNode = initialNode;
			// Parallel keyword topology
			// initialNode.add(currNode, logMath.getLogOne());

			// currNode.add(finalNode, logMath.getLogOne());
		}
		return initialNode;
	}

	@Override
	public void newResult(Result result) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.cmu.sphinx.util.props.Configurable#newProperties(edu.cmu.sphinx.util
	 * .props.PropertySheet)
	 */
	@Override
	public void newProperties(PropertySheet ps) throws PropertyException {
		super.newProperties(ps);
		logMath = (LogMath) ps.getComponent(PROP_LOG_MATH);
	}

}
