package org.spantus.sphinx.dialog.beheviour;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.speech.recognition.GrammarException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.TimerPool;

/**
 * Defines the standard behavior for a node. The standard behavior is: <ul> <li> On entry the set of sentences that can
 * be spoken is displayed. <li> On recognition if a tag returned contains the prefix 'dialog_' it indicates that control
 * should transfer to another dialog node. </ul>
 */
public class MyBehavior extends NewGrammarDialogNodeBehavior {
	private static final Logger LOG = LoggerFactory.getLogger(MyBehavior.class);
    private Collection<String> sampleUtterances;


    /** Executed when we are ready to recognize */
    public void onReady() {
        super.onReady();
        help();
    }


    /**
     * Displays the help message for this node. Currently we display the name of the node and the list of sentences that
     * can be spoken.
     */
    protected void help() {
    	LOG.debug(" ======== {} =======", getGrammarName());
        dumpSampleUtterances();
        LOG.debug(" =================================");
    }


    /**
     * Executed when the recognizer generates a result. Returns the name of the next dialog node to become active, or
     * null if we should stay in this node
     *
     * @param result the recongition result
     * @return the name of the next dialog node or null if control should remain in the current node.
     */
    public String onRecognize(Result result) throws GrammarException {
        String tag = super.onRecognize(result);

        if (tag != null) {
        	LOG.debug("\n {} \n", result.getBestFinalResultNoFiller());
            if (tag.equals("exit")) {
            	LOG.debug("Goodbye! Thanks for visiting!\n");
                System.exit(0);
            }
            if (tag.equals("help")) {
                help();
            } else if (tag.equals("stats")) {
                TimerPool.dumpAll();
            } else if (tag.startsWith("goto_")) {
                return tag.replaceFirst("goto_", "");
            } else if (tag.startsWith("browse")) {
            	String command = tag.replaceFirst("browse ", "");
                execute(command);
            }
        } else {
        	LOG.debug("\n Oops! didn't hear you.\n");
        }
        return null;
    }


    /**
     * execute the given command
     *
     * @param cmd the command to execute
     */
    private void execute(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            // if we can't run the command, just fall back to
            // a non-working demo.
        	LOG.error("if we can't run the command, just fall back to {} ", e);
        }
    }


    /**
     * Collects the set of possible utterances.
     * <p/>
     * TODO: Note the current implementation just generates a large set of random utterances and tosses away any
     * duplicates. There's no guarantee that this will generate all of the possible utterances. (yep, this is a hack)
     *
     * @return the set of sample utterances
     */
    private Collection<String> collectSampleUtterances() {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < 100; i++) {
            String s = getGrammar().getRandomSentence();
            if (!set.contains(s)) {
                set.add(s);
            }
        }

        List<String> sampleList = new ArrayList<String>(set);
        Collections.sort(sampleList);
        return sampleList;
    }


    /** Dumps out the set of sample utterances for this node */
    private void dumpSampleUtterances() {
        if (sampleUtterances == null) {
            sampleUtterances = collectSampleUtterances();
        }

        for (String sampleUtterance : sampleUtterances) {
            LOG.debug("  {}", sampleUtterance);
        }
    }


    /** Indicated that the grammar has changed and the collection of sample utterances should be regenerated. */
    protected void grammarChanged() {
        sampleUtterances = null;
    }
}