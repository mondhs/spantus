package org.spantus.integration.skype;

import org.freedesktop.dbus.DBusInterface;
import org.spantus.integration.skype.processor.IncomeSkypeMessageProcessor;
import org.spantus.integration.skype.processor.ProcessorFactory;

/**
 *
 * @author mondhs
 */
public class SkypeDBusInterface implements DBusInterface {

    private SkypeClientImpl skypeClient = null;
    private IncomeSkypeMessageProcessor messageProcessor = null;

    public SkypeDBusInterface(SkypeClientImpl skypeClient) {
        this.skypeClient = skypeClient;
        messageProcessor = ProcessorFactory.createProcessor(skypeClient);
    }

    public int Notify(String s1) {
        messageProcessor.notify(s1);
        return 0;
    }

    @Override
    public boolean isRemote() {
        return false;
    }
}
