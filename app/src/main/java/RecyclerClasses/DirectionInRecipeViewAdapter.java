package RecyclerClasses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.murva.murvapes.R;

import org.w3c.dom.Text;

import java.util.List;

import Models.Direction;

/**
 * Created by joacimfloren on 2016-11-22.
 */

public class DirectionInRecipeViewAdapter extends RecyclerView.Adapter<DirectionInRecipeViewAdapter.ViewHolder> {
    private List<Direction> directions;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView orderTxt;
        public TextView directionTxt;

        public ViewHolder(View itemView) {
            super(itemView);

            // Assign to attributes
            this.orderTxt = (TextView) itemView.findViewById(R.id.orderNo);
            this.directionTxt = (TextView) itemView.findViewById(R.id.descriptionText);
        }
    }

    public DirectionInRecipeViewAdapter(List<Direction> directions) {
        this.directions = directions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_recipeview_directions, parent, false);
        DirectionInRecipeViewAdapter.ViewHolder vh = new DirectionInRecipeViewAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DirectionInRecipeViewAdapter.ViewHolder holder, int position) {
        final DirectionInRecipeViewAdapter da = this;
        final Direction direction = this.directions.get(position);
        holder.orderTxt.setText(Integer.toString(direction.order));
        holder.directionTxt.setText(direction.description);
    }

    @Override
    public int getItemCount() {
        return this.directions.size();
    }
}
