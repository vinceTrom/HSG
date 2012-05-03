package com.oqs.opengl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class LaunchActivity extends Activity{

	private String currentAnim= "nope";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Spinner	spinner = ((Spinner)findViewById(R.id.spinner));
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.anims_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
		
		
		((Spinner)findViewById(R.id.spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				currentAnim = (String) ((TextView)arg1).getText();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});


		
		findViewById(R.id.launch).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LaunchActivity.this, OpenglActivity.class);
				intent.putExtra("anim",currentAnim);
				startActivity(intent);			
			}
		});
	}
	
}
