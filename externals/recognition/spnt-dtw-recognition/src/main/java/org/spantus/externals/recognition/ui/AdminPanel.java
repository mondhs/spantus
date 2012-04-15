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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import org.spantus.core.beans.SignalSegment;
import org.spantus.core.service.CorpusRepository;
import org.spantus.core.wav.AudioManagerFactory;
import org.spantus.externals.recognition.bean.CorpusFileEntry;
import org.spantus.externals.recognition.corpus.CorpusRepositoryFileImpl;
import org.spantus.logger.Logger;

public class AdminPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable table;
	private CorpusEntryTableModel model;
        private Logger log = Logger.getLogger(AdminPanel.class);

	private CorpusRepository corpusRepository;
	
	private JToolBar toolbar;
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
                        model = new CorpusEntryTableModel(getCorpusRepository());
			table = new JTable(model);
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
                        JButton deleteBtn = new JButton("delete");
			deleteBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					delete();					
				}
			});
                        JButton refreshBtn = new JButton("refresh");
			refreshBtn.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					refresh();					
				}

			});
			toolbar.add(saveCloseBtn);
			toolbar.add(playBtn);
                        toolbar.add(deleteBtn);
                        toolbar.add(refreshBtn);
                        
		}
		return toolbar;
		
	}
	
	public void play(){
		if(getTable().getSelectedRow()>=0){
                        
			SignalSegment entry = model.getCorpusEntry(getTable().getSelectedRow());
                        if(entry instanceof CorpusFileEntry){
                            CorpusFileEntry fileEntry = (CorpusFileEntry)entry;
                            if(fileEntry != null && fileEntry.getWavFile().exists()){
                                    try {
                                            AudioManagerFactory.createAudioManager().play(fileEntry.getWavFile().toURI().toURL());
                                    } catch (MalformedURLException e) {
                                            log.error(e);
                                    }
                            }else{
                                    log.debug("File not exists " + fileEntry.getWavFile());
                            }
                        }
		}
	}
	
	private void save(){
                 log.debug("save all"); 
                 model.saveAll();
	}
	
        private void delete() {
            if (getTable().getSelectedRow() >= 0) {
                model.delete(getTable().getSelectedRow());
            }
            table.revalidate();
        }
         
        private void refresh() {
            model.refresh();
            table.revalidate();
        }
        
        public void setRepositoryPath(String dirPath){
            ((CorpusRepositoryFileImpl)getCorpusRepository()).setRepositoryPath(dirPath);
            refresh();
        }
        
	public CorpusRepository getCorpusRepository() {
		if(corpusRepository == null){
			corpusRepository = new CorpusRepositoryFileImpl();
		}
		return corpusRepository;
	}
	
}
