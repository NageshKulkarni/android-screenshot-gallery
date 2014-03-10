package com.demondevelopers.screenshotgallery;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;


public class FitTopDecorOnlyLayout extends FrameLayout
{
	public FitTopDecorOnlyLayout(Context context)
	{
		super(context);
		initView(context, null);
	}
	
	public FitTopDecorOnlyLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context, attrs);
	}
	
	public FitTopDecorOnlyLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context, attrs);
	}
	
	protected void initView(Context context, AttributeSet attrs)
	{
		//
	}
	
	@Override
	protected boolean fitSystemWindows(Rect insets)
	{
		setPadding(0, insets.top, 0, 0);
		return true;
	}
}
