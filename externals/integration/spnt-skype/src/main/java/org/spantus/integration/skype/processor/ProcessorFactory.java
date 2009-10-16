package org.spantus.integration.skype.processor;

import org.spantus.integration.skype.SkypeClient;

/**
 *
 * @author mondhs
 */
public abstract class ProcessorFactory {
    public static IncomeSkypeMessageProcessor createProcessor(SkypeClient skypeClient){
//        return new IncomeSkypeMessageLogProcessor();
        IncomeSkypeMessageEchoProcessor processor = new IncomeSkypeMessageEchoProcessor();
        processor.setSkypeClient(skypeClient);
        return processor;
    }
}