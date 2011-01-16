/*
Copyright (c) 2009 Mindaugas Greibus (spantus@gmail.com)
Part of program for analyze speech signal
http://spantus.sourceforge.net

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.spantus.externals.recognition.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.spantus.core.beans.I18n;
import org.spantus.core.marker.Marker;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.externals.recognition.bean.RecognitionResultDetails;
import org.spantus.logger.Logger;
import org.spantus.ui.SwingUtils;
import org.spantus.work.ui.ImageResourcesEnum;

/**
 *
 * @author mondhs
 */
public class RecognizeDetailDialog extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<RecognitionResultDetails> results;
    
    private Long selctedSampleId = null;
    private String selctedFeatureId = null;

    Logger log = Logger.getLogger(RecognizeDetailDialog.class);

    private JPanel jContentPane = null;
    private JPanel mainPanel = null;
    private JScrollPane resultScrollPane = null;
    private JEditorPane resultPane;
    private DtwChartPanel chartPanel = null;
    private I18n i18n;
    private URL targetWavURL;
    private Marker targetMarker;


    /**
     * @param owner
     */
    public RecognizeDetailDialog(Frame owner, I18n i18n) {
        super(owner);
        this.i18n = i18n;
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(SwingUtils.currentWindowSize(0.75, 0.75));
        SwingUtils.centerWindow(this);
        setTitle(getI18n().getMessage("RecognitionResults"));
        this.setContentPane(getJContentPane());
    }

    @Override
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane thisRootPane = super.createRootPane();
        thisRootPane.registerKeyboardAction(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        }, stroke,
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return thisRootPane;
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getMainPanel(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getResultScrollPane(), BorderLayout.CENTER);


            mainPanel.add(getChartPanel(), BorderLayout.EAST);
        }
        return mainPanel;
    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getResultScrollPane() {
        if (resultScrollPane == null) {
            resultScrollPane = new JScrollPane();
            resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            resultScrollPane.setViewportView(getResultPane());
        }
        return resultScrollPane;
    }

    protected DtwChartPanel getChartPanel() {
        if (chartPanel == null) {
            chartPanel = new DtwChartPanel();
        }
        
        return chartPanel;
    }

    /**
     * This method initializes jEditorPane
     *
     * @return javax.swing.JEditorPane
     */
    protected JEditorPane getResultPane() {
        if (resultPane == null) {
            HTMLEditorKit kit = new HTMLEditorKit();
            resultPane = new JEditorPane();
            resultPane.setEditable(false);
            resultPane.setContentType("text/html");
            resultPane.addHyperlinkListener(new RecognitionHyperlinkListener());
            resultPane.setCaretPosition(0);
            resultPane.setEditorKit(kit);
            StyleSheet styleSheet = kit.getStyleSheet();
            styleSheet.addRule("table{border-width: 1px;}");
            styleSheet.addRule("table{border-style: solid;}");
            styleSheet.addRule("table{border-color: gray;}");
            styleSheet.addRule("table{border-spacing: 0x;}");
            styleSheet.addRule("table{width: 100%;}");
            styleSheet.addRule("th {border-width: 1px;}");
            styleSheet.addRule("th {border-style: solid;}");
            styleSheet.addRule("th {border-color: gray;}");
            styleSheet.addRule("th {padding: 5px;}");
            styleSheet.addRule("td {border-width: 1px;}");
            styleSheet.addRule("td {border-style: solid;}");
            styleSheet.addRule("td {border-color: gray;}");
            styleSheet.addRule("td {padding: 5px;}");
            styleSheet.addRule("div {width: 100%;}");
            styleSheet.addRule("div {position: absolute;}");
            styleSheet.addRule("div {text-align: center;}");
            styleSheet.addRule("div {padding: 5px;}");

        }
        return resultPane;
    }

    public void updateCtx(List<RecognitionResultDetails> results) {
        
        
        HTMLEditorKit kit = ((HTMLEditorKit) getResultPane().getEditorKit());
//        StyleSheet styleSheet = kit.getStyleSheet();


        Document doc = kit.createDefaultDocument();
        getResultPane().setDocument(doc);
        getResultPane().setText("<html><body>"
                + "<p>" + representResults(results) + "</p></body></html>");
        getResultPane().setCaretPosition(0);

        this.results = results;

    }

    private StringBuilder representEmptyResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div>").append("No recognition pattern is found. Try to learn program some patterns first.").append("</div>");
        return sb;
    }

    private StringBuilder representResults(List<RecognitionResultDetails> results) {
        StringBuilder sb = new StringBuilder();
        if (results.isEmpty()) {
            return representEmptyResults();
        }
        String playImgSrc = getClass().getClassLoader().getResource(ImageResourcesEnum.play.getCode())
                    .toString();

         sb.
            append(html("Target: <a href=\"play={0,number,#}\">", -1)).
            append(html("<img src=\"{0}\" alt=\"play\" border=\"0\" width=\"24\" height=\"24\" />", playImgSrc)).
            append("</a>");
        sb.append("<table class=\"resultTable\">");
        sb.append("<tr><th>").append("Sample Label").append("</th><th>").
                append("Total Score").
                append("</th><th>Feature</th><th>Feature Score</th></tr>");
        for (RecognitionResultDetails recognitionResult : results) {
            StringBuilder subTable = new StringBuilder();
            int rowsSize = 1;
            if (recognitionResult.getInfo().getId().equals(selctedSampleId)) {
                rowsSize = recognitionResult.getScores().size() + 1;
                for (Entry<String, Float> scoreEntry : recognitionResult.getScores().entrySet()) {
                    subTable.append("<tr>");
                    subTable.append("<td>").
                            append(html("<a href=\"show={0}\">",  scoreEntry.getKey())).
                            append(getI18n().getMessage(scoreEntry.getKey())).
                            append("</a>").
                            append("</td><td>").
                            append(getI18n().getDecimalFormat().format(scoreEntry.getValue())).
                            append("</td>");

                    subTable.append("</tr>");
                }
            }

            sb.append("<tr>");
            sb.append(html("<td ROWSPAN=\"{0}\">",rowsSize));
            


            sb.
                    append(html("<a href=\"play={0,number,#}\">",recognitionResult.getInfo().getId())).
                    append(html("<img src=\"{0}\" alt=\"play\" border=\"0\" width=\"24\" height=\"24\" />", playImgSrc)).
                    append("</a>").
                    append(html("<a href=\"show={0,number,#}\">",  recognitionResult.getInfo().getId()));
            if(recognitionResult.getInfo().getId().equals(selctedSampleId)){
                //collapsed +
                sb.append("&#8863;");
            }else{
                //expanded -
                sb.append("&#8862;");
            }
            sb.append(recognitionResult.getInfo().getName()).append("</a>");
            //show expanded id
            if(recognitionResult.getInfo().getId().equals(selctedSampleId)){
            	sb.append("<span>[id=").append(recognitionResult.getInfo().getId()).append("]</span>");
            }
            sb.append("</td>");
            sb.append(html("<td ROWSPAN=\"{0}\">",rowsSize)).
                    append(getI18n().getDecimalFormat().format(
                            recognitionResult.getDistance())).
                     append("</td>");
            //how features are generated
            if (rowsSize == 1) {
                sb.append("<td>").append("</td>");
                sb.append("<td>").append("</td>");
            }
            sb.append("</tr>");
            sb.append(subTable);

        }

        sb.append("</table>");
        return sb;
    }

    protected String html(String patter, Object... args){
        return MessageFormat.format(patter, args);
    }

    @Override
    public void dispose() {
        super.dispose();
        selctedFeatureId = null;
        selctedSampleId = null;
        results = null;
    }



    public I18n getI18n() {
        return i18n;
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }
    
    public Marker getTargetMarker() {
        return targetMarker;
    }

    public void setTargetMarker(Marker targetMarker) {
        this.targetMarker = targetMarker;
    }

    public URL getTargetWavURL() {
        return targetWavURL;
    }

    public void setTargetWavURL(URL targetWavURL) {
        this.targetWavURL = targetWavURL;
    }
    protected void showResult(String id) {
        //check if number that mean sample id
        if (Pattern.matches("^\\d*$", id)) {
            Long key = Long.valueOf(id);
            if (key.equals(selctedSampleId)) {
                selctedSampleId = null;
            } else {
                selctedSampleId = key;
            }
            selctedFeatureId = null;
        } else {
            //if this not a number lets say is feature id
            selctedFeatureId = id;
        }
        updateCtx(results);
        if (selctedSampleId == null) {
            getChartPanel().setRecognitionResult(null);
            getChartPanel().repaint();
            return;
        }
        for (RecognitionResultDetails recognitionResultDetails : results) {
            if (recognitionResultDetails.getInfo().getId().equals(selctedSampleId)) {
                List<Point> points = recognitionResultDetails.getPath().get(selctedFeatureId);
                if (points != null) {
                    //if some feature selected paint only this feature
                    getChartPanel().setRecognitionResult(recognitionResultDetails, selctedFeatureId);
                } else {
                    //if none feature is selected paint all features
                    getChartPanel().setRecognitionResult(recognitionResultDetails);
                }
                break;
            }
        }
        getChartPanel().repaint();
    }

    protected void playResult(String id){
        Long lid = Long.valueOf(id);
        if(lid == -1){
            AudioManagerFactory.createAudioManager().play(
                            getTargetWavURL(),  
                            (getTargetMarker().getStart().floatValue()/1000),
                            (getTargetMarker().getLength().floatValue()/1000)
                            );
        }
        for (RecognitionResultDetails recognitionResultDetails : results) {
            if (recognitionResultDetails.getInfo().getId().equals(
                    lid)) {
                try {
                    AudioManagerFactory.createAudioManager().play(
                            (new File(recognitionResultDetails.getAudioFilePath()
                            ).toURI().toURL()));
                    break;
                } catch (MalformedURLException ex) {
                    log.error(ex);
                }
            }
        } 
    }


    class RecognitionHyperlinkListener implements HyperlinkListener {

        /**
         *
         * @param e
         */
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                StringTokenizer st = new StringTokenizer(e.getDescription(), " ");
                if (st.hasMoreTokens()) {
                    String selectedID = st.nextToken();
                   
                    if(selectedID.startsWith("play=")){
                        selectedID = selectedID.replace("play=","");
                        playResult(selectedID);
                    }else if(selectedID.startsWith("show=")){
                        selectedID = selectedID.replace("show=", "");
                        showResult(selectedID);
                    }

                    
                }

            }
        }
    }
}
