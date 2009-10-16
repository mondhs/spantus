package org.spantus.integration.skype;

public interface SkypeClient{
/**
     * send message to skype with message
     * @param pattern
     * @param vars
     * @return
     */
    public String invoke(String pattern, Object... vars);
    /**
     *  send message to skype plain string
     * @param msg
     * @return
     */
    public String invoke(String msg);
}