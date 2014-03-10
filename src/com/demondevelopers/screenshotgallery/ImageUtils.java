package com.demondevelopers.screenshotgallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;


public final class ImageUtils
{
	public static Bitmap getThumbSizedBitmap(Context context, String filePath)
	{
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		if(options.outWidth <= 0 || options.outHeight <= 0){
			return null;
		}
		options.inSampleSize = calcSampleSize(
			options.outWidth, options.outHeight,
			displayMetrics.widthPixels / 4, displayMetrics.heightPixels / 4);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
	
	public static Bitmap getDisplaySizedBitmap(Context context, String filePath, BitmapFactory.Options options)
	{
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		options.inSampleSize = 1;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		if(options.outWidth <= 0 || options.outHeight <= 0){
			return null;
		}
		options.inSampleSize = calcSampleSize(
			options.outWidth, options.outHeight,
			displayMetrics.widthPixels, displayMetrics.heightPixels);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}
	
	public static int calcSampleSize(int imageWidth, int imageHeight, int desiredWidth, int desiredHeight)
	{
		int sampleSize = 1;
		while((imageWidth > desiredWidth) && (imageHeight > desiredHeight)){
			sampleSize  <<= 1;
			imageWidth  >>= 1;
			imageHeight >>= 1;
		}
		return sampleSize;
	}
	
	@SuppressLint("DefaultLocale")
	public static String humanReadableByteCount(long bytes, boolean si)
	{
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}
