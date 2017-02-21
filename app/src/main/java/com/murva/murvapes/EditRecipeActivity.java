package com.murva.murvapes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import Library.DirectionList;
import Models.Direction;
import Models.DirectionCreation;
import Models.Recipe;
import MurvaTools.MurvaTools;
import RecyclerClasses.DirectionAdapter;

public class EditRecipeActivity extends AppCompatActivity {

    private EditRecipeActivity activity;
    private Recipe recipe;
    private DirectionAdapter adapter;
    private DirectionList directionList;
    private EditText recipeName;
    private EditText recipeDescription;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_create);
        activity = this;

        Button createButton = (Button) findViewById(R.id.createButton);
        Button updateButton = (Button) findViewById(R.id.updateButton);
        RelativeLayout imageContainer = (RelativeLayout) findViewById(R.id.commentImageContainer);
        createButton.setVisibility(View.GONE);
        updateButton.setVisibility(View.VISIBLE);
        imageContainer.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        recipe = gson.fromJson(extras.getString("object"), Recipe.class);

        directionList = DirectionList.convertFromDList(recipe.directions);
        recipeName = (EditText) findViewById(R.id.input_layout_name);
        recipeDescription = (EditText) findViewById(R.id.input_layout_description);
        recipeName.setText(recipe.name);
        recipeDescription.setText(recipe.description);

        this.adapter = new DirectionAdapter(directionList);
        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.direction_recyclerView);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
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

    public void updateRecipe(final View view) {
        boolean somethingNew = false;
        if (!somethingNew && !recipeName.getText().toString().equals(recipe.name)) {
            somethingNew = true;
            recipe.name = recipeName.getText().toString();
        }
        if (!somethingNew && !recipeDescription.getText().toString().equals(recipe.description)) {
            somethingNew = true;
            recipe.description = recipeDescription.getText().toString();
        }

        if (directionList.size() != recipe.directions.size()) {
            somethingNew = true;
            recipe.directions = DirectionList.convertToOriginalDirectionList(directionList);
        }
        else {
            for (int i = 0; i < directionList.size(); i++) {
                if (!directionList.get(i).description.equals(recipe.directions.get(i).description)) {
                    somethingNew = true;
                    recipe.directions = DirectionList.convertToOriginalDirectionList(directionList);
                    break;
                }
            }
        }

        if (somethingNew) {
            MurvaTools.Update("recipes/" + Integer.toString(recipe.id), gson.toJson(recipe), new MurvaTools.ErrorCallback() {
                @Override
                public void callback(String error) {
                    if (error == null) {
                        activity.finish();
                    }
                    else {
                        MurvaTools.showSnackbar(view, error);
                    }
                }
            });
        }
        else {
            MurvaTools.showSnackbar(view, "Nothing to change.");
        }
    }
}
