package RecyclerClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.murva.murvapes.MainActivity;
import com.murva.murvapes.R;
import com.murva.murvapes.RecipeActivity;

import java.util.ArrayList;
import java.util.List;

import Models.Recipe;
import MurvaTools.GlobalData;
import MurvaTools.MurvaTools;

/**
 * Created by Johan Rasmussen on 2016-11-17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> mDataset;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mainLayout;
        public TextView recipeName;
        public ImageView recipeImage;

        public ViewHolder(View itemView){
            super(itemView);
            mainLayout = (RelativeLayout) itemView.findViewById(R.id.mainLayout);
            recipeName = (TextView) itemView.findViewById(R.id.recipeName);
            recipeImage = (ImageView) itemView.findViewById(R.id.imgRecipe);
        }
    }

    public RecipeAdapter(Context context, List<Recipe> recipes){
        this.context = context;
        this.mDataset = recipes;
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_template, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, int position) {
        final Recipe recipe = mDataset.get(position);
        holder.recipeName.setText(recipe.name);
            //MurvaTools.DownloadImageFromUrl(holder.recipeImage, recipe.image);
            Glide.with(context)
                    .load(recipe.image)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.recipeImage);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putInt("id", recipe.id);

                Intent intent = new Intent(context, RecipeActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateList(List<Recipe> recipes){
        this.mDataset = recipes;
        this.notifyDataSetChanged();
    }
}
