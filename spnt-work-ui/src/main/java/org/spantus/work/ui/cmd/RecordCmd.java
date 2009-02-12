package org.spantus.work.ui.cmd;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.AudioCapture;
import org.spantus.core.io.WraperExtractorReader;
import org.spantus.core.threshold.StaticThreshold;
import org.spantus.extractor.ExtractorsFactory;
import org.spantus.extractor.impl.ExtractorEnum;
import org.spantus.extractor.impl.ExtractorUtils;
import org.spantus.logger.Logger;
import org.spantus.work.ui.container.SampleChangeListener;
import org.spantus.work.ui.dto.SpantusWorkInfo;

public class RecordCmd extends AbsrtactCmd {

	protected Logger log = Logger.getLogger(getClass());

	private AudioCapture capture;
	
	private Timer timer;

	private SpantusWorkInfo ctx;
	
	private boolean isRecordInitialyzed = false;
	
	SampleChangeListener lisetener;

	public RecordCmd(SampleChangeListener lisetener) {
		this.lisetener = lisetener;
	}
	
	public String execute(final SpantusWorkInfo ctx) {
		this.ctx = ctx;
		
		ctx.getProject().getCurrentSample().setCurrentFile(null);
		ctx.getProject().getCurrentSample().setFormat(null);
		
		IExtractorInputReader reader = ExtractorsFactory
				.createReader(getFormat());
		
		StaticThreshold threshold = new StaticThreshold();
		threshold.setCoef(2f);
		
		ExtractorUtils.register(reader, ExtractorEnum.WAVFORM_EXTRACTOR);
		ExtractorUtils.registerThreshold(reader, ExtractorEnum.SMOOTHED_ENERGY_EXTRACTOR, threshold);
		
		capture = new AudioCapture(new WraperExtractorReader(reader));
		capture.setFormat(getFormat());
		capture.start();
		ctx.setPlaying(true);
		getTimer().schedule(new InitCapture(reader), 2000L);
		getTimer().schedule(new UpdateCapture(), 2000L, 1000L);

		return null;
	}

	public class InitCapture extends TimerTask {
		IExtractorInputReader reader;
		public InitCapture(IExtractorInputReader reader) {
			this.reader = reader;
		}
		@Override
		public void run() {
			lisetener.changedReader(reader);
			isRecordInitialyzed = true;
		}
	}
	
	public class UpdateCapture extends TimerTask {
		@Override
		public void run() {
			if(!isRecordInitialyzed){
				return;
			}
			if (lisetener != null) {
				lisetener.refresh();
			}
			if (!ctx.getPlaying()) {
				log.debug("repaint");
				capture.finalize();
				this.cancel();
			}
		}

	}
	
	
	public AudioFormat getFormat() {
		float sampleRate = 8000;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,
				bigEndian);
	}

	protected Timer getTimer() {
		if(timer == null){
			timer = new Timer("Spantus sound capture");
		}
		return timer;
	}

}
