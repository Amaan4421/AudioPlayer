package com.example.audio_player.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audio_player.Model.UpNextSongsModel;
import com.example.audio_player.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UpNextSongAdapter extends RecyclerView.Adapter<UpNextSongAdapter.VideoViewHolder> {

    private List<UpNextSongsModel> songList;
    private OnSongClickListener listener;

    //interface for handling clicks
    public interface OnSongClickListener {
        void onSongClicked(int position);
    }

    //constructor with the listener parameter
    public UpNextSongAdapter(List<UpNextSongsModel> songList, OnSongClickListener listener) {
        this.songList = songList;
        this.listener = listener;
    }

    //set the layout
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_upnext_songs_list, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position)
    {
        UpNextSongsModel song = songList.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songDuration.setText(song.getDuration());
        Picasso.get().load(song.getThumbnailUrl()).into(holder.thumbnail);

        //song item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onSongClicked(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, songDuration;
        ImageView thumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            songDuration = itemView.findViewById(R.id.song_duration);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }//end of method
    }//end of method
}//end of class
