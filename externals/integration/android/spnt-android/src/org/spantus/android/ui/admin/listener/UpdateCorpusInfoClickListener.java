package org.spantus.android.ui.admin.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spantus.android.activity.AdminActivity;
import org.spantus.android.activity.CorpusItemEditActivity;
import org.spantus.android.dto.CorpusItem;
import org.spantus.android.service.SpantusApiServiceImpl;
import org.spantus.android.ui.admin.SpntAdminContext;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TwoLineListItem;

public class UpdateCorpusInfoClickListener implements OnClickListener,
		OnItemClickListener {


	private List<CorpusItem> listItems;
	private Activity activity;
	private ArrayAdapter<CorpusItem> adapter;
	private Map<String, CorpusItem> itemsMap = new HashMap<String, CorpusItem>();
	private SpntAdminContext ctx;
	private SpantusApiServiceImpl spantusApiService;

	public UpdateCorpusInfoClickListener(List<CorpusItem> listItems,
			ArrayAdapter<CorpusItem> adapter, Activity activity, SpntAdminContext ctx) {
		this.listItems = listItems;
		this.activity = activity;
		this.adapter = adapter;
		this.ctx=ctx;
	}

	public void onClick(View v) {
		itemsMap = getSpantusApiService().findCorpusAllEntries();
		listItems.clear();
		listItems.addAll(itemsMap.values());
		adapter.notifyDataSetChanged();
	}

	/**
	 * 
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String key = (String) ((TwoLineListItem) view).getText1().getText();
		CorpusItem item = itemsMap.get(key);
//		logText.setText(item.getId());
		getCtx().setCorpusItem(item);
		
		Intent myIntent = new Intent(view.getContext(),
				CorpusItemEditActivity.class);
		myIntent.putExtra(AdminActivity.CTX_KEY_NAME, ctx);
		activity.startActivityForResult(myIntent, 0);
	}

	public SpantusApiServiceImpl getSpantusApiService() {
		if(spantusApiService == null){
			spantusApiService = new SpantusApiServiceImpl();
			spantusApiService.setSpantusServerApi(getCtx().getSpantusServerApi());
		}
		return spantusApiService;
	}

	public void setSpantusApiService(SpantusApiServiceImpl spantusApiService) {
		this.spantusApiService = spantusApiService;
	}

	public SpntAdminContext getCtx() {
		return ctx;
	}

	public void setCtx(SpntAdminContext ctx) {
		this.ctx = ctx;
	}

	

}
