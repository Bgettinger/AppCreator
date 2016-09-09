package com.solly.appcreator;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static com.solly.appcreator.Utils.isExternalStorageWritable;


public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Calling superclass creator
		super.onCreate(savedInstanceState);

		// Setting standard view to main activity
		setContentView(R.layout.activity_main);

		File tld = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		new File(tld, "AppCreator").mkdir();

		// Store the list of applications in the variable apps
		SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);

		for(String app : sharedPref.getStringSet("apps", new HashSet<String>())) {

		}

		ImageButton createButton = (ImageButton) findViewById(R.id.create);
		createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MainActivity.this)
						.setView(MainActivity.this.getLayoutInflater().inflate(R.layout.new_app_dialog, null))
						.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								if(isExternalStorageWritable()) {
									SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
									SharedPreferences.Editor editor = sharedPref.edit();
									Set<String> apps = sharedPref.getStringSet("apps", new HashSet<String>());
									apps.add(MainActivity.this.findViewById(R.id.app_name).toString());
									editor.putStringSet("apps", apps);
									editor.apply();
								} else {
									new AlertDialog.Builder(MainActivity.this)
											.setMessage(R.string.ok)
											.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int id) {
													dialog.dismiss();
												}
											})
											.create();
								}
								dialog.dismiss();
							}
						})
						.create();
			}
		});
	}
}
