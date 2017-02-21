package com.murva.murvapes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import Models.Account;
import MurvaTools.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import Mockup.Mockup;
import Models.CommentOnRecipe;
import Models.Recipe;
import Models.Comment;
import MurvaTools.MurvaTools;
import RecyclerClasses.CommentAdapter;
import RecyclerClasses.DirectionInRecipeViewAdapter;

public class RecipeActivity extends GpsActivity {

    private LinearLayout thisView;
    private Bitmap bmp;

    private Recipe recipe;
    private int recipeId;
    private Account creator;
    private DirectionInRecipeViewAdapter adapter;
    private CommentAdapter commentAdapter;
    private Menu recipeMenu;
    private RecipeActivity activity;

    private boolean starred = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        thisView = (LinearLayout) findViewById(R.id.recipeRoot);
        activity = this;

        allowClickToChangePosition = false;

        Bundle extras = getIntent().getExtras();
        this.recipeId = extras.getInt("id");

        MurvaTools.FetchRecipeById(new MurvaTools.RecipeCallback() {
            @Override
            public void callback(Recipe recipe) {
                MurvaTools.FetchCommentsOfRecipe(
                        new MurvaTools.CommentsCallback() {
                            @Override
                            public void callback(final Recipe recipe, final List<CommentOnRecipe> comments) {

                                try {
                                    MurvaTools.FetchAccountById(recipe.creator.id, new MurvaTools.AccountCallback() {
                                        @Override
                                        public void callback(Account account) {
                                            creator = account;
                                            setViewData(recipe, comments);
                                            inflateMenu();
                                            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.recipeMap);
                                            mapFragment.getMapAsync(activity);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, recipe
                );
            }
        }, this.recipeId);

        MurvaTools.FetchFavoriteRecipes(new MurvaTools.MurvaCallback() {
            @Override
            public void callback() {
                setStarIcon();
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        latLng = new LatLng(creator.longitude, creator.longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mGoogleMap.addMarker(markerOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    public void inflateMenu() {
        if (recipe.creator.id.equals(GlobalData.User.id)) {
            getMenuInflater().inflate(R.menu.menu_recipe, recipeMenu);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        recipeMenu = menu;

        return true;
    }

    public void setViewData(Recipe recipe, List<CommentOnRecipe> comments) {
        this.recipe = recipe;
        Typeface font = Typeface.createFromAsset( getAssets(), "fonts/fontawesome-webfont.ttf" );

        if (recipe.image != null) {
            ImageView recipeImg = (ImageView) this.findViewById(R.id.recipeImage);
            //GetImageFromUrl(recipe.image, recipeImg);
            Glide.with(this)
                    .load(recipe.image)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(recipeImg);
        }

        if (recipe.name != null) {
            TextView titleString = (TextView) this.findViewById(R.id.titleString);
            titleString.setText(recipe.name);
        }

        if (recipe.description != null) {
            TextView descriptionString = (TextView) this.findViewById(R.id.descriptionString);
            descriptionString.setText(recipe.description);
        }

        if (recipe.creator != null) {
            TextView authorString = (TextView) this.findViewById(R.id.authorString);
            authorString.setText(recipe.creator.userName);
        }

        LinearLayout directionsLayout = (LinearLayout) this.findViewById(R.id.directionsContent);

        if (recipe.directions != null && recipe.directions.size() > 0) {
            TextView directionPreText = (TextView) this.findViewById(R.id.noDirectionsText);
            ((ViewGroup) directionPreText.getParent()).removeView(directionPreText);

            // Init adapter
            this.adapter = new DirectionInRecipeViewAdapter(recipe.directions);

            // Set recyclerView's adapter to adapter
            RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.direction_recyclerView);
            recyclerView.setAdapter(adapter);

            // Set layout manager
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
        }

        if (comments.size() > 0) {
            TextView commentPreText = (TextView) this.findViewById(R.id.noCommentsText);
            ((ViewGroup) commentPreText.getParent()).removeView(commentPreText);

            // Init adapter
            this.commentAdapter = new CommentAdapter(comments);

            // Set recyclerView's adapter to adapter
            RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.comments_recyclerView);
            recyclerView.setAdapter(commentAdapter);

            // Set layout manager
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
        }

        TextView userIcon = (TextView) findViewById( R.id.authorTitleString );
        userIcon.setTypeface(font);
    }

    public void setStarIcon() {
        for (Recipe r: GlobalData.favouriteRecipes) {
            if (r.id == recipeId) {
                starred = true;
                break;
            }
        }

        if (starred) {
            ImageView starImg = (ImageView) findViewById(R.id.starIcon);
            starImg.setImageResource(R.drawable.star_icon_active);
        }
    }

    public void createComment(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", this.recipeId);

        Intent intent = new Intent(this, CreateCommentActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    public void starRecipe(View view) {
        ImageView starImg = (ImageView) findViewById(R.id.starIcon);

        if (!starred) {
            //Star recipe
            MurvaTools.PutFavoritesToAccount(recipe, true);

            starImg.setImageResource(R.drawable.star_icon_active);
            starred = true;
        }
        else {
            //Unstar recipe
            MurvaTools.PutFavoritesToAccount(recipe, false);

            starImg.setImageResource(R.drawable.star_icon);
            starred = false;
        }
    }

    public boolean deleteRecipe(final MenuItem item) {
        final RecipeActivity activity = this;
        new AlertDialog.Builder(thisView.getContext())
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        MurvaTools.Delete("recipes/" + Integer.toString(recipe.id), new MurvaTools.ErrorCallback() {
                            @Override
                            public void callback(String error) {
                                if(error != null) {
                                    MurvaTools.showSnackbar(thisView, error);
                                }
                                else {
                                    GlobalData.recipes.remove(recipe);
                                    GlobalData.favouriteRecipes.remove(recipe);
                                    activity.finish();
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
        return true;
    }

    public boolean updateRecipe(MenuItem item) {
        Gson gson = new Gson();
        Bundle bundle = new Bundle();
        bundle.putString("object", gson.toJson(recipe));

        Intent intent = new Intent(this, EditRecipeActivity.class);
        intent.putExtras(bundle);
        this.startActivity(intent);
        return true;
    }
}