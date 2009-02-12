package org.spantus.demo.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.spantus.demo.dto.DemoAppletInfo;
import org.spantus.demo.dto.ReaderDto;
import org.spantus.demo.i18n.I18nFactory;
import org.spantus.demo.services.ReadersEnum;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.mpeg7.Mpeg7ExtractorEnum;

public class DemoOptionReaderPnl extends JPanel {
	enum DemoOptionReaderLabels {
		readers, selectAll
	}

	private static final long serialVersionUID = 1L;
	private JComboBox readerCmb = null;
	private JPanel readerChoisePnl = null;
	private JLabel jLabel = null;
	private JPanel readerContentPnl = null;
	private JPanel mpeg7ReaderPnl = null;
	private JPanel commonReaderPnl = null;
	private JScrollPane jScrollPane = null;
	private Map<ReadersEnum, List<JCheckBox>> readersChoises = null;
	private JCheckBox sellectAll = null;
	private DemoAppletInfo info = null;

	public DemoAppletInfo getInfo() {
		if (info == null) {
			info = new DemoAppletInfo();
		}
		return info;
	}

	public void setInfo(DemoAppletInfo info) {
		this.info = info;
	}

	/**
	 * This is the default constructor
	 */
	public DemoOptionReaderPnl() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		this.add(getReaderChoisePnl(), BorderLayout.NORTH);
		this.add(getJScrollPane(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getReaderContentPnl());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getReaderCmb() {
		if (readerCmb == null) {
			readerCmb = new JComboBox(ReadersEnum.values());
			readerCmb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ReadersEnum reader = (ReadersEnum) ((JComboBox) e
							.getSource()).getSelectedItem();
					getInfo().getCurrentReader().setReader(reader);
					updateReaders(getInfo().getCurrentReader());
				}
			});
		}
		return readerCmb;
	}

	/**
	 * This method initializes readerChoisePnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getReaderChoisePnl() {
		if (readerChoisePnl == null) {
			jLabel = new JLabel();
			jLabel.setText(I18nFactory.createI18n()
					.getMessage(DemoOptionReaderLabels.readers.name()));
			readerChoisePnl = new JPanel();
			readerChoisePnl.setLayout(new BoxLayout(getReaderChoisePnl(),
					BoxLayout.X_AXIS));
			readerChoisePnl.add(jLabel, null);
			readerChoisePnl.add(getReaderCmb(), null);
			readerChoisePnl.add(getSellectAll(), null);
		}
		return readerChoisePnl;
	}

	/**
	 * This method initializes readerContentPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getReaderContentPnl() {
		if (readerContentPnl == null) {
			readerContentPnl = new JPanel();
			readerContentPnl.setLayout(new BoxLayout(getReaderContentPnl(),
					BoxLayout.Y_AXIS));
			readerContentPnl.add(getMpeg7ReaderPnl(), null);
			readerContentPnl.add(getCommonReaderPnl(), null);
		}
		return readerContentPnl;
	}

	/**
	 * This method initializes mpeg7ReaderPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMpeg7ReaderPnl() {
		if (mpeg7ReaderPnl == null) {
			mpeg7ReaderPnl = new JPanel();
			mpeg7ReaderPnl.setLayout(new BoxLayout(getMpeg7ReaderPnl(),
					BoxLayout.Y_AXIS));
			for (JCheckBox cb : getReadersChoises().get(ReadersEnum.mpeg7)) {
				mpeg7ReaderPnl.add(cb, null);
			}
		}
		return mpeg7ReaderPnl;
	}

	/**
	 * This method initializes commonReaderPnl
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCommonReaderPnl() {
		if (commonReaderPnl == null) {
			commonReaderPnl = new JPanel();
			commonReaderPnl.setLayout(new BoxLayout(getCommonReaderPnl(),
					BoxLayout.Y_AXIS));
			for (JCheckBox cb : getReadersChoises().get(ReadersEnum.common)) {
				commonReaderPnl.add(cb, null);
			}

		}
		return commonReaderPnl;
	}

	/**
	 * This method initializes extractors
	 * 
	 * @return List<javax.swing.JCheckBox>
	 */
	private Map<ReadersEnum, List<JCheckBox>> getReadersChoises() {
		if (readersChoises == null) {
			readersChoises = new HashMap<ReadersEnum, List<JCheckBox>>();
			List<JCheckBox> extractors = new ArrayList<JCheckBox>();
			for (ExtractorEnum extractor : ExtractorEnum.values()) {
				JCheckBox cb = new JCheckBox();
				cb.setText(I18nFactory.createI18n()
						.getMessage(extractor.name()));
				cb.setName(extractor.name());
				extractors.add(cb);
			}
			readersChoises.put(ReadersEnum.common, extractors);
			extractors = new ArrayList<JCheckBox>();
			for (Mpeg7ExtractorEnum extractor : Mpeg7ExtractorEnum.values()) {
				JCheckBox cb = new JCheckBox();
				cb.setText(I18nFactory.createI18n()
						.getMessage(extractor.name()));
				cb.setName(extractor.name());
				extractors.add(cb);
			}
			readersChoises.put(ReadersEnum.mpeg7, extractors);
		}
		return readersChoises;
	}

	protected void onShow() {
		getReaderCmb()
				.setSelectedItem(getInfo().getCurrentReader().getReader());
		updateReaders(getInfo().getCurrentReader());
	}

	protected void updateReaders(ReaderDto readerInfo) {
		getSellectAll().setSelected(false);
		switch (readerInfo.getReader()) {
		case common:
			getCommonReaderPnl().setVisible(true);
			getMpeg7ReaderPnl().setVisible(false);
			break;
		case mpeg7:
			getCommonReaderPnl().setVisible(false);
			getMpeg7ReaderPnl().setVisible(true);
			break;
		default:
			throw new RuntimeException("not impl: " + readerInfo.getReader());
		}
		for (JCheckBox cb : getReadersChoises().get(readerInfo.getReader())) {
			cb.setSelected(readerInfo.getExtractors().contains(cb.getName()));
		}

	}

	public void read() {
		 getInfo().getCurrentReader().getExtractors().clear();
		for (JCheckBox cb : getReadersChoises().get(getInfo().getCurrentReader().getReader())) {
			if (cb.isSelected()) {
				getInfo().getCurrentReader().getExtractors().add(
						cb.getName());
			}
		}
	}

	private JCheckBox getSellectAll() {
		if(sellectAll == null){
			sellectAll = new JCheckBox();
			sellectAll.setText(I18nFactory.createI18n()
					.getMessage(DemoOptionReaderLabels.selectAll.name()));
			sellectAll.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					
					for (JCheckBox cb : getReadersChoises().get(getInfo().getCurrentReader().getReader())) {
						cb.setSelected(((JCheckBox)e.getSource()).isSelected());
					}
				}});
		}
		return sellectAll;
	}

}
