package org.spantus.android.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.spantus.android.R;
import org.spantus.android.SpantusApplication;
import org.spantus.android.dto.CorpusItem;
import org.spantus.android.service.SpantusApiServiceImpl;
import org.spantus.android.ui.admin.SpntAdminContext;
import org.spantus.android.ui.admin.listener.UpdateCorpusInfoClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AdminActivity extends Activity {

	public static final String CTX_KEY_NAME = "ctx";
	List<CorpusItem> listItems = new ArrayList<CorpusItem>();
	ArrayAdapter<CorpusItem> adapter;
	SpntAdminContext ctx = null;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.admin);

		SpantusApplication appState = ((SpantusApplication)getApplicationContext());
		ctx = appState.getSpntAdminContext();
		if(ctx == null){
			ctx = new SpntAdminContext();
			appState.setSpntAdminContext(ctx);
		}
		

		ListView listView = (ListView) findViewById(R.id.list);
		adapter = new ArrayAdapter<CorpusItem>(
	            this,
	            android.R.layout.simple_list_item_2,
	            android.R.id.text1,
				listItems) {
	 
	            @Override
	            public View getView(int position, View convertView, ViewGroup parent) {
	 
	                // Must always return just a View.
	                View view = super.getView(position, convertView, parent);
	 
	                // If you look at the android.R.layout.simple_list_item_2 source, you'll see
	                // it's a TwoLineListItem with 2 TextViews - text1 and text2.
	                //TwoLineListItem listItem = (TwoLineListItem) view;
	                CorpusItem entry = listItems.get(position);
	                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
	                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
	                text1.setText(entry.getId());
	                text2.setText(SpantusApiServiceImpl.DATE_FORMAT.format(entry.getCreated()));
	                return view;
	            }
	        };
		UpdateCorpusInfoClickListener listener = new UpdateCorpusInfoClickListener(
				listItems, adapter, this, ctx);

		listView.setOnItemClickListener(listener);
		listView.setAdapter(adapter);

		Button updateCorpusInfoBtn = (Button) findViewById(R.id.retrieveCorpusInfoBtn);
		updateCorpusInfoBtn.setOnClickListener(listener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String deletedKey = ctx.getDeletedKey();
		ctx.setDeletedKey(null);
		if(deletedKey!= null){
			Iterator<CorpusItem> iter = listItems.iterator();
			for (; iter.hasNext();) {
				CorpusItem item = (CorpusItem) iter.next();
				if(deletedKey.equals(item.getId())){
					iter.remove();
					break;
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	public void onEditBtn(View view) {
		if(ctx.getCorpusItem() == null){
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Nothing to edit");
			alertDialog.setMessage("Corpus item not selected. " +
					"Please, click \"Retrieve Corpus Info\"," +
					" select one of the items and click Edit again");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			 
			    } }); 
			alertDialog.show();
			return;
		}
		Intent myIntent = new Intent(view.getContext(),
				CorpusItemEditActivity.class);
		myIntent.putExtra(CTX_KEY_NAME, ctx);
		startActivityForResult(myIntent, 0);
	}

	public void onBackBtn(View target) {
		this.finish();
	}
}
