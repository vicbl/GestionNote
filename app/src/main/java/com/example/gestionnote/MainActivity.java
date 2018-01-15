package com.example.gestionnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
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
    List<String> listDataHeader;
    HashMap<String, String> listDataChild;

    JSONArray filetmp = new JSONArray();

    final Context context = this;
    private View helpView;


    public String getObjectToDelete() {
        return objectToDelete;
    }

    public void setObjectToDelete(String objectToDelete) {
        this.objectToDelete = objectToDelete;
    }

    private String objectToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Gestion de Notes");


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

      /*  */

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        /**
         * Le runnable permet d'afficher la boite de dialog de suppression
         * Il s'affiche après un clique long sur le titre de la note
         */
        final Runnable run = new Runnable() {

            @Override
            public void run() {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Que voulez vous faire ?");
                builder1.setCancelable(true);

                /**
                 * Premier choix : on supprime la note
                 * Une boite de dialog demande la confirmation
                 */
                builder1.setPositiveButton(
                        "Supprimer la note",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final Dialog dialogDelete = new Dialog(context);
                                dialogDelete.setContentView(R.layout.delete_note_dial);
                                dialogDelete.setTitle("Supprimer");

                                // set the custom dialog components - text, image and button
                                final TextView textTitre = (TextView) dialogDelete.findViewById(R.id.textDelete);


                                String textToDisplay = "Voulez vous supprimer ";
                                textToDisplay = textToDisplay + getObjectToDelete();
                                textTitre.setText(textToDisplay);

                                Button dialogButtonOK = (Button) dialogDelete.findViewById(R.id.dialogButtonOK);
                                Button dialogButtonCancel = (Button) dialogDelete.findViewById(R.id.dialogButtonCancel);
                                // if button is clicked, close the custom dialog
                                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogDelete.dismiss();
                                    }
                                });


                                dialogButtonOK.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        deleteObjectFromFile(getObjectToDelete());
                                        Log.e("Click delete", listDataHeader.toString());
                                        refreshView();
                                        dialogDelete.dismiss();
                                        Toast.makeText(getApplicationContext(),
                                                getObjectToDelete() + " Supprimé avec succès",
                                                Toast.LENGTH_SHORT).show();

                                    }

                                });

                                dialogDelete.show();
                            }
                        });

                /**
                 * Deuxième choix on modifie a note
                 * Une boite dialog avec le champ du titre et de la description apparait pour modifier
                 */
                builder1.setNegativeButton(
                        "Modifier la note",
                        new DialogInterface.OnClickListener() {

                            String titreNoteAModif = getObjectToDelete();

                            String descriptionAModif = getdescriptionNote(titreNoteAModif);

                            public void onClick(DialogInterface dialoga, int id) {
                                // On affiche une boite dialog avec les infos de la note

                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.create_note_dial);

                                ((EditText) dialog.findViewById(R.id.titre)).setText(titreNoteAModif);
                                ((EditText) dialog.findViewById(R.id.note)).setText(descriptionAModif);
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
                                        String note = textNote.getText().toString();

                                        //On retire la boite de dialogue si l'édition de la note s'est bien passé
                                        if (updateObject(titreNoteAModif, titre, note)) {
                                            dialog.dismiss();
                                            refreshView();
                                        }

                                        Log.e("Titre : ", titre);
                                        Log.e("DataHeader : ", listDataHeader.toString());


                                    }
                                });
                                dialog.show();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }


        };


        // handel permet de lancer le thread run après un délai de 500 ms
        final Handler handel = new Handler();

        expListView.setLongClickable(true);
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                   @Override
                                                   public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                       //On récupère le titre de la note que l'on souhaite supprimer
                                                       TextView txtListChild = (TextView) view
                                                               .findViewById(R.id.lblListHeader);


                                                       // Log.e("Child : ", " i = "+i+" flat position"+((ExpandableListView)adapterView).getFlatListPosition(l)+" child "+txtListChild.getText());
                                                       setObjectToDelete(txtListChild.getText().toString());
                                                       handel.postDelayed(run, 500);
                                                       return false;
                                                   }
                                               }

        );


        /**
         * Bouton permettant de supprimer toutes les notes
         */
        FloatingActionButton deleteAll = (FloatingActionButton) findViewById(R.id.deleteNote);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Confirmer la supression de toutes les notes");
                builder1.setCancelable(true);

                /**
                 * Premier choix : on supprime tout
                 */
                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                writeToFile("");
                                listDataChild.clear();
                                listDataHeader.clear();

                                filetmp = new JSONArray(new ArrayList<String>());

                                refreshView();


                                Toast.makeText(getApplicationContext(),
                                        "Vous avez supprimé toutes les notes",
                                        Toast.LENGTH_SHORT).show();

                                dialog.cancel();
                            }

                        });

                /**
                 * Deuxième choix on annule
                 */
                builder1.setNegativeButton(
                        "Annuler",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

        /**
         * Bouton permettant d'ajouter une note
         */
        FloatingActionButton createAdd = (FloatingActionButton) findViewById(R.id.addNote);
        createAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.create_note_dial);


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
                        String note = textNote.getText().toString();

                        //On retire la boite de dialogue si la création de la note s'est bien passé
                        if (createObject(titre, note)) {
                            dialog.dismiss();
                        }

                        Log.e("Titre : ", titre);
                        Log.e("DataHeader : ", listDataHeader.toString());


                    }
                });
                dialog.show();
            }


        });


    }

    /**
     * Affiche la page d'aide lorsque l'on clique sur le bouton "?"
     *
     * @param mi
     */
    public void onComposeAction(MenuItem mi) {
        Intent myIntent = new Intent(expListView.getContext(), HelpActivity.class);
        startActivityForResult(myIntent, 0);
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    /**
     * Permet de récupérer la description de la note à partir de son titre
     *
     * @param titre
     * @return
     */
    private String getdescriptionNote(String titre) {
        int indexTitre = listDataHeader.indexOf(titre);
        JSONObject tmp = new JSONObject();
        String descriptionNote = null;
        try {
            tmp = (JSONObject) filetmp.get(indexTitre);
            descriptionNote = tmp.get("note").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("descriptionNote", " = " + descriptionNote + " index = " + indexTitre);
        return descriptionNote;
    }

    /**
     * Méthode qui permet de rafraichir la page contenant toutes les notes
     */
    private void refreshView() {
        int i = 0;
        while (i < expListView.getLastVisiblePosition()) {
            expListView.collapseGroup(i);
            i++;
        }
        expListView.setVisibility(View.GONE);
        expListView.refreshDrawableState();
        expListView.setVisibility(View.VISIBLE);

    }

    /**
     * Permet d'ajouter une note dans le fichier JSON et ajouter la note aux listes
     *
     * @param titre, titre de la note
     * @param note,  descritpion de la note
     * @return true quand la création c'est bien passé ou false quand ce n'est pas le cas
     */
    private boolean createObject(String titre, String note) {
        if (!listDataHeader.contains(titre)) {
            listDataHeader.add(titre);
            listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), note);

            JSONObject obj = new JSONObject();
            try {
                obj.put("titre", titre);
                obj.put("note", note);


                filetmp.put(obj);


                writeToFile(filetmp.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("JSON content", obj.toString());
            Log.e("file array", filetmp.toString());

            Toast.makeText(getApplicationContext(),
                    titre + " créé avec succès",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getApplicationContext(),
                    titre + " existe déjà",
                    Toast.LENGTH_SHORT).show();
            Log.e("Titre deja cree", titre);
            return false;
        }
    }

    /**
     * Permet de modifier une note
     *
     * @param oldTitre Va permettre de récupérer la position de la note dans le fichier et dans les list
     * @param titre    nouveau titre
     * @param note     nouvelle description
     * @return
     */
    private boolean updateObject(String oldTitre, String titre, String note) {


        //On recupère l'index de l'ancienne note pour ajouter la nouvelle au même endroit
        int indexOldNote = listDataHeader.indexOf(oldTitre);
        expListView.collapseGroup(indexOldNote);
        Log.e("file array", " avant edition " + filetmp.toString());
        Log.e("listDataHeader", " avant edition " + listDataHeader.toString());
        JSONObject obj = new JSONObject();
        try {
            obj = (JSONObject) filetmp.get(indexOldNote);
            obj.put("titre", titre);
            obj.put("note", note);

            filetmp.put(indexOldNote, obj);
            writeToFile(filetmp.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        listDataHeader.remove(indexOldNote);
        listDataHeader.add(indexOldNote, titre);
        listDataChild.put(listDataHeader.get(indexOldNote), note);


        Log.e("JSON content", obj.toString());
        Log.e("file array", " apres edition " + filetmp.toString());
        Log.e("listDataHeader", " apres edition " + listDataHeader.toString());
        Toast.makeText(getApplicationContext(),
                titre + " éditée avec succès",
                Toast.LENGTH_SHORT).show();
        return true;

    }

    /**
     * Permet de supprimer une seule note du fichier de sauvegarde
     *
     * @param toDelete est la string contenant le titre de la note à supprimer
     */
    private void deleteObjectFromFile(String toDelete) {

        if (listDataHeader.contains(toDelete)) {

            int posObjToDelete = listDataHeader.indexOf(toDelete);
            expListView.collapseGroup(posObjToDelete);

            // On supprime les notes des listes du layout
            listDataHeader.remove(toDelete);
            listDataChild.remove(toDelete);

            // On supprime l'objet du fichier de sauvegarde
            filetmp.remove(posObjToDelete);

            writeToFile(filetmp.toString());


            Log.e("obj supp pos = ", "" + posObjToDelete);
            Log.e("file array", " after delete" + filetmp.toString());

        } else {
            Toast.makeText(getApplicationContext(),
                    "Error ce titre n'existe pas",
                    Toast.LENGTH_SHORT).show();
            Log.e("Error", " Impossible de supprimer " + toDelete + " il n'existe pas");
        }

    }

    /**
     * Permet d'écrir dans le fichier de sauvegarde
     *
     * @param data String devant contenir un tableau JSON contenant toutes les notes
     */
    private void writeToFile(String data) {

        try {
            FileOutputStream fos = openFileOutput("test.txt", Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(data);
            outputStreamWriter.close();


        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Permet de recupérer tout le contenu du fichier de sauvegarde
     *
     * @return un String
     */
    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("test.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    /**
     * Permet de charger le contenu du fichier dans les listes d'affichage de titres et de notes
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, String>();


        try {
            filetmp = new JSONArray(readFromFile());
            JSONObject obj = new JSONObject();
            for (int i = 0; i < filetmp.length(); i++) {
                obj = filetmp.getJSONObject(i);
                String titre = obj.get("titre").toString();
                String note = obj.get("note").toString();
                listDataHeader.add(titre);
                listDataChild.put(listDataHeader.get(i), note);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


