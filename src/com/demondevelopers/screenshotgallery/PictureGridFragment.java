package com.demondevelopers.screenshotgallery;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


public class PictureGridFragment extends BaseFragment
{
	private GridView       mGridView;
	private PictureAdapter mAdapter;
	
	
	public PictureGridFragment()
	{
		// Blank constructor required by platform
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mAdapter = new PictureAdapter(getActivity());
		getLoaderManager().restartLoader(0, null, mAdapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_picture_grid, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mGridView = (GridView)view.findViewById(R.id.picture_grid);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(mItemClickListener);
	}
	
	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener()
	{
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Cursor cursor = mAdapter.getCursor();
			if(cursor.moveToPosition(position)){
				getApp().putZoomImage(ImageUtils.getThumbSizedBitmap(view.getContext(), 
					cursor.getString(PictureQuery.DATA)));
			}
			Intent intent = PictureActivity.createIntent(view.getContext(), position);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
				boolean wasEnabled = view.isDrawingCacheEnabled();
				view.setDrawingCacheEnabled(true);
				Bundle options = ActivityOptions.makeThumbnailScaleUpAnimation(
					view, Bitmap.createBitmap(view.getDrawingCache()), 0, 0).toBundle();
				view.setDrawingCacheEnabled(wasEnabled);
				getActivity().startActivity(intent, options);
			}
			else{
				getActivity().startActivity(intent);
			}
		}
	};
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mAdapter.changeCursor(null);
	}
	
	
	private class PictureAdapter extends CursorAdapter
		implements LoaderCallbacks<Cursor>
	{
		private LayoutInflater mInflater;
		
		
		public PictureAdapter(Context context)
		{
			super(context, null, 0);
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args)
		{
			return new CursorLoader(getActivity(), 
				PictureQuery.URI, PictureQuery.PROJECTION, 
				null, null, Images.Media.DEFAULT_SORT_ORDER);
		}
		
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data)
		{
			mAdapter.changeCursor(data);
		}
		
		@Override
		public void onLoaderReset(Loader<Cursor> loader)
		{
			//
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent)
		{
			return mInflater.inflate(R.layout.item_picture, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor)
		{
			((ThumbView)view.findViewById(R.id.picture_item_thumb))
				.setImageThumbnail(cursor.getLong(PictureQuery._ID), Thumbnails.MICRO_KIND);
			
			((TextView)view.findViewById(R.id.picture_item_text1))
				.setText(cursor.getString(PictureQuery.TITLE));
			
			((TextView)view.findViewById(R.id.picture_item_text2))
				.setText(DateFormat.getDateFormat(view.getContext())
					.format(cursor.getLong(PictureQuery.DATE)));
		}
	}
	
	private static interface PictureQuery
	{
		public static final Uri URI = Images.Media.EXTERNAL_CONTENT_URI;
		
		public static final String[] PROJECTION = {
			Images.ImageColumns._ID,
			Images.ImageColumns.TITLE,
			Images.ImageColumns.DATE_TAKEN,
			Images.ImageColumns.DATA
		};
		
		public static int _ID   = 0;
		public static int TITLE = 1;
		public static int DATE  = 2;
		public static int DATA  = 3;
	}
}
