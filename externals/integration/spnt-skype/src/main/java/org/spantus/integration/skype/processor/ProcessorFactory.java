package org.spantus.integration.skype.processor;

import org.spantus.integration.skype.SkypeClient;

/**
 *
 * @author mondhs
 */
public abstract class ProcessorFactory {
    public static IncomeSkypeMessageProcessor createProcessor(SkypeClient skypeClient){

        IncomeSkypeMessageHandleProcessor processor =
                  new IncomeSkypeMessageHandleProcessor();
        processor.setSkypeClient(skypeClient);
//        IncomeSkypeMessageLogProcessor processor = new IncomeSkypeMessageLogProcessor();
        return processor;
    }
}