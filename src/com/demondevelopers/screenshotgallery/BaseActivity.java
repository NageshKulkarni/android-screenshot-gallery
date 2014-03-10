package com.demondevelopers.screenshotgallery;

import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


public class BaseActivity extends FragmentActivity
{
	private static final String TAG = BaseActivity.class.getSimpleName();
	
	
	protected GalleryApp getApp()
	{
		return GalleryApp.from(this);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void ensureFullscreenLayout()
	{
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			getWindow().getDecorView()
				.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.overflow, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
			if(menu.getClass().getSimpleName().equals("MenuBuilder")){
				try{
					Method m = menu.getClass().getDeclaredMethod(
						"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				}
				catch(NoSuchMethodException e){
					Log.e(TAG, "onMenuOpened", e);
				}
				catch(Exception e){
					throw new RuntimeException(e);
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.action_about){
			new AboutFragment()
				.show(getSupportFragmentManager(), "about");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
