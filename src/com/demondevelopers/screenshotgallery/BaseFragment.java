package com.demondevelopers.screenshotgallery;

import android.support.v4.app.Fragment;


public class BaseFragment extends Fragment
{
	protected GalleryApp getApp()
	{
		return GalleryApp.from(getActivity());
	}
}
