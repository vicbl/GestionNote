package com.example.gestionnote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
-import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hugo on 20/12/2017.
 */

public class TodoList{
    private List<String> _listDataHeader;
    private HashMap<String, String> _listDataChild;
    private FileOutputStream fos;
    private InputStream inputStream;
    private JSONArray filetmp;
    public static  Context contextTodoList = MainActivity.class.getApplicationContext();

public TodoList(){
    prepareListData();
}
public FileOutputStream getFos(){
    return this.fos;
}

    public void setFos(FileOutputStream fos) {
        this.fos = fos;
    }
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private JSONArray _filetmp = new JSONArray();

    public List<String> get_listDataHeader() {
        return _listDataHeader;
    }

    public HashMap<String, String> get_listDataChild() {
        return _listDataChild;
    }

    public void deleteAll() {
        writeToFile("");
        _listDataChild.clear();
        _listDataHeader.clear();
    }

    public void add(String titre, String note) {
        if (!_listDataHeader.contains(titre)) {
            _listDataHeader.add(titre);
            _listDataChild.put(_listDataHeader.get(_listDataHeader.size() - 1), note);


            JSONObject obj = new JSONObject();
            try {
                obj.put("titre", titre);
                obj.put("note", note);

                filetmp = new JSONArray(readFromFile());
                filetmp.put(obj);


                writeToFile(filetmp.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("JSON content", obj.toString());
           // Log.e("file array", filetmp.toString());
            Log.e("read file", readFromFile());
        } else {
            Log.e("Titre deja cree", titre);
        }
    }

    private void writeToFile(String data) {
        Log.e("Write file", data);
        try {
            FileOutputStream fos =  MainActivity.contextTodoList.openFileOutput("test.txt", Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = MainActivity.contextTodoList.openFileInput("test.txt");
            if (inputStream != null) {

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }


                ret = stringBuilder.toString();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void prepareListData() {
        Log.e("Preparing liste", "vide");
        _listDataHeader = new ArrayList<String>();
        _listDataChild = new HashMap<String, String>();


        try {
            Log.e("File content", readFromFile());
            filetmp = new JSONArray(readFromFile());
            JSONObject obj = new JSONObject();
            for (int i = 0; i < filetmp.length(); i++) {
                obj = filetmp.getJSONObject(i);
                String titre = obj.get("titre").toString();
                String note = obj.get("note").toString();
                _listDataHeader.add(titre);
                _listDataChild.put(_listDataHeader.get(i), note);
                Log.e("liste chargée", _listDataHeader.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
