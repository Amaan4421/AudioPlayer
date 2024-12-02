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

import java.util.ArrayList;

public class PlayListSongsAdapter extends RecyclerView.Adapter<PlayListSongsAdapter.ViewHolder> {

    private final ArrayList<PlaylistModel> songs;

    //constructor
    public PlayListSongsAdapter(ArrayList<PlaylistModel> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public PlayListSongsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListSongsAdapter.ViewHolder holder, int position) {
        PlaylistModel song = songs.get(position);
        holder.songTitle.setText(song.getSongTitle());
        holder.songDuration.setText(song.getDuration());
        Picasso.get().load(song.getSongThumbnailUrl()).into(holder.songImage);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView songTitle, songDuration;
        ImageView songImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.audio_title);
            songImage = itemView.findViewById(R.id.videoImage);
            songDuration = itemView.findViewById(R.id.audio_duration);
        }
    }
}
