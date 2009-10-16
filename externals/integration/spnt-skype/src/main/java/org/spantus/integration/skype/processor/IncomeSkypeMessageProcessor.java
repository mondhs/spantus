package org.spantus.integration.skype.processor;


public interface IncomeSkypeMessageProcessor{
     /**
     * Notification from skype
     * @param msg
     */
    public void notify(String msg);
}