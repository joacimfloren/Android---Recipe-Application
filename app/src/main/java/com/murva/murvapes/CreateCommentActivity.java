package com.murva.murvapes;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;

import Models.Comment;
import MurvaTools.MurvaTools;
import MurvaTools.GlobalData;

public class CreateCommentActivity extends AppCompatActivity {

    private CreateCommentActivity activity;
    private int recipeId;
    private String userId = GlobalData.tokenDecoded.userId;
    private boolean addImg = true;

    private static final int SELECT_PICTURE = 100;
    private View theView;
    ImageView imgView;
    String imgPath;
    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);
        activity = this;

        Bundle extras = getIntent().getExtras();
        this.recipeId = extras.getInt("id");

        Spinner dropdown = (Spinner)findViewById(R.id.spinnerGrade);
        String[] items = new String[]{ "1", "2", "3", "4", "5" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        theView = findViewById(R.id.activity_create_comment);
        imgView = (ImageView) this.findViewById(R.id.commentImg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    imgUri = selectedImageUri;
                    imgPath = selectedImageUri.toString();
                    Log.d("img", "Image Path : " + imgPath);
                    // Set the image in ImageView
                    imgView.setImageURI(selectedImageUri);

                    Button addButton = (Button) this.findViewById(R.id.addImgBtn);
                    addButton.setBackgroundResource(R.drawable.ic_cross);

                    addImg = false;
                }
            }
        }
    }

    public void addImage(View view) {

        if (addImg) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }
        else {
            imgView.setImageResource(R.drawable.placeholder_recipe);

            Button addButton = (Button) this.findViewById(R.id.addImgBtn);
            addButton.setBackgroundResource(R.drawable.ic_icon_plus);

            addImg = true;
        }
    }

    public void postComment(final View view) {
        EditText commenttxt = (EditText) this.findViewById(R.id.comment);

        if (commenttxt.getText().length() < 10) {
            MurvaTools.showSnackbar(view, "Comment is to short");
        }

        else if (commenttxt.getText().length() > 400)
        {
            MurvaTools.showSnackbar(view, "Comment is to long");
        }

        else {
            Spinner spinner = (Spinner) this.findViewById(R.id.spinnerGrade);
            int grade = Integer.parseInt(spinner.getSelectedItem().toString());
            Comment comment = new Comment(commenttxt.getText().toString(), grade, userId, recipeId);
            MurvaTools.CreateComment(new MurvaTools.CreateCommentCallback() {
                @Override
                public void callback(Pair<Integer, String> response) {
                    if (response.second == null) {
                        uploadImageToComment(response.first);
                    }
                    else {
                        MurvaTools.showSnackbar(view, response.second);
                    }
                }
            }, recipeId, comment);
        }
    }

    public void uploadImageToComment(int commentId) {
        if (commentId > -1 && !addImg) {
            try {
                String url = "comments/" + commentId + "/image";
                MurvaTools.PutImage(url, getContentResolver().openInputStream(imgUri), new MurvaTools.PutImageCallback() {
                    @Override
                    public void callback(Pair<Boolean, String> result) {
                        if (result.first) {
                            //MurvaTools.showSnackbar(theView, "Success!");
                            activity.finish();
                        }
                        else {
                            MurvaTools.showSnackbar(theView, "Image wasn't uploaded.");
                        }
                    }
                });
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            activity.finish();
        }
    }
}
