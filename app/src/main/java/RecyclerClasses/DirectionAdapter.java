package RecyclerClasses;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.murva.murvapes.R;

import Library.DirectionEditText;
import Library.DirectionList;
import Library.DirectionListInfo;
import Models.Direction;
import Models.DirectionCreation;

/**
 * Created by rikardolsson on 2016-11-17.
 */

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.ViewHolder> {

    final private DirectionList directions;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv;
        public DirectionEditText et;
        public ImageButton ib;

        public ViewHolder(View itemView) {
            super(itemView);

            // Assign to attributes
            this.tv = (TextView) itemView.findViewById(R.id.direction_tv);
            this.et = (DirectionEditText) itemView.findViewById(R.id.direction_et);
            this.ib = (ImageButton) itemView.findViewById(R.id.direction_ib);
        }
    }

    public DirectionAdapter(DirectionList directions) {
        this.directions = directions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.direction_list_template, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Init adapter vars
        final DirectionAdapter da = this;
        final DirectionCreation direction = this.directions.get(position);

        // Set view vars
        holder.tv.setText(Integer.toString(direction.order));
        holder.et.setDirection(direction);

        // Init a onClickListener to remove Direction from list
        holder.ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                da.RemoveDirection(direction);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.directions.size();
    }

    public void AddDirection() {
        // Add new empty Direction to collection
        this.directions.add();

        // Notify to update view
        this.notifyDataSetChanged();
    }

    public void RemoveDirection(DirectionCreation direction) {
        // Check if not empty (actually not needed, is checked in method)
        if (this.directions.isEmpty())
            return;

        // Remove chosen direction
        if (this.directions.remove(direction.order) == DirectionListInfo.NOT_FOUND)
            Log.d("DIRECTIONS", "Direction not found.");

        // Notify to update view
        this.notifyDataSetChanged();
    }
}
