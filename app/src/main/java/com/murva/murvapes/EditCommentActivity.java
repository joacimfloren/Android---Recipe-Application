package com.murva.murvapes;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.net.MalformedURLException;

import Models.CommentOnRecipe;
import MurvaTools.GlobalData;
import MurvaTools.MurvaTools;

public class EditCommentActivity extends AppCompatActivity {

    EditCommentActivity activity;
    String initialText;
    int initialGrade;
    CommentOnRecipe comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);
        activity = this;
        Gson gson = new Gson();

        Bundle bundle = getIntent().getExtras();
        String commentString = bundle.getString("comment");
        comment = gson.fromJson(commentString, CommentOnRecipe.class);

        initialText = comment.text;
        initialGrade = comment.grade;

        EditText editText = (EditText) findViewById(R.id.comment);
        editText.setText(initialText);

        Spinner dropdown = (Spinner)findViewById(R.id.spinnerGrade);
        String[] items = new String[]{ "1", "2", "3", "4", "5" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(initialGrade - 1);

        RelativeLayout imgContainer = (RelativeLayout) findViewById(R.id.commentImageContainer);
        imgContainer.setVisibility(View.GONE);
        Button addButton = (Button) findViewById(R.id.addCommentButton);
        addButton.setVisibility(View.GONE);
        LinearLayout updateContainer = (LinearLayout) findViewById(R.id.updateCommentContainer);
        updateContainer.setVisibility(View.VISIBLE);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void updateComment(final View view) {
        EditText editText = (EditText) findViewById(R.id.comment);
        Spinner gradeDropdown = (Spinner) findViewById(R.id.spinnerGrade);
        if (editText.getText().toString().equals(initialText) && gradeDropdown.getSelectedItemPosition() == (initialGrade - 1)) {
            MurvaTools.showSnackbar(view, "Nothing to change.");
        }
        else {
            comment.text = editText.getText().toString();
            comment.grade = gradeDropdown.getSelectedItemPosition() + 1;
            Gson gson = new Gson();
            MurvaTools.Update("comments/" + comment.id, gson.toJson(comment), new MurvaTools.ErrorCallback() {
                @Override
                public void callback(String error) {
                    if (error == null) {
                        activity.finish();
                        //MurvaTools.showSnackbar(view, "Success!");
                    }
                    else {
                        MurvaTools.showSnackbar(view, error);
                    }
                }
            });
        }
    }

    public void deleteComment(final View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        MurvaTools.Delete("comments/" + Integer.toString(comment.id), new MurvaTools.ErrorCallback() {
                            @Override
                            public void callback(String error) {
                                if(error != null) {
                                    MurvaTools.showSnackbar(view, error);
                                }
                                else {
                                    activity.finish();
                                    //MurvaTools.showSnackbar(view, "Success!");
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
