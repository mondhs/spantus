/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.container.panel;

import java.awt.Frame;
import java.util.List;
import org.spantus.externals.recognition.bean.RecognitionResult;
import org.spantus.work.ui.dto.SpantusWorkInfo;

/**
 *
 * @author mondhs
 */
public class RecognizeDetailDialog extends SpantusAboutDialog {

    public RecognizeDetailDialog(Frame owner) {
        super(owner);
    }

    public void updateCtx(SpantusWorkInfo ctx, List<RecognitionResult> results) {
        String css = "<head><style type=\"text/css\">"
                + ".label{font-weight:bold}"
                + "</style></head>";
        super.getJEditorPane().setText("<html>" + css + "<body><p>" + representResults(results) + "</p></body></html>");
        super.getJEditorPane().setCaretPosition(0);
    }

    private StringBuilder representResults(List<RecognitionResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        for (RecognitionResult recognitionResult : results) {
            sb.append("<tr>");
            sb.append("<td>")
                    .append(recognitionResult.getInfo().getName())
                    .append("</td>");
            sb.append("<td>")
                    .append(recognitionResult.getDistance())
                    .append("</td>");

            sb.append("</tr>");
        }
        if(results.isEmpty()){
            sb.append("<tr>");
            sb.append("<td>")
                    .append("No recognition pattern is found. Try to learn program some patterns first.")
                    .append("</td>");

            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb;
    }
}
