package com.demondevelopers.screenshotgallery;

import android.os.Bundle;


public class MainActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(savedInstanceState == null){
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new PictureGridFragment())
				.commit();
		}
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		getApp().purgeImageCache();
	}
}
