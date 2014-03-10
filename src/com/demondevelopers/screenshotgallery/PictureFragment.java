package com.demondevelopers.screenshotgallery;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class PictureFragment extends BaseFragment
	implements PictureView.OnAsyncCompleted
{
	private static final String ARG_VALUES = "argValues";
	
	private ContentValues   mValues;
	private PictureView     mPictureView;
	private OnPictureLoaded mOnPictureLoaded;
	private ToggleUIState   mToggleUIState;
	private GestureDetector mGestureDetector;
	
	
	public static PictureFragment newInstance(ContentValues values)
	{
		PictureFragment frag = new PictureFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_VALUES, values);
		frag.setArguments(args);
		return frag;
	}
	
	public PictureFragment()
	{
		// Blank constructor required by platform
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		if(activity instanceof OnPictureLoaded){
			mOnPictureLoaded = (OnPictureLoaded)activity; 
		}
		if(activity instanceof ToggleUIState){
			mToggleUIState = (ToggleUIState)activity;
		}
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mOnPictureLoaded = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mValues = getArguments().getParcelable(ARG_VALUES);
		mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener()
		{
			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{
				if(mToggleUIState != null){
					return mToggleUIState.toggleUIState();
				}
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e)
			{
				return true;
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_picture, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		mPictureView = (PictureView)view.findViewById(R.id.picture);
		mPictureView.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return mGestureDetector.onTouchEvent(event);
			}
		});
		mPictureView.setOnAsyncCompleted(this);
		mPictureView.setImageAsync(mValues.getAsString(ImageColumns.DATA));
	}
	
	public static interface OnPictureLoaded
	{
		public void onPictureLoaded(boolean successful, String filePath);
	}
	
	public static interface ToggleUIState
	{
		public boolean toggleUIState();
	}
	
	@Override
	public void onAsyncCompleted(PictureView view, boolean successful, String filePath)
	{
		if(mOnPictureLoaded != null){
			mOnPictureLoaded.onPictureLoaded(successful, filePath);
		}
	}
}
