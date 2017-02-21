package com.murva.murvapes;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import Models.Recipe;
import MurvaTools.GlobalData;
import MurvaTools.MurvaTools;
import RecyclerClasses.RecipeAdapter;

/**
 * Created by Johan Rasmussen on 2016-11-02.
 */

public class RecipeFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View myView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.recipe_feed_layout, container, false);

        mRecyclerView = (RecyclerView) myView.findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(myView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new RecipeAdapter(myView.getContext(), getRecipeList());
        mRecyclerView.setAdapter(mAdapter);

        final RecipeFeedFragment tempFragment = this;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                List<Recipe> tempRecipeList = new ArrayList<Recipe>();

                // Scrolled to bottom, load more recipes
                if (recyclerView.getScrollState() == View.SCROLL_INDICATOR_BOTTOM) {
                    if(MainActivity.feedState == 0) {
                        tempRecipeList = GlobalData.recipes;
                        MurvaTools.FetchRecipesByPage(tempRecipeList.size() / 10 + 1, null, tempFragment);
                    }
                }

            }
        });

        return myView;
    }

    public List<Recipe> getRecipeList(){
        List<Recipe> tempRecipeList = new ArrayList<Recipe>();

        if(MainActivity.feedState == 0)
            tempRecipeList = GlobalData.recipes;
        else if(MainActivity.feedState == 1)
            tempRecipeList = GlobalData.favouriteRecipes;
        else
            tempRecipeList = GlobalData.myRecipes;

        List<Recipe> recipeList = new ArrayList<Recipe>();
        for(int i = 0; i < tempRecipeList.size(); i++) {
            if(tempRecipeList.get(i).name.toLowerCase().contains(MainActivity.searchString.toLowerCase()) || MainActivity.searchString.isEmpty()) {
                recipeList.add(tempRecipeList.get(i));
            }
        }
        return recipeList;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {

        if(MainActivity.feedState == 0) {
            MurvaTools.FetchRecipesByPage(1, null, this);
            GlobalData.recipes = new ArrayList<Recipe>();
        }
        else if(MainActivity.feedState == 1) {
            GlobalData.favouriteRecipes = new ArrayList<Recipe>();
            MurvaTools.FetchFavoriteRecipes(new MurvaTools.MurvaCallback() {
                @Override
                public void callback() {
                    MurvaTools.SaveRecipeListToDisk(GlobalData.favouriteRecipes, myView.getContext().getString(R.string.recipe_storage_favourite), myView.getContext());
                }
            });
        }
        else {
            GlobalData.myRecipes = new ArrayList<Recipe>();
            MurvaTools.FetchRecipesByAccountId(GlobalData.User.id, this);
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    public RecipeAdapter GetRecipeAdapter(){
        return mAdapter;
    }

}
