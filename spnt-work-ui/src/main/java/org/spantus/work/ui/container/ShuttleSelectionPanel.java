package org.spantus.work.ui.container;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionListener;

import org.spantus.ui.ModelEntry;
import org.spantus.ui.ModelEntryByNameComparator;
import org.spantus.work.ui.i18n.I18nFactory;
 
public class ShuttleSelectionPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

	private static final String ADD_BUTTON_LABEL = "shuttleAddButton";

	private static final String REMOVE_BUTTON_LABEL = "shuttleRemoveButton";

	private static final String DEFAULT_SOURCE_CHOICE_LABEL = "shuttleAvailableChoises";

	private static final String DEFAULT_DEST_CHOICE_LABEL = "shuttleSelectedChoises";

	private JLabel sourceLabel;

	private JList sourceList;

	private SortedListModel sourceListModel;
 
	private JList destList;

	private SortedListModel destListModel;

	private JLabel destLabel;

	private JButton addButton;

	private JButton removeButton;

	public ShuttleSelectionPanel() {
		initScreen();
	}

	public String getSourceChoicesTitle() {
		return sourceLabel.getText();
	}

	public void setSourceChoicesTitle(String newValue) {
		sourceLabel.setText(newValue);
	}

	public String getDestinationChoicesTitle() {
		return destLabel.getText();
	}

	public void setDestinationChoicesTitle(String newValue) {
		destLabel.setText(newValue);
	}

	public void clearSourceListModel() {
		sourceListModel.clear();
	}

	public void clearDestinationListModel() {
		destListModel.clear();
	}

	public void addSourceElements(ListModel newValue) {
		fillListModel(sourceListModel, newValue);
	}

	public void setSourceElements(ListModel newValue) {
		clearSourceListModel();
		addSourceElements(newValue);
	}

	public void addDestinationElements(ListModel newValue) {
		fillListModel(destListModel, newValue);
	}

	private void fillListModel(SortedListModel model, ListModel newValues) {
		int size = newValues.getSize();
		for (int i = 0; i < size; i++) {
			model.add((ModelEntry)newValues.getElementAt(i));
		}
	}

	public void addSourceElements(Object newValue[]) {
		fillListModel(sourceListModel, newValue);
	}
	
	public void addSourceElement(ModelEntry modelEntry) {
		modelEntry.setOrder(0);
		sourceListModel.add(modelEntry);
	}


	public void setSourceElements(Object newValue[]) {
		clearSourceListModel();
		addSourceElements(newValue);
	}

	public void addDestinationElements(Object[] newValue) {
		fillListModel(destListModel, newValue);
	}
	
	public void addDestinationElement(ModelEntry modelEntry) {
		modelEntry.setOrder(destListModel.getSize());
		destListModel.add(modelEntry);
	}

	private void fillListModel(SortedListModel model, Object newValues[]) {
		for (Object object : newValues) {
			if(object instanceof ModelEntry){
				model.add((ModelEntry)object);
			}else {
				ModelEntry modelEntry =new ModelEntry(object.toString(),object);
				model.add(modelEntry);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Iterator sourceIterator() {
		return sourceListModel.iterator();
	}

	@SuppressWarnings("unchecked")
	public Iterator destinationIterator() {
		return destListModel.iterator();
	}

	public void setSourceCellRenderer(ListCellRenderer newValue) {
		sourceList.setCellRenderer(newValue);
	}

	public ListCellRenderer getSourceCellRenderer() {
		return sourceList.getCellRenderer();
	}

	public void setDestinationCellRenderer(ListCellRenderer newValue) {
		destList.setCellRenderer(newValue);
	}

	public ListCellRenderer getDestinationCellRenderer() {
		return destList.getCellRenderer();
	}

	public void setVisibleRowCount(int newValue) {
		sourceList.setVisibleRowCount(newValue);
		destList.setVisibleRowCount(newValue);
	}

	public int getVisibleRowCount() {
		return sourceList.getVisibleRowCount();
	}

	public void setSelectionBackground(Color newValue) {
		sourceList.setSelectionBackground(newValue);
		destList.setSelectionBackground(newValue);
	}

	public Color getSelectionBackground() {
		return sourceList.getSelectionBackground();
	}

	public void setSelectionForeground(Color newValue) {
		sourceList.setSelectionForeground(newValue);
		destList.setSelectionForeground(newValue);
	}

	public Color getSelectionForeground() {
		return sourceList.getSelectionForeground();
	}

	private void clearSourceSelected() {
//		Object selected[] = sourceList.getSelectedValues();
//		for (int i = selected.length - 1; i >= 0; --i) {
//			sourceListModel.removeElement(selected[i]);
//		}
		sourceList.getSelectionModel().clearSelection();
	}

	private void clearDestinationSelected() {
		Object selected[] = destList.getSelectedValues();
		for (int i = selected.length - 1; i >= 0; --i) {
			destListModel.removeElement(selected[i]);
		}
		destList.getSelectionModel().clearSelection();
	}

	private void initScreen() {
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		sourceLabel = new JLabel(getMessage(DEFAULT_SOURCE_CHOICE_LABEL));
		//source list should be sorting by name
		sourceListModel = new SortedListModel(
				new TreeSet<ModelEntry>(new ModelEntryByNameComparator())
				);
		sourceList = new JList(sourceListModel);
		add(sourceLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
		add(new JScrollPane(sourceList), new GridBagConstraints(0, 1, 1, 5, .5,
				1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				EMPTY_INSETS, 0, 0));

		addButton = new JButton(getMessage(getMessage(ADD_BUTTON_LABEL)));
		add(addButton, new GridBagConstraints(1, 2, 1, 2, 0, .25,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
		addButton.addActionListener(new AddListener());
		removeButton = new JButton(getMessage(REMOVE_BUTTON_LABEL));
		add(removeButton, new GridBagConstraints(1, 4, 1, 2, 0, .25,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 5, 0, 5), 0, 0));
		removeButton.addActionListener(new RemoveListener());

		destLabel = new JLabel(getMessage(DEFAULT_DEST_CHOICE_LABEL));
		destListModel = new SortedListModel();
		destList = new JList(destListModel);
		add(destLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				EMPTY_INSETS, 0, 0));
		add(new JScrollPane(destList), new GridBagConstraints(2, 1, 1, 5, .5,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				EMPTY_INSETS, 0, 0));
	}


	private class AddListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object selected[] = sourceList.getSelectedValues();
			addDestinationElements(selected);
			clearSourceSelected();
			numerizeDestination(selected);
		}
	}

	private class RemoveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object selected[] = destList.getSelectedValues();
			addSourceElements(selected);
			clearDestinationSelected();
			numerizeSource(selected);
		}
	}
	
	public JList getDestList(){
		return destList;
	}

	public SortedListModel getSourceListModel() {
		return sourceListModel;
	}

	public SortedListModel getDestListModel() {
		return destListModel;
	}
	
