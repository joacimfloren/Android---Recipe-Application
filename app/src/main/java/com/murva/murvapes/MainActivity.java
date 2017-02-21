package com.murva.murvapes;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import Models.Account;
import MurvaTools.*;

import java.util.ArrayList;
import java.util.List;

import Models.Recipe;
import Mockup.Mockup;
import MurvaTools.*;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    public static String searchString = "";
    public static int feedState = 0;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            createRecipe(view);
            }
        });

        // Sets recipe feed as startup fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new RecipeFeedFragment()).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View hView =  navigationView.getHeaderView(0);

        hView.setBackground(getResources().getDrawable(R.drawable.primary_color));

        MurvaTools.SaveToken(this);

        MurvaTools.FetchRecipesByPage(1, this, null);
        GlobalData.favouriteRecipes = MurvaTools.LoadRecipeListFromDisk(this.getString(R.string.recipe_storage_favourite), this);
        if (GlobalData.favouriteRecipes.isEmpty()){
            final MainActivity tempActivity = this;
            MurvaTools.FetchFavoriteRecipes(new MurvaTools.MurvaCallback() {
                @Override
                public void callback() {
                    MurvaTools.SaveRecipeListToDisk(GlobalData.favouriteRecipes, tempActivity.getString(R.string.recipe_storage_favourite), tempActivity);
                }
            });
        }

        try {
            MurvaTools.FetchAccountById(GlobalData.tokenDecoded.userId, new MurvaTools.AccountCallback() {
                @Override
                public void callback(Account account) {
                    TextView nav_user = (TextView)hView.findViewById(R.id.activeUser);
                    if(account != null) {
                        nav_user.setText(account.userName);
                        GlobalData.User = account;
                        MurvaTools.FetchRecipesByAccountId(account.id, null);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRecipe(View view){
//        Snackbar.make(view, "Create recipe, coming soon!!! BORROWEEEEEEER!!!!", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        Intent intent = new Intent(this, RecipeCreateActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recipes) {
            this.feedState = 0;
            this.fab.show();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new RecipeFeedFragment()).commit();
        } else if (id == R.id.nav_myfavourites) {
            this.feedState = 1;
            this.fab.show();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new RecipeFeedFragment()).commit();
        } else if (id == R.id.nav_myrecipes) {
            this.feedState = 2;
            this.fab.show();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new RecipeFeedFragment()).commit();
        } else if(id == R.id.nav_profile){
            this.fab.hide();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new ProfileFragment()).commit();
        } else if (id == R.id.nav_logout) {
            MurvaTools.ClearToken(this);
            Intent intent = new Intent(this, LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateRecipeFeed(){
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new RecipeFeedFragment()).commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.searchString = newText;
        // Removes any previously stacked Fragments
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, new RecipeFeedFragment()).commit();
        return false;
    }
}
