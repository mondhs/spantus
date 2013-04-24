package org.spantus.android.segmentor.record.activity;


import org.spantus.android.segmentor.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button startRecordingBtn	 = (Button) findViewById(R.id.startRecordingBtn);
		startRecordingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {	
            	Intent recordIntent = new Intent(MainActivity.this, RecordActivity.class);
            	MainActivity.this.startActivity(recordIntent);
            }
        });
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
