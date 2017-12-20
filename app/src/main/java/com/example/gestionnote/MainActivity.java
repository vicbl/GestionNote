package com.example.gestionnote;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;

 final Context context = this;
    
    private TodoList maListe = new TodoList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Gestion de Notes");

        try {
            FileOutputStream fos = openFileOutput("test.txt", Context.MODE_PRIVATE);
            maListe.setFos(fos);
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        try {
            InputStream inputStream = context.openFileInput("test.txt");
            maListe.setInputStream(inputStream);
            Log.e("Setting input stream", "ok");
        } catch (IOException e) {
            Log.e("Setting input stream", "ko");
            Log.e("Exception", "File write failed: " + e.toString());
        }

                // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);



        listAdapter = new ExpandableListAdapter(this, maListe.get_listDataHeader(), maListe.get_listDataChild());

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
                        maListe.get_listDataHeader().get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        maListe.get_listDataHeader().get(groupPosition) + " Collapsed",
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
                        maListe.get_listDataHeader().get(groupPosition)
                                + " : "
                                + maListe.get_listDataChild().get(
                                maListe.get_listDataHeader().get(groupPosition)), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
        final Runnable run = new Runnable() {

            @Override
            public void run() {
                // Your code to run on long click
                Toast.makeText(getApplicationContext(),
                        "5000",
                        Toast.LENGTH_SHORT).show();
            }
        };
        final Handler handel = new Handler();
        expListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handel.postDelayed(run, 500);


                        break;
                    case MotionEvent.ACTION_UP:
                        handel.removeCallbacks(run);
                        Toast.makeText(getApplicationContext(),
                                "released",
                                Toast.LENGTH_SHORT).show();

                        break;
                }
                return false;
            }
        });

        // Bouton supprimer all
        FloatingActionButton createDelete = (FloatingActionButton) findViewById(R.id.deleteNote);
        createDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maListe.deleteAll();

                expListView.setVisibility(View.GONE);
                expListView.setVisibility(View.VISIBLE);


                Toast.makeText(getApplicationContext(),
                        "Supprimer",
                        Toast.LENGTH_SHORT).show();
            }
        });


        // Bouton Add
        FloatingActionButton createAdd = (FloatingActionButton) findViewById(R.id.addNote);
        createAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.create_note_dial);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                final EditText textNote = (EditText) dialog.findViewById(R.id.note);
                final EditText textTitre = (EditText) dialog.findViewById(R.id.titre);


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


					/*	Toast.makeText(getApplicationContext(),
                                context.getFilesDir().toString(),
								Toast.LENGTH_SHORT).show();*/

                        String note = textNote.getText().toString();
                        maListe.add(titre, note);
                        dialog.dismiss();


                    }
                });
                dialog.show();
            }


        });


    }

}
