package com.example.audio_player.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audio_player.Activity.PlayAudio;
import com.example.audio_player.Adapter.PlayListSongsAdapter;
import com.example.audio_player.Model.PlaylistModel;
import com.example.audio_player.Model.UpNextSongsModel;
import com.example.audio_player.R;
import com.example.audio_player.Utils.AudioExtractor;
import com.example.audio_player.database_helper.PlayListDatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayListSongsFragment extends Fragment {

    private RecyclerView playlistRecyclerView;
    private ImageButton playAllSongs;
    private PlayListDatabaseHelper dbHelper;
    private String playlistName;
    private ProgressBar progressBar;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_playlist_songs, container, false);

        //get layout details by ids
        playlistRecyclerView = view.findViewById(R.id.playlistRecyclerView);
        TextView playlistTitle = view.findViewById(R.id.playlistTitle);
        ImageView playlistImage = view.findViewById(R.id.playlistImage);
        playAllSongs = view.findViewById(R.id.playAllButton);
        progressBar = view.findViewById(R.id.showLoading);

        //initializing database helper
        dbHelper = new PlayListDatabaseHelper(getContext());

        //get playlist name from arguments
        playlistName = getArguments() != null ? getArguments().getString("playlistName") : null;
        String playlistImageUrl = getArguments() != null ? getArguments().getString("playlistImageUrl") : null;


        //set playlist title and image
        if (playlistName != null && playlistImageUrl != null)
        {
            playlistTitle.setText(playlistName);
            Picasso.get().load(playlistImageUrl).into(playlistImage);
            loadSongsFromPlaylist();
        }
        else
        {
            Toast.makeText(getContext(), "Playlist not found", Toast.LENGTH_SHORT).show();
        }
        return view;
    }//end of onCreate


    //method to playlist songs
    private void loadSongsFromPlaylist()
    {
        //get data from database
        ArrayList<PlaylistModel> songs = dbHelper.getSongsFromPlaylist(playlistName);
        if (songs.isEmpty())
        {
            Toast.makeText(getContext(), "No songs found!!!", Toast.LENGTH_SHORT).show();
        }//end of if
        else
        {
            //set the adapter to show songs
            PlayListSongsAdapter songAdapter = new PlayListSongsAdapter(songs);
            playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            playlistRecyclerView.setAdapter(songAdapter);

            //play all button click
            playAllSongs.setOnClickListener(v -> {
                    playAllSongs(songs);
            });//end of onClick
        }//end of else
    }//end of method


    //method to play all songs together
    private void playAllSongs(ArrayList<PlaylistModel> songs)
    {
        if (!songs.isEmpty())
        {
            //extract the URL of the first song and pass other songs to model for the up-next queue
            PlaylistModel firstSong = songs.get(0);
            ArrayList<UpNextSongsModel> upNextQueue = new ArrayList<>();

            //adding all songs in model
            for (int i = 0; i < songs.size(); i++)
            {
                PlaylistModel song = songs.get(i);
                upNextQueue.add(new UpNextSongsModel(song.getSongTitle(), song.getSongThumbnailUrl(), song.getVideoUrl(), song.getDuration()));
            }

            //extract URL for the first song
            progressBar.setVisibility(View.VISIBLE);

            //pass the first song data and play that song
            Intent i = new Intent(getContext(), PlayAudio.class);
            i.putExtra("title", firstSong.getSongTitle());
            i.putExtra("image", firstSong.getSongThumbnailUrl());
            i.putParcelableArrayListExtra("upNextQueue", upNextQueue);

            //call audio extracting class to get audio url to play song
            AudioExtractor audioExtractor = new AudioExtractor(getContext());
            audioExtractor.getAudioFileUrl(firstSong.getVideoUrl(), i, progressBar);
        }//end of if
        else
        {
            Toast.makeText(getContext(), "No songs available to play!!!", Toast.LENGTH_SHORT).show();
        }//end of else
    }//end of method
}//end of class