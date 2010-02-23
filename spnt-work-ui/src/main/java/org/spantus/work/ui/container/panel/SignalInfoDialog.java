/*
 	Copyright (c) 2009, 2010 Mindaugas Greibus (spantus@gmail.com)
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
package org.spantus.work.ui.container.panel;

import java.awt.Frame;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JEditorPane;

import org.spantus.core.extractor.SignalFormat;
import org.spantus.utils.StringUtils;
import org.spantus.work.ui.container.SpantusWorkSwingUtils;
import org.spantus.work.ui.container.option.WindowOptionPnl;
import org.spantus.work.ui.dto.SpantusWorkInfo;
import org.spantus.work.ui.dto.WorkUIExtractorConfig;
import org.spantus.work.ui.i18n.I18nFactory;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * Created Feb 23, 2010
 *
 */
public class SignalInfoDialog extends SpantusAboutDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SpantusWorkInfo ctx;

	public SignalInfoDialog(Frame owner) {
		super(owner);
	}
	@Override
	protected void initialize() {
		super.initialize();
		this.setSize(SpantusWorkSwingUtils.currentWindowSize(0.25, 0.5));
		setTitle(I18nFactory.createI18n().getMessage("information"));
	}
	
	@Override
	protected JEditorPane getJEditorPane() {
		JEditorPane jEditorPane = super.getJEditorPane();
		StringBuilder sb = new StringBuilder(); 
		if(getCtx()!= null){
			sb.append(getCtx());
		}
		jEditorPane.setText("<html><body>"+
				getMessage(SignalInfoDialog.class.getName()+"."+"noInfo")+		
				"</body></html>");
		getjLabel().setIcon(null);
		return jEditorPane;
	}


	public SpantusWorkInfo getCtx() {
		return ctx;
	}


	public void setCtx(SpantusWorkInfo ctx) {
		StringBuilder sb = new StringBuilder();
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		if(ctx== null || ctx.getProject().getSample() == null
                        || ctx.getProject().getSample().getSignalFormat() == null
                        || ctx.getProject().getSample().getSignalFormat().getParameters() == null){
			return;
		}
                
    		SignalFormat signalFormat = ctx.getProject().getSample().getSignalFormat();
		info.putAll(signalFormat.getParameters());
		sb.append("<h1>").append(getClassMessage("signalHeader")).append("</h1>");
		for (Entry<String, Object> infoEntry : info.entrySet()) {
			sb.append("<div>");
			sb.append("<span class=\"label\">").append(getClassMessage(infoEntry.getKey())).append(":</spant>");	
			sb.append("<span class=\"value\">").append(infoEntry.getValue()).append("</spant>");
			sb.append("</div>");
		}
		
		sb.append("<h1>").append(getClassMessage("featureHeader")).append("</h1>");	
		
		for (String extr: ctx.getProject().getFeatureReader().getExtractors()) {
			String[] data = extr.split(":"); 
			sb.append("<div>");
			sb.append("<span class=\"label\">").append(getMessage(data[0])).append(":</spant>");	
			sb.append("<span class=\"value\">").append(getMessage(data[1])).append("</spant>");
			sb.append("</div>");
		}
		
		
		sb.append("<h1>").append(getClassMessage("parametersHeader")).append("</h1>");
		info = new LinkedHashMap<String, Object>();
		WorkUIExtractorConfig config = ctx.getProject().getFeatureReader().getWorkConfig();
		info.put("windowingType", WindowOptionPnl.PREFIX_windowing+config.getWindowingType());
		info.put("preemphasis", WindowOptionPnl.PREFIX_preemphasis+config.getPreemphasis());
		info.put("classifier", WindowOptionPnl.PREFIX_classifier+ctx.getProject().getClassifierType()); 
		info.put("segmentation", WindowOptionPnl.PREFIX_segmentation+config.getSegmentationServiceType()); 
		
		for (Entry<String, Object> infoEntry : info.entrySet()) {
			sb.append("<div>");
			sb.append("<span class=\"label\">").append(getMessage(infoEntry.getKey())).append(":</spant>");	
			sb.append("<span class=\"value\">").append(getMessage(""+infoEntry.getValue())).append("</spant>");
			sb.append("</div>");
		}
		sb.append("<div>");
		sb.append("<span class=\"label\">").append(getMessage("windowSize")).append(":</spant>");	
		sb.append("<span class=\"value\">").append(""+config.getWindowSize()).append("</spant>");
		sb.append("</div>");
		sb.append("<div>");
		sb.append("<span class=\"label\">").append(getMessage("windowOverlap")).append(":</spant>");	
		sb.append("<span class=\"value\">").append(""+config.getWindowOverlap()).append("</spant>");
		sb.append("</div>");
		

		
		String css = "<head><style type=\"text/css\">" +
				".label{font-weight:bold}"+
				"</style></head>"; 
		super.getJEditorPane().setText("<html>"+css+"<body><p>" + sb + "</p></body></html>");
		super.getJEditorPane().setCaretPosition(0);
		this.ctx = ctx;
	}
	
	public String getClassMessage(String key){
		return getMessage(SignalInfoDialog.class.getName()+"."+key);
	}
	public String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}
}