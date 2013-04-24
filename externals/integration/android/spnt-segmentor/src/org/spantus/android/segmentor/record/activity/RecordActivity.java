package org.spantus.android.segmentor.record.activity;

import java.text.MessageFormat;
import java.util.Deque;
import java.util.LinkedList;

import org.spantus.android.segmentor.record.entity.ExtractorReaderCtx;
import org.spantus.android.segmentor.record.entity.RecordState;
import org.spantus.android.segmentor.record.entity.WindowMinMax;
import org.spantus.android.segmentor.services.AndroidExtractorsFactory;
import org.spantus.android.segmentor.services.impl.RecordServiceImpl;
import org.spantus.android.segmentor.view.AudioView;
import org.spantus.android.segmentor.R;
import org.spantus.core.io.BaseWraperExtractorReader;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.logger.Logger;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RecordActivity extends Activity {

	public static final int sampleRate = 8000;
	public static final int bufferSizeFactor = 1;
	private static final Logger LOG = Logger
			.getLogger(RecordServiceImpl.class);

	private ProgressBar mLevel;
	private AudioView mAudioView;

	private Handler handler = new Handler();
	private int mLastLevel;
	private TextView mInfoOut;

	public AndroidExtractorsFactory factory() {
		return AndroidExtractorsFactory.getFactory();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		LOG.debug("[onCreate]");
		
		mAudioView = (AudioView) findViewById(R.id.audioview);

		mLevel = (ProgressBar) findViewById(R.id.progressbar_level);

		mLevel.setMax(32676);

		ToggleButton recordBtn = (ToggleButton) findViewById(R.id.togglebutton_record);

		Button redrawBtn = (Button) findViewById(R.id.redrawBtn);
		mInfoOut = (TextView) findViewById(R.id.info);

		redrawBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mAudioView.updateModel();
			}
		});

		recordBtn
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked
								&& RecordState.RECORDING.equals(factory()
										.getSpantusAudioCtx().getRecordState())) {

							ExtractorReaderCtx readerCtx = factory()
									.createDefaultReader();
							final BaseWraperExtractorReader wraperReader = factory()
									.createBaseWraperExtractorReader(readerCtx,
											1);
							Thread thread = new Thread(new Runnable() {
								public void run() {
									factory().createRecordService().record(
											factory().getSpantusAudioCtx(),
											wraperReader);
								}
							});

							thread.setPriority(Thread.currentThread()
									.getThreadGroup().getMaxPriority());

							thread.start();

							handler.removeCallbacks(update);
							handler.postDelayed(update, 25);

						} else if (RecordState.RECORDING.equals(factory()
								.getSpantusAudioCtx().getRecordState())) {
							factory().createRecordService().stopRequest(
									factory().getSpantusAudioCtx());
							handler.removeCallbacks(update);
						}

					}
				});
	}

	// private void readAudioBuffer(AudioRecord audio) {
	// try {
	// short[] buffer = new short[bufferSize];
	// mRecordCtx.setSamplesProcessed(0L);
	// int bufferReadResult;
	// Deque<Integer> avgVector = new LinkedList<Integer>();
	// do {
	// //this is main reading part
	// bufferReadResult = audio.read(buffer, 0, bufferSize);
	//
	// for (int i = 0; i < bufferReadResult; i++) {
	// int lastLevel = (int) buffer[i];
	// if (avgVector.size() == 1000) {
	// WindowMinMax minMax = new WindowMinMax();
	// for (Integer val : avgVector) {
	// minMax.setMin(Math.min(minMax.getMin(), val));
	// minMax.setMax(Math.max(minMax.getMax(), val));
	// }
	// mAudioView.onAudioMinMax(System.currentTimeMillis(), minMax);
	// mLastLevel = minMax.getMax();
	// avgVector.clear();
	// }
	// avgVector.add(lastLevel);
	// }
	// mRecordCtx.setSamplesProcessed(mRecordCtx.getSamplesProcessed()+bufferReadResult);
	// } while (bufferReadResult > 0
	// && audio.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING);
	//
	//
	//
	// if (audio != null) {
	// audio.release();
	// audio = null;
	// handler.removeCallbacks(update);
	//
	// }
	//
	// } catch (Exception e) {
	// Log.e(TAG, "Error while reading audio from mic", e);
	// }
	//
	// }

	private Runnable update = new Runnable() {
		public void run() {
			Integer lastLevel = mLastLevel;
			RecordActivity.this.mLevel.setProgress(lastLevel);
			lastLevel /= 2;
			mAudioView.updateModel();
			// mInfoOut.setText(MessageFormat.format("processesd {0}s and {1}samples",
			// (System.currentTimeMillis()-mRecordCtx.getSrartedOn())/1000,mRecordCtx.getSamplesProcessed()));
			handler.postAtTime(this, SystemClock.uptimeMillis() + 500);
		}

	};

}