//	void numerizeDestination(Iterable<Object> iter){
//		int size = destListModel.getSize();
//		int i = 0;
//		for (Object object : iter) {
//			if(object instanceof ModelEntry){
//				((ModelEntry)object).setOrder(size + (i++));
//			}
//		}
//	}
	void numerizeDestination(Object[] iter){
		int size = destListModel.getSize();
		int i = 0;
		for (Object object : iter) {
			if(object instanceof ModelEntry){
				((ModelEntry)object).setOrder(size + (i++));
			}
		}
	}
//	void numerizeSource(Iterable<Object> iter){
//		for (Object object : iter) {
//			if(object instanceof ModelEntry){
//				((ModelEntry)object).setOrder(0);
//			}
//		}
//	}
	void numerizeSource(Object[] iter){
		for (Object object : iter) {
			if(object instanceof ModelEntry){
				((ModelEntry)object).setOrder(0);
			}
		}
	}
	
	public void addListSelectionListener(ListSelectionListener listSelectionListener ){
		destList.getSelectionModel().addListSelectionListener(listSelectionListener);
	}
	
	private static String getMessage(String key){
		return I18nFactory.createI18n().getMessage(key);
	}
	
	public static void main(String args[]) {
		JFrame f = new JFrame("Dual List Box Tester");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ShuttleSelectionPanel dual = new ShuttleSelectionPanel();
		dual.addSourceElements(new String[] { "One", "Two", "Three" });
		dual.addSourceElements(new String[] { "Four", "Five", "Six" });
		dual.addSourceElements(new String[] { "Seven", "Eight", "Nine" });
		dual.addSourceElements(new String[] { "Ten", "Eleven", "Twelve" });
		dual
				.addSourceElements(new String[] { "Thirteen", "Fourteen",
						"Fifteen" });
		dual.addSourceElements(new String[] { "Sixteen", "Seventeen",
				"Eighteen" });
		dual.addSourceElements(new String[] { "Nineteen", "Twenty", "Thirty" });
		f.getContentPane().add(dual, BorderLayout.CENTER);
		f.setSize(400, 300);
		f.setVisible(true);
	}
}

