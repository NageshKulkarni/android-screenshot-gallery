package com.demondevelopers.screenshotgallery;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutFragment extends DialogFragment
{
	public AboutFragment()
	{
		// Blank constructor required by platform
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Dialog);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		getDialog().setTitle(getString(R.string.action_about) + ' ' + getString(R.string.app_name));
		return inflater.inflate(R.layout.dialog_about, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		TextView message = (TextView)view.findViewById(android.R.id.message);
		message.setText(Html.fromHtml(getResources().getString(R.string.app_about)));
		message.setMovementMethod(LinkMovementMethod.getInstance());
		view.findViewById(android.R.id.button1).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
	}
	
}
