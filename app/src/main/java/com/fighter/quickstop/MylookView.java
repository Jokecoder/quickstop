package com.fighter.quickstop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/***
 * 自定义进度条
 * @author spring sky
 * Email:vipa1888@163.com
 * 创建时间：2014-1-6下午3:28:51
 */
public class MylookView extends View {
	ArrayList<Boolean> timelist;
	/**分段颜色*/
	private static final int[] SECTION_COLORS = {Color.GREEN,Color.YELLOW,Color.RED};
	/**进度条最大值*/
	private float maxCount;
	/**进度条当前值*/
	private float currentCount;
	/**画笔*/
	private Paint mPaint;
	private int mWidth,mHeight;
	int i=0;
	int lasti=-1;
	int start=-1;//开始
	int end=-1;//结束

	public void setStart(int a){
		this.start=a;
	}
	public void setEnd(int a){
		this.end=a;
	}

	@Override
	public void postInvalidate() {
		super.postInvalidate();
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float lastfloati;
		float nowfloati;
		float f = 1;
		mPaint = new Paint();
		mPaint.setColor(Color.parseColor("#00ff00"));//绿色
		if (timelist.get(0)) {
			canvas.drawRect(0, 2, Float.valueOf(1 * f / 1440 * mWidth), mHeight - 3, mPaint);
		} else {
			mPaint.setColor(Color.parseColor("#ff0000"));//红色
			canvas.drawRect(0, 2, Float.valueOf(1 * f / 1440 * mWidth), mHeight - 3, mPaint);
		}
		for (i = 1; i < 1440; i++) {
			if (i == start||i==end) {
				mPaint.setColor(Color.parseColor("#ff0000"));//设置黑色画笔
				canvas.drawRect(Float.valueOf((i) * f / 1440 * mWidth), 2, Float.valueOf((i + 2) * f / 1440 * mWidth), mHeight + 3, mPaint);

			}
			if (i == 1 || i == 360 || i == 720 || i == 1080) {
				mPaint.setColor(Color.parseColor("#000000"));//设置黑色画笔
				canvas.drawRect(Float.valueOf((i) * f / 1440 * mWidth), 2, Float.valueOf((i + 2) * f / 1440 * mWidth), mHeight + 3, mPaint);
			}
			if (timelist.get(i) && !timelist.get(i - 1)) {//之前都不能用，现在开始可以用，全部画红色
				mPaint.setColor(Color.parseColor("#ff0000"));//设置红色画笔
				lastfloati = Float.valueOf(lasti * f / 1440 * mWidth);
				nowfloati = Float.valueOf((i - 1) * f / 1440 * mWidth);
				canvas.drawRect(lastfloati, 2, nowfloati, mHeight - 3, mPaint);
				lasti = i;
			} else if (!timelist.get(i) && timelist.get(i - 1)) {//之前都可以，现在开始不能用
				mPaint.setColor(Color.parseColor("#00ff00"));//绿色画笔
				lastfloati = Float.valueOf(lasti * f / 1440 * mWidth);
				nowfloati = Float.valueOf((i - 1) * f / 1440 * mWidth);
				canvas.drawRect(lastfloati, 2, nowfloati, mHeight - 3, mPaint);
				lasti = i;
			}
		}
		if (timelist.get(1439)) {//最后一个能用，则之前都能用
			mPaint.setColor(Color.parseColor("#00ff00"));//绿色画笔
			lastfloati = Float.valueOf(lasti * f / 1440 * mWidth);
			canvas.drawRect(lastfloati, 2, mWidth, mHeight - 3, mPaint);//画完
			lasti = i;
		} else {
			mPaint.setColor(Color.parseColor("#ff0000"));//红色画笔
			lastfloati = Float.valueOf(lasti * f / 1440 * mWidth);
			canvas.drawRect(lastfloati, 2, mWidth, mHeight - 3, mPaint);//画完
			lasti = i;
		}
	}
	public MylookView(Context context, AttributeSet attrs,
					  int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	public MylookView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public MylookView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context) {
	}

	public void setTime(ArrayList nowlist){
		this.timelist=nowlist;
	}
	
	private int dipToPx(int dip) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
	}
	
	/***
	 * 设置最大的进度值
	 * @param maxCount
	 */
	public void setMaxCount(float maxCount) {
		this.maxCount = maxCount;
	}
	
	/***
	 * 设置当前的进度值
	 * @param currentCount
	 */
	public void setCurrentCount(float currentCount) {
		this.currentCount = currentCount > maxCount ? maxCount : currentCount;
		invalidate();
	}
	
	public float getMaxCount() {
		return maxCount;
	}
	
	public float getCurrentCount() {
		return currentCount;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
			mWidth = widthSpecSize;
		} else {
			mWidth = 0;
		}
		if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
			mHeight = dipToPx(15);
		} else {
			mHeight = heightSpecSize;
		}
		setMeasuredDimension(mWidth, mHeight);
	}
}
