package org.spnt.recognition.dtw.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import org.spantus.logger.Logger;
import org.spantus.work.wav.AudioManagerFactory;
import org.spnt.recognition.bean.CorpusFileEntry;
import org.spnt.recognition.corpus.CorpusRepositoryFileImpl;

public class AdminPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private Logger log = Logger.getLogger(AdminPanel.class);

	private CorpusRepositoryFileImpl corpusRepository;
	
	private JToolBar toolbar;
	List<CorpusFileEntry> corpusFileEntries;
	/**
	 * @param owner
	 */
	public AdminPanel() {
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		corpusFileEntries = getCorpusRepository().findAllFileEntries();
		this.setSize(640, 480);
//		this.setSize(SpantusWorkSwingUtils.currentWindowSize(0.5, 0.25));
//		SpantusWorkSwingUtils.centerWindow(this);
//		this.setContentPane(getJContentPane());
		setLayout(new BorderLayout());
	     //Add the scroll pane to this panel.
		add(new JScrollPane(getTable()), BorderLayout.CENTER);
		add(getToolbar(), BorderLayout.NORTH);
	}
	
	private JTable getTable() {
		if (table == null) {
			table = new JTable(new CorpusEntryTableModel(corpusFileEntries));
			table.setPreferredScrollableViewportSize(new Dimension(500, 70));
//			table.setFillsViewportHeight(true);
		}
		return table;
	}

	private JToolBar getToolbar(){
		if(toolbar == null){
			toolbar = new JToolBar();
			toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 2));
			JButton playBtn = new JButton("play");
			playBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					play();					
				}
			});
			JButton saveCloseBtn = new JButton("save");
			saveCloseBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					save();					
				}
			});
			toolbar.add(saveCloseBtn);
			toolbar.add(playBtn);
		}
		return toolbar;
		
	}
	
	public void play(){
		if(getTable().getSelectedRow()>=0){
			CorpusFileEntry entry = corpusFileEntries.get(getTable().getSelectedRow());
			if(entry.getWavFile().exists()){
				try {
					AudioManagerFactory.createAudioManager().play(entry.getWavFile().toURI().toURL());
				} catch (MalformedURLException e) {
					log.error(e);
				}
			}
		}
	}
	
	public void save(){
		for (CorpusFileEntry entry : corpusFileEntries) {
			getCorpusRepository().update(entry);
		}
	}
	
	public CorpusRepositoryFileImpl getCorpusRepository() {
		if(corpusRepository == null){
			corpusRepository = new CorpusRepositoryFileImpl();
		}
		return corpusRepository;
	}
	
}
