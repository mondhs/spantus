package org.spantus.sphinx.dialog.beheviour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.sphinx.dialog.Dialog;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;

/**
 * An extension of the standard node behavior for music. This node will add rules to the grammar based upon the contents
 * of the music.txt file. This provides an example of how to extend a grammar directly from code as opposed to writing
 * out a JSGF file.
 */
public class MyMusicBehavior extends MyBehavior {
	private static final Logger LOG = LoggerFactory.getLogger(MyMusicBehavior.class);
    private List<String> songList = new ArrayList<String>();


    /** Creates a music behavior */
    public MyMusicBehavior() {
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
        	LOG.error("Can't get playlist", ioe);
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
        	LOG.error("Trouble with the grammar ", ge);
            throw new IOException("Can't add rules for playlist", ge);
        }
        // now lets commit the changes
        getGrammar().commitChanges();
        grammarChanged();
    }
}