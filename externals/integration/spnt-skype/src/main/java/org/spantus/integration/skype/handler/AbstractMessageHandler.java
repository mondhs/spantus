package org.spantus.integration.skype.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.spantus.integration.skype.SkypeClient;


public abstract class AbstractMessageHandler implements IncomeSkypeMessageHandler{
    protected Logger log = Logger.getLogger(getClass());
    SkypeClient skypeClient;

    public void setSkypeClient(SkypeClient skypeClient) {
        this.skypeClient = skypeClient;
    }

    public SkypeClient getSkypeClient() {
        return skypeClient;
    }

    public List<String> regexp(String pattern, String msg) {
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(msg);
        if (!m.find()) {
            return list;
        }
        for (int i = 1; i <= m.groupCount(); i++) {
            list.add(m.group(i));
        }
        return list;

    }

    public void assertResult(List<String> responseArgs, int size){
        if(responseArgs == null || responseArgs.size()!=size){
            throw new IllegalArgumentException();
        }
    }

}