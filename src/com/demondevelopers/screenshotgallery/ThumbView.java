package com.demondevelopers.screenshotgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.AttributeSet;
import android.widget.ImageView;

import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.EXACTLY;


public class ThumbView extends ImageView
{
	private ThumbRequest mRequest;
	
	
	public ThumbView(Context context)
	{
		super(context);
		initView(context, null);
	}
	
	public ThumbView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context, attrs);
	}
	
	public ThumbView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context, attrs);
	}
	
	protected void initView(Context context, AttributeSet attrs)
	{
		//
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, makeMeasureSpec(getSize(widthMeasureSpec), EXACTLY));
	}
	
	private String createCacheKey(long id, int kind)
	{
		return "thumb-" + kind + "-" + id;
	}
	
	public void setImageThumbnail(long id, int kind)
	{
		Bitmap cached = GalleryApp.from(getContext()).getImageFromCache(createCacheKey(id, kind));
		if(cached == null){
			cancelImageRequest();
			setImageResource(R.drawable.ic_picture);
			mRequest = new ThumbRequest(id, kind);
			mRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else{
			setImageBitmap(cached);
		}
	}
	
	private void cancelImageRequest()
	{
		if(mRequest != null && !mRequest.isCancelled()){
			mRequest.cancel();
		}
	}
	
	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		cancelImageRequest();
	}
	
	
	private class ThumbRequest extends AsyncTask<Void, Void, Bitmap>
	{
		private long mId;
		private int  mKind;
		
		
		public ThumbRequest(long id, int kind)
		{
			mId   = id;
			mKind = kind;
		}
		
		@Override
		protected Bitmap doInBackground(Void... params)
		{
			return Thumbnails.getThumbnail(getContext().getContentResolver(),
				mId, mKind, null);
		}
		
		@Override
		protected void onPostExecute(Bitmap result)
		{
			if(result != null){
				GalleryApp.from(getContext())
					.putImageIntoCache(createCacheKey(mId, mKind), result);
				setImageBitmap(result);
			}
		}

		public void cancel()
		{
			super.cancel(false);
			Thumbnails.cancelThumbnailRequest(
				getContext().getContentResolver()
				, mId);
		}
	}
}
