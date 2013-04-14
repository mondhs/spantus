package org.spantus.record.view;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map.Entry;

import org.spantus.record.R;
import org.spantus.record.entity.WindowMinMax;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class AudioView extends View {

	private static final int AUDIO_MAX_VALUE = 32676;

	private static final String TAG = AudioView.class.getCanonicalName();

	// Signal painting
	private Bitmap mBitmap;
	private Canvas mCanvas = new Canvas();
	private Paint mPaint = new Paint();

	private Float mSpeed = 1.0F;
	private Float mScale = null;
	private int mColor = Color.argb(192, 255, 64, 64);
	private float mLastMinValue;
	private float mYOffset;
	private float mWidth;
	private float mHeight;
	private float mMaxX;
	private float mLastX;
	private Path mPath = new Path();

	private float mLastMaxValue;

	public AudioView(Context context) {
		super(context);
	}

	public AudioView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(getResources().getColor(R.drawable.grid_line));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		synchronized (this) {
			if (mBitmap != null) {
				final Paint paint = mPaint;
				if (mLastX >= mMaxX) {
					mLastX = 0;
					final float yoffset = mYOffset;
					final float maxx = mMaxX;
					paint.setColor(0xFFAAAAAA);
					mCanvas.drawColor(0xFFFFFFFF);
					mCanvas.drawLine(0, yoffset, maxx, yoffset, paint);

				}
				canvas.drawBitmap(mBitmap, 0, 0, null);
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
		mCanvas.setBitmap(mBitmap);
		mCanvas.drawColor(0xFFFFFFFF);
		mScale = -(h * .5f * (1.0f / (AUDIO_MAX_VALUE)));
		mYOffset = h * 0.5f;
		mWidth = w;
		mHeight = h;
		if (mWidth < mHeight) {
			mMaxX = w;
		} else {
			mMaxX = w - 50;
		}
		mLastX = mMaxX;
	}

	public void onAudioMinMax(Long onTime, WindowMinMax value) {
		Log.d(TAG, MessageFormat.format("[onAudioSample] on {0} value  {1}",
				onTime, value));
		synchronized (this) {
			if (mBitmap != null) {
				final Canvas canvas = mCanvas;
				final Paint paint = mPaint;

				float deltaX = mSpeed;
				float newX = mLastX + deltaX;
				float minV = mYOffset + value.getMin() * mScale;
				float maxV = mYOffset + value.getMax() * mScale;
				paint.setColor(mColor);
				canvas.drawLine(mLastX, mLastMinValue, newX, minV, paint);
				canvas.drawLine(mLastX, mLastMaxValue, newX, maxV, paint);
				canvas.drawLine(newX, minV, newX, maxV, paint);
				mLastMinValue = minV;
				mLastMaxValue = maxV;
				mLastX += mSpeed;

			}
		}
	}

	public void updateModel() {
		invalidate();
	}
}
