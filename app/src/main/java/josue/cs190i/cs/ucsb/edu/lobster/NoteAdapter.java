package josue.cs190i.cs.ucsb.edu.lobster;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danielle on 6/2/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    List<String> names = new ArrayList<String>();
    List<String> contents = new ArrayList<>();
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_single_item, parent, false);
        NoteAdapter.ViewHolder viewHolder = new NoteAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.note_content.setText(contents.get(position));
        holder.person_name.setText(names.get(position));
        Log.d("bindview", "inside on bind view holder");


    }

    public void add(String name, String content){
        names.add(name);
        contents.add(content);
        Log.d("add", "added to the recycler view");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView person_name;
        TextView note_content;
        ImageView note_picture;

        public ViewHolder(View view) {
            super(view);
            person_name = (TextView) view.findViewById(R.id.person_name);
            note_content = (TextView) view.findViewById(R.id.note_content);
            note_picture = (ImageView) view.findViewById(R.id.note_picture);


        }
    }
}

