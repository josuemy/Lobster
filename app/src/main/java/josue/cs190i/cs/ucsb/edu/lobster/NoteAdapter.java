package josue.cs190i.cs.ucsb.edu.lobster;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Danielle on 6/2/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    List<Note> notes = new ArrayList<Note>();
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_single_item, parent, false);
        NoteAdapter.ViewHolder viewHolder = new NoteAdapter.ViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("bindview", "inside on bind view holder");

        holder.note_content.setText(notes.get(position).content);
        holder.person_name.setText(notes.get(position).person_name);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        holder.note_time.setText(currentDateTimeString);

        holder.note_picture.setImageBitmap(notes.get(position).picture);
        if(holder.note_picture.getDrawable() != null){
            holder.note_picture.setVisibility(View.VISIBLE);
        }
        holder.note_category.setText(notes.get(position).category);


    }

    public void add(Note note){
        notes.add(note);
        Log.d("add", "added to the recycler view");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView person_name;
        TextView note_content;
        TextView note_time;
        TextView note_category;
        ImageView note_picture;


        public ViewHolder(View view) {
            super(view);
            person_name = (TextView) view.findViewById(R.id.person_name);
            note_content = (TextView) view.findViewById(R.id.note_content);
            note_picture = (ImageView) view.findViewById(R.id.note_picture);
            note_category = (TextView) view.findViewById(R.id.note_category);
            note_time = (TextView) view.findViewById(R.id.note_date_time);

        }
    }
}
