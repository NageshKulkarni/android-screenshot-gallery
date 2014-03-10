package com.demondevelopers.screenshotgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;


public class PictureView extends ImageView
{
	private PictureRequest mRequest;
	private OnAsyncCompleted mOnAsyncCompleted;
	
	
	public PictureView(Context context)
	{
		super(context);
		initView(context, null);
	}
	
	public PictureView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context, attrs);
	}
	
	public PictureView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context, attrs);
	}
	
	protected void initView(Context context, AttributeSet attrs)
	{
		setAdjustViewBounds(true);
	}
	
	public void setOnAsyncCompleted(OnAsyncCompleted onAsyncCompleted)
	{
		mOnAsyncCompleted = onAsyncCompleted;
	}
	
	private void dispatchOnAsyncCompleted(boolean successful, String filePath)
	{
		if(mOnAsyncCompleted != null){
			mOnAsyncCompleted.onAsyncCompleted(this, successful, filePath);
		}
	}
	
	private String createCacheKey(String filePath)
	{
		return "image-" + filePath;
	}
	
	public void setImageAsync(String filePath)
	{
		Bitmap cached = GalleryApp.from(getContext())
			.getImageFromCache(createCacheKey(filePath));
		if(cached == null){
			cancelImageRequest();
			setImageResource(R.drawable.ic_picture);
			mRequest = new PictureRequest(filePath);
			mRequest.execute();
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
	
	
	private class PictureRequest extends AsyncTask<Void, Void, Bitmap>
	{
		private String mFilePath;
		private BitmapFactory.Options mOptions;
		
		
		public PictureRequest(String filePath)
		{
			mFilePath = filePath;
		}
		
		@Override
		protected Bitmap doInBackground(Void... params)
		{
			mOptions = new BitmapFactory.Options();
			return ImageUtils.getDisplaySizedBitmap(getContext(), mFilePath, mOptions);
		}
		
		@Override
		protected void onPostExecute(Bitmap result)
		{
			if(result != null){
				GalleryApp.from(getContext())
					.putImageIntoCache(createCacheKey(mFilePath), result);
				setImageBitmap(result);
				dispatchOnAsyncCompleted(true, mFilePath);
			}
			else{
				dispatchOnAsyncCompleted(false, mFilePath);
			}
		}

		public void cancel()
		{
			super.cancel(false);
			if(mOptions != null){
				mOptions.requestCancelDecode();
			}
		}
	}
	
	public interface OnAsyncCompleted
	{
		public void onAsyncCompleted(PictureView view, boolean successful, String filePath);
	}
}
