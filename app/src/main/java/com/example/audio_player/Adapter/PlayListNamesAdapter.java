package com.example.audio_player.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audio_player.Model.PlaylistModel;
import com.example.audio_player.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayListNamesAdapter extends RecyclerView.Adapter<PlayListNamesAdapter.ViewHolder> {

    private List<PlaylistModel> playlistNames;
    private ClickEvent clickEvent;


    //interface of click event
    public interface ClickEvent {
        void onItemClick(PlaylistModel playlistName);
    }

    //constructor
    public PlayListNamesAdapter(List<PlaylistModel> playlistNames, ClickEvent clickEvent) {
        this.playlistNames = playlistNames;
        this.clickEvent = clickEvent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_playlist_names, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistModel playlistName = playlistNames.get(position);
        holder.playlistName.setText(playlistName.getTitle());
        Picasso.get().load(playlistName.getThumbnailUrl()).into(holder.playlistImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEvent.onItemClick(playlistName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        ImageView playlistImage;

        public ViewHolder(View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlistName);
            playlistImage = itemView.findViewById(R.id.playlistImage);
        }
    }
}
