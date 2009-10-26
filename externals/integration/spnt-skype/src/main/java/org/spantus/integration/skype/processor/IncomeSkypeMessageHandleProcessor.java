package org.spantus.integration.skype.processor;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.spantus.integration.skype.SkypeClient;
import org.spantus.integration.skype.handler.AbstractMessageHandler;
import org.spantus.integration.skype.handler.CallHandler;
import org.spantus.integration.skype.handler.ChatHandler;
import org.spantus.integration.skype.handler.EchoChatMessageHandler;

public class IncomeSkypeMessageHandleProcessor implements IncomeSkypeMessageProcessor {

    private Logger log = Logger.getLogger(IncomeSkypeMessageHandleProcessor.class);
    SkypeClient skypeClient;
    List<AbstractMessageHandler> handlers;

    public IncomeSkypeMessageHandleProcessor() {
        handlers = new ArrayList<AbstractMessageHandler>();
        handlers.add(new ChatHandler());
        handlers.add(new EchoChatMessageHandler());
        handlers.add(new CallHandler());
    }

    /**
     * Notification from skype
     * @param msg
     */
    public void notify(String msg) {
        for (AbstractMessageHandler handler : handlers) {
            handler.setSkypeClient(getSkypeClient());
            if(handler.handle(msg)){
                log.debug("handled by" +handler.getClass().getSimpleName() + ": " + msg);
            }
        }
    }

    public SkypeClient getSkypeClient() {
        return skypeClient;
    }

    public void setSkypeClient(SkypeClient skypeClient) {
        this.skypeClient = skypeClient;
    }
}