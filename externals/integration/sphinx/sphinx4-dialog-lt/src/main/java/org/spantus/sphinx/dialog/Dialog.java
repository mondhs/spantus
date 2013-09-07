
package org.spantus.sphinx.dialog;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.sphinx.dialog.beheviour.MyBehavior;
import org.spantus.sphinx.dialog.beheviour.MyMusicBehavior;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;


/**
 * A simple Dialog demo showing a simple speech application built using Sphinx-4 that uses the DialogManager.
 * <p/>
 * This demo uses a DialogManager to manage a set of dialog states. Each dialog state potentially has its own grammar.
 */
public class Dialog {
	private static final Logger LOG = LoggerFactory.getLogger(Dialog.class); 

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



            LOG.info("\nWelcome to the Sphinx-4 Dialog Demo "
                    + " - Version 1.0\n");

            dialogManager.addNode("menu", new MyBehavior());
//            dialogManager.addNode("email", new MyBehavior());
//            dialogManager.addNode("games", new MyBehavior());
            dialogManager.addNode("news", new MyBehavior());
//            dialogManager.addNode("music", new MyMusicBehavior());
//            dialogManager.addNode("movies", new MyBehavior());
            dialogManager.addNode("phone", new MyBehavior());
//            dialogManager.addNode("books", new MyBehavior());

            dialogManager.setInitialNode("menu");

            LOG.info("Loading dialogs ...");

            dialogManager.allocate();


            LOG.info("Running  ...");

            dialogManager.go();

            LOG.info("Cleaning up  ...");

            dialogManager.deallocate();

        } catch (IOException e) {
        	LOG.error("Problem when loading Dialog: ", e);
        } catch (PropertyException e) {
        	LOG.error("Problem configuring Dialog: ", e);
        }
        System.exit(0);
    }
}
