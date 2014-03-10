package com.demondevelopers.screenshotgallery;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class PictureActivity extends BaseActivity implements LoaderCallbacks<Cursor>, 
	PictureFragment.OnPictureLoaded, PictureFragment.ToggleUIState
{
	public static final String EXTRA_POSITION  = "extraPosition";
	
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private ViewPager mViewPager;
	private PicturePagerAdapter mAdapter;
	private ImageView mHolder;
	private int mPosition;
	
	
	public static Intent createIntent(Context context, int position)
	{
		return new Intent(context, PictureActivity.class)
			.putExtra(EXTRA_POSITION, position);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		ensureFullscreenLayout();
		setContentView(R.layout.activity_picture);
		
		mAdapter   = new PicturePagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager)findViewById(R.id.view_pager);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOffscreenPageLimit(3);
		
		mHolder = (ImageView)findViewById(R.id.holder);
		Bitmap thumbnail = getApp().getZoomImage();
		if(thumbnail != null){
			mHolder.setImageBitmap(thumbnail);
			getApp().putZoomImage(null);
		}
		else{
			mHolder.setVisibility(View.GONE);
		}
		if(savedInstanceState != null){
			mPosition = savedInstanceState.getInt(EXTRA_POSITION);
		}
		else{
			mPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);
		}
		getSupportLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onPictureLoaded(boolean successful, String filePath)
	{
		mHolder.setVisibility(View.GONE);
		mHolder.setImageDrawable(null);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public boolean toggleUIState()
	{
		View v = getWindow().getDecorView();
		int flags = 0;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		}
		int current = v.getSystemUiVisibility();
		if((current & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0){
			v.setSystemUiVisibility(flags);
		}
		else{
			flags |= View.SYSTEM_UI_FLAG_LOW_PROFILE | 
				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
				flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
			}
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
				flags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
			}
			v.setSystemUiVisibility(flags);
		}
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_POSITION, mViewPager.getCurrentItem());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		mPosition = savedInstanceState.getInt(EXTRA_POSITION);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		return new CursorLoader(this, Images.Media.EXTERNAL_CONTENT_URI,
			null, null, null, Images.Media.DEFAULT_SORT_ORDER);
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		mAdapter.changeCursor(data);
		mViewPager.setCurrentItem(mPosition, false);
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		//
	}
	
	
	private class PicturePagerAdapter extends FragmentStatePagerAdapter
	{
		private Cursor mCursor;
		
		
		public PicturePagerAdapter(FragmentManager fm)
		{
			super(fm);
		}
		
		public void changeCursor(Cursor newCursor)
		{
			if(newCursor == mCursor){
				return;
			}
			Cursor oldCursor = mCursor;
			mCursor = newCursor;
			if(oldCursor != null){
				oldCursor.close();
			}
			notifyDataSetChanged();
		}
		
		@Override
		public Fragment getItem(int position)
		{
			if(!mCursor.moveToPosition(position)){
				throw new IllegalArgumentException("position " + position + " not in cursor");
			}
			ContentValues values = new ContentValues();
			DatabaseUtils.cursorRowToContentValues(mCursor, values);
			return PictureFragment.newInstance(values);
		}
		
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public void setPrimaryItem(ViewGroup container, final int position, Object object)
		{
			super.setPrimaryItem(container, position, object);
			if(mCursor == null){
				return;
			}
			if(!mCursor.moveToPosition(position)){
				throw new IllegalArgumentException("position " + position + " not in cursor");
			}
			final String title = mCursor.getString(mCursor.getColumnIndex(ImageColumns.TITLE));
			StringBuilder sb = new StringBuilder(128);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
				sb.append(mCursor.getInt(mCursor.getColumnIndex(ImageColumns.WIDTH)));
				sb.append('x');
				sb.append(mCursor.getInt(mCursor.getColumnIndex(ImageColumns.HEIGHT)));
				sb.append(", ");
			}
			sb.append(ImageUtils.humanReadableByteCount(
				mCursor.getLong(mCursor.getColumnIndex(ImageColumns.SIZE)), false));
			sb.append(", ");
			sb.append(DateFormat.getDateFormat(container.getContext())
				.format(mCursor.getLong(mCursor.getColumnIndex(ImageColumns.DATE_TAKEN))));
			final String subtitle = sb.toString();
			mHandler.post(new Runnable(){
				@Override
				public void run()
				{
					getActionBar().setTitle(title);
					getActionBar().setSubtitle(subtitle);
				}
			});
		}
		
		@Override
		public int getCount()
		{
			return (mCursor != null) ? mCursor.getCount() : 0;
		}
	}
}
