package com.demondevelopers.screenshotgallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;


public class OverlayDecorLayout extends FrameLayout
{
	private View mActionBar;
	
	
	public OverlayDecorLayout(Context context)
	{
		super(context);
		initView(context, null);
	}
	
	public OverlayDecorLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context, attrs);
	}
	
	public OverlayDecorLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context, attrs);
	}
	
	protected void initView(Context context, AttributeSet attrs)
	{
		Activity activity = (Activity)getContext();
		if(activity.getWindow().hasFeature(Window.FEATURE_ACTION_BAR_OVERLAY)){
			mActionBar = findActivityActionBar(activity);
			if(mActionBar == null){
				throw new IllegalStateException("Failed to find ActionBar");
			}
		}
	}
	
	/**
	 * Finds ActionBar the same way the platform does.
	 * 
	 */
	private View findActivityActionBar(Activity activity)
	{
		View actionBar = null;
		View decor = activity.getWindow().getDecorView();
		int top_action_bar = getResources().getIdentifier("android:id/top_action_bar", null, null);
		if(top_action_bar != 0){
			actionBar = decor.findViewById(top_action_bar);
		}
		if(actionBar == null){
			int action_bar_container = getResources().getIdentifier("android:id/action_bar_container", null, null);
			if(action_bar_container != 0){
				actionBar = decor.findViewById(action_bar_container);
			}
		}
		return actionBar;
	}
	
	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		if(mActionBar != null){
			ViewTreeObserver vto = mActionBar.getViewTreeObserver();
			if(vto.isAlive()){
				vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
			}
		}
	}
	
	private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = 
		new ViewTreeObserver.OnGlobalLayoutListener()
	{
		@Override
		public void onGlobalLayout()
		{
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)getLayoutParams();
			lp.topMargin = (int)(mActionBar.getHeight() + mActionBar.getTranslationY());
			setLayoutParams(lp);
			/*post(new Runnable()
			{
				public void run()
				{
					requestLayout();
				};
			});*/
		}
	};
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		if(mActionBar != null){
			ViewTreeObserver vto = mActionBar.getViewTreeObserver();
			if(vto.isAlive()){
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
					vto.removeOnGlobalLayoutListener(mGlobalLayoutListener);
				}
				else{
					vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
				}
			}
		}
	}
}
