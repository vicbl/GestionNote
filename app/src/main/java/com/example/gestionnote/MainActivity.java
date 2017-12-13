package com.example.gestionnote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;

	final Context context = this;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.lvExp);

		// preparing list data


		prepareListData();
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);

		// Listview Group click listener
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				Toast.makeText(getApplicationContext(),
						listDataHeader.get(groupPosition) + " Expanded",
						Toast.LENGTH_SHORT).show();
			}
		});

		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				Toast.makeText(getApplicationContext(),
						listDataHeader.get(groupPosition) + " Collapsed",
						Toast.LENGTH_SHORT).show();

			}
		});

		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});


		FloatingActionButton create = (FloatingActionButton) findViewById(R.id.fab);
		create.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
					// Click action
				/*	Toast.makeText(getApplicationContext(),
							"click",
						Toast.LENGTH_SHORT).show();*/



				// custom dialog
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.create_note_dial);
				dialog.setTitle("Title...");

				// set the custom dialog components - text, image and button
				final EditText textNote = (EditText) dialog.findViewById(R.id.note);
				final EditText textTitre = (EditText) dialog.findViewById(R.id.titre);

//				ImageView image = (ImageView) dialog.findViewById(R.id.image);
//				image.setImageResource(R.drawable.ic_launcher);*/

				Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
				Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
				// if button is clicked, close the custom dialog
				dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialogButtonOK.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String titre = textTitre.getText().toString();
						listDataHeader.add(titre);

						Toast.makeText(getApplicationContext(),
								context.getFilesDir().toString(),
								Toast.LENGTH_SHORT).show();

						String note = textNote.getText().toString();


						List<String> tmp = new ArrayList<String>();
						tmp.add(note);

						listDataChild.put(listDataHeader.get(listDataHeader.size()-1),tmp);

						String fileContent = readFromFile();




						JSONObject obj = new JSONObject();
						try {
							obj.put("titre",titre );
							obj.put("note",note );
							JSONObject filetmp = new JSONObject(fileContent.toString());
							Log.e("get number",""+filetmp.length());
							Log.e("get titre",""+filetmp.getJSONObject(titre));
						} catch (JSONException e) {
							e.printStackTrace();
						}

						Log.e("JSON content",obj.toString());
						Log.e("file content",fileContent);


					if (!fileContent.isEmpty()){
						fileContent=fileContent+",";
					}
						writeToFile(fileContent+obj.toString());

						dialog.dismiss();
					}
				});
				dialog.show();
			}


		});


	}


	private void writeToFile(String data) {

		try {
			FileOutputStream fos = openFileOutput("test.txt", Context.MODE_PRIVATE);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
			outputStreamWriter.write(data);
			Toast.makeText(getApplicationContext(),
					"Sauvegarder",
					Toast.LENGTH_SHORT).show();
			outputStreamWriter.close();
			Toast.makeText(getApplicationContext(),
					"Sauvegarder",
					Toast.LENGTH_SHORT).show();

		}
		catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}


	private String readFromFile() {

		String ret = "";

		try {
			InputStream inputStream = context.openFileInput("test.txt");

			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		}
		catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return ret;
	}

	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		try {
			JSONObject obj = new JSONObject(readFromFile().toString());
			String titre = obj.get("titre").toString();
			String note = obj.get("note").toString();

				listDataHeader.add(titre);
Log.e("errorkjdfgdkfjg",titre);
			Log.e("errorkjdfgdkfjg",note);




		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
