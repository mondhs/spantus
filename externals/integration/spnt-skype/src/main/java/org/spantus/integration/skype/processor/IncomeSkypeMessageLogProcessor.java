package org.spantus.integration.skype.processor;

import org.apache.log4j.Logger;


public class IncomeSkypeMessageLogProcessor implements IncomeSkypeMessageProcessor{
    private Logger log = Logger.getLogger(IncomeSkypeMessageLogProcessor.class);

    /**
     * Notification from skype
     * @param msg
     */
    public void notify(String msg){
        log.debug("<" + msg);
    }
}