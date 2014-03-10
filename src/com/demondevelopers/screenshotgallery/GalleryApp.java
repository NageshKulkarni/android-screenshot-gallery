package com.demondevelopers.screenshotgallery;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;


public class GalleryApp extends Application
{
	private static final String KEY_ZOOM_IMG = "zoom-image";
	
	private BitmapCache mImageCache = new BitmapCache();
	
	
	public static GalleryApp from(Context context)
	{
		if(context == null){
			throw new IllegalStateException("cannot get app from null context");
		}
		return (GalleryApp)context.getApplicationContext();
	}
	
	public void putImageIntoCache(String key, Bitmap bitmap)
	{
		mImageCache.put(key, bitmap);
	}
	
	public Bitmap getImageFromCache(String key)
	{
		return mImageCache.get(key);
	}
	
	public void purgeImageCache()
	{
		mImageCache.evictAll();
	}
	
	public void putZoomImage(Bitmap bitmap)
	{
		if(bitmap == null){
			mImageCache.remove(KEY_ZOOM_IMG);
		}
		else{
			mImageCache.put(KEY_ZOOM_IMG, bitmap);
		}
	}
	
	public Bitmap getZoomImage()
	{
		return mImageCache.get(KEY_ZOOM_IMG);
	}
	
	public static class BitmapCache extends LruCache<String, Bitmap>
	{
		public BitmapCache()
		{
			super(Math.min(30 * 1024 * 1024, 
				(int)(0.08d * Runtime.getRuntime().maxMemory())));
		}
		
		@Override
		protected int sizeOf(String key, Bitmap value)
		{
			return value.getRowBytes() * value.getHeight();
		}
	}
}
