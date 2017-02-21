package com.murva.murvapes;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Library.DirectionList;
import Models.Direction;
import Models.Recipe;
import Models.RecipeCreation;
import MurvaTools.MurvaTools;
import MurvaTools.GlobalData;
import MurvaTools.Validation;
import RecyclerClasses.DirectionAdapter;

public class RecipeCreateActivity extends AppCompatActivity {

    private DirectionList directions;
    private DirectionAdapter adapter;

    private boolean addImg = true;
    private static final int SELECT_PICTURE = 100;
    private View theView;
    ImageView imgView;
    String imgPath;
    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_create);

        // Init empty direction list
        this.directions = new DirectionList();

        // Init adapter
        this.adapter = new DirectionAdapter(this.directions);

        // Set recyclerView's adapter to adapter
        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.direction_recyclerView);
        recyclerView.setAdapter(adapter);

        // Set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        theView = findViewById(R.id.activity_recipe_create);
        imgView = (ImageView) this.findViewById(R.id.commentImg);
    }

    public void addDirection(View view) {
        this.adapter.AddDirection();
    }

    private Map<String, String> getTextValues() {
        // Init a Map to map each value to corresponding EditText
        Map<String, String> dictionary = new HashMap<String, String>();

        // Get name Edit Text
        EditText nameEditText = (EditText) this.findViewById(R.id.input_layout_name);
        String name = nameEditText.getText().toString();

        // Get description Edit Text
        EditText descriptionEditText = (EditText) this.findViewById(R.id.input_layout_description);
        String description = descriptionEditText.getText().toString();

        // Set the edit text name as key and text field's value as value
        dictionary.put("name", name);
        dictionary.put("description", description);

        // Return map
        return dictionary;
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

    public void createRecipe(final View view) throws Exception {

        Map<String, String> values = this.getTextValues();

        // Init a recipe to validate
        RecipeCreation recipe = new RecipeCreation();

        // Assign the attributes
        recipe.creatorId = GlobalData.tokenDecoded.userId;
        recipe.directions = this.directions.toList();
        recipe.name = values.get("name");
        recipe.description = values.get("description");

        // Validate and get errors
        List<String> errors = Validation.validateCreateRecipe(recipe);

        // Check if validation has errors, show them to the user
        if (errors.isEmpty()) {
            // Good to go
            MurvaTools.CreateRecipe(recipe, new MurvaTools.RecipePostCallback() {
                @Override
                public void pre() {
                    // Show loading panel
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                }

                @Override
                public void post(Pair<Integer, String> response) {
                    // Show result to user
                    findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);

                    if (response.second == null) {
                        uploadImageToRecipe(response.first);
                    }
                    else {
                        MurvaTools.showSnackbar(view, response.second);
                    }
                }
            });

        } else {
            // This will print an error one at the time. Little more struggle for the user.. but sometimes that's life.
            MurvaTools.showSnackbar(view, errors.get(0));
        }

    }

    public void uploadImageToRecipe(int recipeId) {
        final RecipeCreateActivity rca = this;

        if (recipeId > -1 && addImg == false) {
            try {
                String url = "recipes/" + recipeId + "/image";
                MurvaTools.PutImage(url, getContentResolver().openInputStream(imgUri), new MurvaTools.PutImageCallback() {
                    @Override
                    public void callback(Pair<Boolean, String> result) {
                        if (result.first) {
                            //MurvaTools.showSnackbar(theView, "Success!");
                            rca.finish();
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
    }
}
