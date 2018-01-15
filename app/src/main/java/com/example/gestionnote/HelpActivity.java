package com.example.gestionnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Victor on 15/01/2018.
 */

public class HelpActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        TextView textView = (TextView) findViewById(R.id.helpTextView);

        ImageSpan imageAdd = new ImageSpan(this, android.R.drawable.ic_input_add);
        ImageSpan imageDelete = new ImageSpan(this, android.R.drawable.ic_delete);
        SpannableString spannableString = new SpannableString(textView.getText());

        int start1 = 58;
        int end1 = 59;
        int flag1 = 0;
        spannableString.setSpan(imageAdd, start1, end1, flag1);

        int start2 = 115;
        int end2 = 116;
        int flag2 = 0;
        spannableString.setSpan(imageDelete, start2, end2, flag2);

        textView.setText(spannableString);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle(""); // set the top title
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            //Quand on clique sur la flèche retour
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
