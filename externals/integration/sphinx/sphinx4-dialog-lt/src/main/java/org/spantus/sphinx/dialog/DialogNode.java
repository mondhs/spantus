package org.spantus.sphinx.dialog;

import java.io.IOException;

import javax.speech.recognition.GrammarException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spantus.sphinx.dialog.beheviour.DialogNodeBehavior;

import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;

/**
 * Represents a node in the dialog
 */
public class DialogNode {
	private static final Logger LOG = LoggerFactory.getLogger(DialogNode.class);
    /**
	 * 
	 */
	private DialogManager dialogManager;
	private DialogNodeBehavior behavior;
    private String name;

    /**
     * Creates a dialog node with the given name an application
     * behavior
     *
     * @param name the name of the node
     *
     * @param behavior the application behavor for the node
     * @param dialogManager TODO
     *
     */
    DialogNode(DialogManager dialogManager, String name, DialogNodeBehavior behavior) {
        this.dialogManager = dialogManager;
		this.behavior = behavior;
        this.name = name;
    }


    /**
     * Initializes the node
     */
    
    void init() {
        behavior.onInit(this);
    }

    /**
     * Enters the node, prepares it for recognition
     * @throws JSGFGrammarException 
     * @throws JSGFGrammarParseException 
     */
    void enter() throws IOException, JSGFGrammarParseException, JSGFGrammarException {
        LOG.debug("Entering {}", name);
        behavior.onEntry();
        behavior.onReady();
    }

    /**
     * Performs recognition at the node.
     *
     * @return the result tag
     */
    String recognize() throws GrammarException {
    	LOG.debug("Recognize {}", name);
        Result result = this.dialogManager.recognize();
        return behavior.onRecognize(result);
    }

    /**
     * Exits the node
     */
    void exit() {
    	LOG.debug("Exiting {}", name);
        behavior.onExit();
    }

    /**
     * Gets the name of the node
     *
     * @return the name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the JSGF Grammar for the dialog manager that
     * contains this node
     *
     * @return the grammar
     */
    public JSGFGrammar getGrammar() {
        return this.dialogManager.getGrammar();
    }


    public DialogManager getDialogManager() {
        return this.dialogManager;
    }
}