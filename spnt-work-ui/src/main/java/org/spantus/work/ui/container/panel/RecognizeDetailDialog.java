/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.container.panel;

import java.awt.Frame;
import java.util.List;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

/**
 *
 * @author mondhs
 */
public class RecognizeDetailDialog extends SpantusAboutDialog {

    public RecognizeDetailDialog(Frame owner) {
        super(owner);
        setTitle(I18nFactory.createI18n().getMessage("patternRecognized"));
        getJEditorPane().addHyperlinkListener(new RecognitionHyperlinkListener());
    }

    public void updateCtx(SpantusWorkInfo ctx, List<RecognitionResultDetails> results) {
        String css = "<head><style type=\"text/css\">"
                + ".label{font-weight:bold}"
                + "</style></head>";
        super.getJEditorPane().setText("<html>" + css + "<body><p>" + representResults(results) + "</p></body></html>");
        super.getJEditorPane().setCaretPosition(0);

    }

    private StringBuilder representResults(List<RecognitionResultDetails> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        for (RecognitionResult recognitionResult : results) {
            sb.append("<tr>");
            sb.append("<td>").
                    append("<a href=\"").append(recognitionResult.getInfo().getId())
                    .append("\">").
                    append(recognitionResult.getInfo().getName()).append("</a></td>");
            sb.append("<td>").append(recognitionResult.getDistance()).append("</td>");

            sb.append("</tr>");
        }
        if (results.isEmpty()) {
            sb.append("<tr>");
            sb.append("<td>").append("No recognition pattern is found. Try to learn program some patterns first.").append("</td>");

            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb;
    }

    class RecognitionHyperlinkListener implements HyperlinkListener {

        public void hyperlinkUpdate(HyperlinkEvent e) {
            if(HyperlinkEvent.EventType.ACTIVATED == e.getEventType()){
                String desc = e.getDescription();
                desc.charAt(1);
            }
        }
    }
}
