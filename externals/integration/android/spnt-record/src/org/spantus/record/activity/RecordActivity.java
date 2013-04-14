package org.spantus.record.activity;

import java.text.MessageFormat;
import java.util.Deque;
import java.util.LinkedList;

import org.spantus.record.R;
import org.spantus.record.entity.RecordCtx;
import org.spantus.record.entity.WindowMinMax;
import org.spantus.record.view.AudioView;

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
	private static final String TAG = "RecordActivity";

	private ProgressBar mLevel;
	private AudioView mAudioView;
	private AudioRecord mAudioRecord;
	private int bufferSize;
	private Handler handler = new Handler();
	private int mLastLevel;
	private TextView mInfoOut;
	private RecordCtx mRecordCtx;

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
		
		mRecordCtx = new RecordCtx(); 
		
		mAudioView = (AudioView)findViewById(R.id.audioview);

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
				

		recordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					bufferSize = AudioRecord.getMinBufferSize(sampleRate,
							AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT)
							* bufferSizeFactor;

					Log.d(TAG, MessageFormat.format("sampleRate={0}; bufferSize={1}", sampleRate, bufferSize));

					mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
							sampleRate, AudioFormat.CHANNEL_IN_MONO,
							AudioFormat.ENCODING_PCM_16BIT, bufferSize);

					
					mAudioRecord.startRecording();
					mRecordCtx.setSrartedOn(System.currentTimeMillis());
					
					Thread thread = new Thread(new Runnable() {
						public void run() {
							readAudioBuffer(mAudioRecord);
						}
					});

					thread.setPriority(Thread.currentThread().getThreadGroup()
							.getMaxPriority());

					thread.start();

					handler.removeCallbacks(update);
					handler.postDelayed(update, 25);

				} else if (mAudioRecord != null) {
					mAudioRecord.stop();
					mAudioRecord.release();
					mAudioRecord = null;
					handler.removeCallbacks(update);
				}

			}
		});
	}

	private void readAudioBuffer(AudioRecord audio) {
		try {
			short[] buffer = new short[bufferSize];
			mRecordCtx.setSamplesProcessed(0L);
			int bufferReadResult;
//			Deque<WindowMinMax> levelVector = new LinkedList<WindowMinMax>();
			Deque<Integer> avgVector = new LinkedList<Integer>();
			do {
				bufferReadResult = audio.read(buffer, 0, bufferSize);
				for (int i = 0; i < bufferReadResult; i++) {
					int lastLevel = (int) buffer[i];
					if (avgVector.size() == 1000) {
						WindowMinMax minMax = new WindowMinMax();
						for (Integer val : avgVector) {
							minMax.setMin(Math.min(minMax.getMin(), val));
							minMax.setMax(Math.max(minMax.getMax(), val));
						}
						mAudioView.onAudioMinMax(System.currentTimeMillis(), minMax);
						mLastLevel = minMax.getMax();
						avgVector.clear();
					}
					avgVector.add(lastLevel);
				}
				mRecordCtx.setSamplesProcessed(mRecordCtx.getSamplesProcessed()+bufferReadResult);
			} while (bufferReadResult > 0
					&& audio.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING);

			
			
			if (audio != null) {
				audio.release();
				audio = null;
				handler.removeCallbacks(update);

			}

		} catch (Exception e) {
			Log.e(TAG, "Error while reading audio from mic", e);
		}

	}

	private Runnable update = new Runnable() {
		public void run() {
			Integer lastLevel = mLastLevel;
			RecordActivity.this.mLevel.setProgress(lastLevel);
			lastLevel /= 2;
			mAudioView.updateModel();
			mInfoOut.setText(MessageFormat.format("processesd {0}s and {1}samples", (System.currentTimeMillis()-mRecordCtx.getSrartedOn())/1000,mRecordCtx.getSamplesProcessed()));
			handler.postAtTime(this, SystemClock.uptimeMillis() + 500);
		}

	};

}
