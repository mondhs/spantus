/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.work.ui.container.panel;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.i18n.I18nFactory;

/**
 *
 * @author mondhs
 */
public class RecognizeDetailDialog extends SpantusAboutDialog {

    private List<RecognitionResultDetails> results;

    private Long selctedId = null;

    public RecognizeDetailDialog(Frame owner) {
        super(owner);
        setTitle(I18nFactory.createI18n().getMessage("patternRecognized"));
        getJEditorPane().addHyperlinkListener(new RecognitionHyperlinkListener());
        HTMLEditorKit kit = new HTMLEditorKit();
        getJEditorPane().setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {color:#000; font-family:times; margin: 40px; }");

    }

    public void updateCtx(SpantusWorkInfo ctx, List<RecognitionResultDetails> results) {
        HTMLEditorKit kit = ((HTMLEditorKit)getJEditorPane().getEditorKit());
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("table{border-width: 1px;}");
        styleSheet.addRule("table{border-style: solid;}");
        styleSheet.addRule("table{border-color: gray;}");
        styleSheet.addRule("table{border-spacing: 0x;}");
        styleSheet.addRule("th {border-width: 1px;}");
        styleSheet.addRule("th {border-style: solid;}");
        styleSheet.addRule("th {border-color: gray;}");
        styleSheet.addRule("th {padding: 5px;}");
        styleSheet.addRule("td {border-width: 1px;}");
        styleSheet.addRule("td {border-style: solid;}");
        styleSheet.addRule("td {border-color: gray;}");
        styleSheet.addRule("td {padding: 5px;}");

        Document doc = kit.createDefaultDocument();
        getJEditorPane().setDocument(doc);
        getJEditorPane().setText("<html><body><h2>" + "Resutls" +"</h2>"
                + "<p>" + representResults(results) + "</p></body></html>");
        super.getJEditorPane().setCaretPosition(0);

        this.results = results;

    }

    private StringBuilder representResults(List<RecognitionResultDetails> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"resultTable\">");
        sb.append("<tr><th>").append("Label").append("</th><th>")
                .append("Total Score").append("</th><th>Feature</th><th>Feature Score</th></tr>");
        for (RecognitionResultDetails recognitionResult : results) {
            StringBuilder subTable = new StringBuilder();
            int rowsize=1;
            if(recognitionResult.getInfo().getId().equals(selctedId)){
                rowsize = recognitionResult.getScores().size()+1;
                for (Entry<String, Float> scoreEntry : recognitionResult.getScores().entrySet()) {
                    subTable.append("<tr>");
                    subTable.append("<td>").
                            append(scoreEntry.getKey()).
                    append("</td><td>").
                    append(scoreEntry.getValue()).
                    append("</td>");

                    subTable.append("</tr>");
                }
            }

            sb.append("<tr>");
            sb.append("<td ROWSPAN=").append(rowsize).append(">").
                    append("<a href=\"").append(recognitionResult.getInfo().getId()).append("\">").
                    append(recognitionResult.getInfo().getName()).append("</a></td>");
            sb.append("<td ROWSPAN=").append(rowsize).append(">").
                    append(recognitionResult.getDistance()).append("</td>");
            if(rowsize==1){
                sb.append("<td>").append("</td>");
                sb.append("<td>").append("</td>");
            }
            sb.append("</tr>");
            sb.append(subTable);
            
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
        /**
         * 
         * @param g
         * @param points
         */
        protected void drawPaths(Graphics2D g, List<Point> points) {
            int[] xArr = new int[points.size()];
            int[] yArr = new int[points.size()];
            int i = 0;
            for (Point p : points) {
                xArr[i] = p.x;
                yArr[i] = p.y;
                i++;
            }
            g.drawPolyline(xArr, yArr, xArr.length);
        }
        /**
         *
         * @param e
         */
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                StringTokenizer st = new StringTokenizer(e.getDescription(), " ");
                if (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    Long key = Long.valueOf(s);
                    selctedId=key;
                    updateCtx(null, results);
                    for (RecognitionResultDetails recognitionResultDetails : results) {
                        if (recognitionResultDetails.getInfo().getId().equals(key)) {

                            Graphics2D g = (Graphics2D) getjLabel().getGraphics();
                            g.setColor(Color.white);
                            g.fillRect(0, 0, getjLabel().getHeight(), getjLabel().getWidth());
                            g.setColor(Color.red);

                            for (Entry<String,List<Point>> detail : recognitionResultDetails.getPath().entrySet()) {
                                drawPaths(g, detail.getValue());
                            }

                            break;
                        }
                    }
                }

            }
        }
    }
}
