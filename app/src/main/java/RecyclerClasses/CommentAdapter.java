package RecyclerClasses;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.murva.murvapes.CreateCommentActivity;
import com.murva.murvapes.EditCommentActivity;
import com.murva.murvapes.R;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

import Models.Comment;
import Models.CommentOnRecipe;
import Models.Direction;
import MurvaTools.MurvaTools;
import MurvaTools.GlobalData;

/**
 * Created by joacimfloren on 2016-11-22.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<CommentOnRecipe> comments;
    public static View theView;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView username;
        public TextView comment;
        public TextView grade;
        public Typeface font;
        public Resources res;
        public TableRow cell;

        public ViewHolder(View itemView) {
            super(itemView);

            theView = itemView;
            this.cell = (TableRow) itemView.findViewById(R.id.commentCell);
            this.img = (ImageView) itemView.findViewById(R.id.commentImage); //TODO - Hämta bild från nätet här
            this.username = (TextView) itemView.findViewById(R.id.commenterName);
            this.comment = (TextView) itemView.findViewById(R.id.commentText);
            this.grade = (TextView) itemView.findViewById(R.id.gradeText);

            this.font = Typeface.createFromAsset( itemView.getResources().getAssets(), "fonts/fontawesome-webfont.ttf" );
            this.res = itemView.getResources();
        }
    }

    public CommentAdapter(List<CommentOnRecipe> comments) {
        this.comments = comments;
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_recipeview_comments, parent, false);
        CommentAdapter.ViewHolder vh = new CommentAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        final CommentAdapter ca = this;
        final CommentOnRecipe comment = this.comments.get(position);

        Glide.with(theView.getContext())
                .load(comment.image)
                .placeholder(R.drawable.placeholder_recipe)
                .error(R.drawable.placeholder_recipe)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.img);

        holder.username.setText(comment.commenter.userName);
        holder.comment.setText(comment.text);
        holder.grade.setText(holder.res.getString(R.string.recipe_grade_icon) + " " + Integer.toString(comment.grade));
        holder.grade.setTypeface(holder.font);

        holder.cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (comment.commenter.id == GlobalData.User.id){
                if (comment.commenter.id.equals(GlobalData.User.id)){
                    Intent intent = new Intent(theView.getContext(), EditCommentActivity.class);
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    String c = gson.toJson(comment);
                    bundle.putString("comment", c);
                    bundle.putBoolean("edit", true);

                /*
                bundle.putInt("id", comment.id);
                bundle.putInt("grade", comment.grade);
                bundle.putString("text", comment.text);
                bundle.putString("image", comment.image);
                bundle.putString("userId", comment.commenter.id);
                bundle.putString("userName", comment.commenter.userName);*/
                    //Sätt in saker i putextra(Bundle)
                    intent.putExtras(bundle);
                    theView.getContext().startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.comments.size();
    }
}

