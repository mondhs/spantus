
package org.spantus.sphinx.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.TimerPool;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;


/**
 * A simple Dialog demo showing a simple speech application built using Sphinx-4 that uses the DialogManager.
 * <p/>
 * This demo uses a DialogManager to manage a set of dialog states. Each dialog state potentially has its own grammar.
 */
public class Dialog {

    /** Main method for running the Dialog demo. 
     * @throws JSGFGrammarException 
     * @throws JSGFGrammarParseException 
     **/
    public static void main(String[] args) throws JSGFGrammarParseException, JSGFGrammarException {
        try {
            URL url;
            if (args.length > 0) {
                url = new File(args[0]).toURI().toURL();
            } else {
                url = Dialog.class.getResource("dialog.config.xml");
            }
            ConfigurationManager cm = new ConfigurationManager(url);

            DialogManager dialogManager = (DialogManager)
                    cm.lookup("dialogManager");



            System.out.println("\nWelcome to the Sphinx-4 Dialog Demo "
                    + " - Version 1.0\n");

            dialogManager.addNode("menu", new MyBehavior());
            dialogManager.addNode("email", new MyBehavior());
            dialogManager.addNode("games", new MyBehavior());
            dialogManager.addNode("news", new MyBehavior());
            dialogManager.addNode("music", new MyMusicBehavior());
            dialogManager.addNode("movies", new MyBehavior());
            dialogManager.addNode("phone", new MyBehavior());
            dialogManager.addNode("books", new MyBehavior());

            dialogManager.setInitialNode("menu");

            System.out.println("Loading dialogs ...");

            dialogManager.allocate();


            System.out.println("Running  ...");

            dialogManager.go();

            System.out.println("Cleaning up  ...");

            dialogManager.deallocate();

        } catch (IOException e) {
            System.err.println("Problem when loading Dialog: " + e);
        } catch (PropertyException e) {
            System.err.println("Problem configuring Dialog: " + e);
        }
        System.exit(0);
    }
}


/**
 * Defines the standard behavior for a node. The standard behavior is: <ul> <li> On entry the set of sentences that can
 * be spoken is displayed. <li> On recognition if a tag returned contains the prefix 'dialog_' it indicates that control
 * should transfer to another dialog node. </ul>
 */
class MyBehavior extends NewGrammarDialogNodeBehavior {

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
        System.out.println(" ======== " + getGrammarName() + " =======");
        dumpSampleUtterances();
        System.out.println(" =================================");
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
            System.out.println("\n "
                    + result.getBestFinalResultNoFiller() + '\n');
            if (tag.equals("exit")) {
                System.out.println("Goodbye! Thanks for visiting!\n");
                System.exit(0);
            }
            if (tag.equals("help")) {
                help();
            } else if (tag.equals("stats")) {
                TimerPool.dumpAll();
            } else if (tag.startsWith("goto_")) {
                return tag.replaceFirst("goto_", "");
            } else if (tag.startsWith("browse")) {
                execute(tag);
            }
        } else {
            System.out.println("\n Oops! didn't hear you.\n");
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
            System.out.println("  " + sampleUtterance);
        }
    }


    /** Indicated that the grammar has changed and the collection of sample utterances should be regenerated. */
    protected void grammarChanged() {
        sampleUtterances = null;
    }
}

/**
 * An extension of the standard node behavior for music. This node will add rules to the grammar based upon the contents
 * of the music.txt file. This provides an example of how to extend a grammar directly from code as opposed to writing
 * out a JSGF file.
 */
class MyMusicBehavior extends MyBehavior {

    private List<String> songList = new ArrayList<String>();


    /** Creates a music behavior */
    MyMusicBehavior() {
        try {
            InputStream is = Dialog.class.getResourceAsStream("playlist.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String song;

            while ((song = br.readLine()) != null) {
                if (!song.isEmpty()) {
                    songList.add(song);
                }
            }
            br.close();
        } catch (IOException ioe) {
            System.err.println("Can't get playlist");
        }
    }


    /** Executed when we enter this node. Displays the active grammar 
     * @throws JSGFGrammarException 
     * @throws JSGFGrammarParseException */
    public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException {
        super.onEntry();

        // now lets add our custom songs from the play list
        // First, get the JSAPI RuleGrammar
        BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
        try {
            recognizer.allocate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        RuleGrammar ruleGrammar = new BaseRuleGrammar (recognizer, getGrammar().getRuleGrammar());

        // now lets add a rule for each song in the play list

        String ruleName = "song";
        int count = 1;

        try {
            for (String song : songList) {
                String newRuleName = ruleName + count;
                Rule newRule = ruleGrammar.ruleForJSGF("listen to " + song
                    + " { " + newRuleName + " }");
                ruleGrammar.setRule(newRuleName, newRule, true);
                ruleGrammar.setEnabled(newRuleName, true);
                count++;
            }
        } catch (GrammarException ge) {
            System.out.println("Trouble with the grammar " + ge);
            throw new IOException("Can't add rules for playlist " + ge);
        }
        // now lets commit the changes
        getGrammar().commitChanges();
        grammarChanged();
    }
}
