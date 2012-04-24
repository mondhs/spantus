package org.spantus.android.activity;

import org.spantus.android.R;
import org.spantus.android.SpantusApplication;
import org.spantus.android.audio.PlayUrlTask;
import org.spantus.android.service.SpantusApiServiceImpl;
import org.spantus.android.ui.admin.SpntAdminContext;
import org.spantus.logger.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CorpusItemEditActivity extends Activity {
	private static final Logger LOG = Logger.getLogger(CorpusItemEditActivity.class);

	private SpntAdminContext ctx;
	private PlayUrlTask audioOut;
	private SpantusApiServiceImpl spantusApiService;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.corpus_item_edit);
		// ctx = (SpntAdminContext) getIntent().getExtras().getSerializable(
		// AdminActivity.CTX_KEY_NAME);
		SpantusApplication appState = ((SpantusApplication) getApplicationContext());
		ctx = appState.getSpntAdminContext();

//		TextView entryId = (TextView) findViewById(R.id.entryIdOut);
		TextView fileNameOut = (TextView) findViewById(R.id.fileNameOut);
		TextView entryCreationDateOut = (TextView) findViewById(R.id.entryCreationDateOut);
		TextView entryFileSizeOut = (TextView) findViewById(R.id.entryFileSizeOut);
		TextView entryDescriptionInput = (TextView) findViewById(R.id.entryDescriptionInput);

		entryFileSizeOut.setText(""+ctx.getCorpusItem().getFileSize());
//		entryId.setText(ctx.getCorpusItem().getId());
		fileNameOut.setText(ctx.getCorpusItem().getFileName());
		entryDescriptionInput.setText(ctx.getCorpusItem().getDescription());
		entryCreationDateOut.setText(SpantusApiServiceImpl.DATE_FORMAT
				.format(ctx.getCorpusItem().getCreated()));
	}

	public void onBackBtn(View target) {
		this.finish();
	}
	
	public void onViewBtn(View view) {
		try {
			Uri fileName = getSpantusApiService().createPlaybackUri(
					getCtx().getCorpusItem().getFileName());
			
//			Intent intent = new Intent(view.getContext(),
//					CorpusItemEditActivity.class);
			
            Intent intent = new Intent(Intent.ACTION_EDIT,fileName
            		);
//            intent.putExtra("was_get_content_intent",
//                    mWasGetContentIntent);
            intent.setClassName(
            		view.getContext(),
            ViewAudioFormActivity.class.getCanonicalName());
            final int REQUEST_CODE_EDIT = 1;
            startActivityForResult(intent, REQUEST_CODE_EDIT);
        } catch (Exception e) {
            LOG.error(e);
        }
	}

	public void onDeleteBtn(View target) {
		ctx.setDeletedKey(getCtx().getCorpusItem().getId());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete " + ctx.getDeletedKey() + "?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								getSpantusApiService().delete(ctx.getDeletedKey());
								CorpusItemEditActivity.this.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						ctx.setDeletedKey(null);
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

		
		
	}
	public void onUpdateBtn(View playBtn) {
		TextView entryDescriptionInput = (TextView) findViewById(R.id.entryDescriptionInput);
		getSpantusApiService().udpate(getCtx().getCorpusItem().getId(), ""+entryDescriptionInput.getText());
		this.finish();
	}

	public void onPlayBtn(View playBtn) {
		if (audioOut == null || !audioOut.isPlaing()) {
			playBtn.setEnabled(false);
			AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioOut = new PlayUrlTask(audioManager);
			audioOut.execute(getSpantusApiService().createPlaybackUrl(
					getCtx().getCorpusItem().getFileName()));
			playBtn.setEnabled(true);
		} else {
			audioOut.cancel(true);
		}
	}

	public SpntAdminContext getCtx() {
		return ctx;
	}

	public void setCtx(SpntAdminContext ctx) {
		this.ctx = ctx;
	}

	public SpantusApiServiceImpl getSpantusApiService() {
		if (spantusApiService == null) {
			spantusApiService = new SpantusApiServiceImpl();
			spantusApiService.setSpantusServerApi(getCtx()
					.getSpantusServerApi());
		}
		return spantusApiService;
	}

	public void setSpantusApiService(SpantusApiServiceImpl spantusApiService) {
		this.spantusApiService = spantusApiService;
	}

}
