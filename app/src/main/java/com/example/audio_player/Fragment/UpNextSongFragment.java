package com.example.audio_player.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.audio_player.Activity.PlayAudio;
import com.example.audio_player.Adapter.UpNextSongAdapter;
import com.example.audio_player.Model.UpNextSongsModel;
import com.example.audio_player.R;
import com.example.audio_player.Utils.AudioExtractor;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpNextSongFragment extends BottomSheetDialogFragment {

    private List<UpNextSongsModel> songList;
    private final HashMap<String, String> preFetchedSongUrls = new HashMap<>();


    //constructor to get songs list from other activity
    public UpNextSongFragment(List<UpNextSongsModel> songList) {
        this.songList = songList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_up_next_song, container, false);
        RecyclerView queueList = view.findViewById(R.id.queueList);
        queueList.setLayoutManager(new LinearLayoutManager(getContext()));
        Intent intent = new Intent(getContext(), PlayAudio.class);

        //extract song url one by one and store
        AudioExtractor audioExtractor = new AudioExtractor(getContext());
        if (!songList.isEmpty())
        {
            for (UpNextSongsModel song : songList)
            {
                audioExtractor.preFetchSongUrl(song.getVideoUrl(), new AudioExtractor.SongUrlCallback() {
                    @Override
                    public void onSongUrlExtracted(String audioUrl)
                    {
                        //store url in hashmap
                        preFetchedSongUrls.put(song.getVideoUrl(), audioUrl);
                    }
                });//end of method
            }//end of for
        }//end of if

        //pass extracted url to other activity to play song
        ArrayList<String> audioUrls = new ArrayList<>(preFetchedSongUrls.values());
        intent.putStringArrayListExtra("audioUrls", audioUrls);


        //set the adapter to show songs
        UpNextSongAdapter adapter = new UpNextSongAdapter(songList, new UpNextSongAdapter.OnSongClickListener() {
            @Override
            public void onSongClicked(int position)
            {
                //get song url for playing, if found
                String videoUrl = songList.get(position).getVideoUrl();
                String audioUrl = preFetchedSongUrls.get(videoUrl);

                //if not null then play song by passing intent
                if (audioUrl != null)
                {
                    intent.putExtra("audioUrl", audioUrl);
                    intent.putExtra("title", songList.get(position).getTitle());
                    intent.putExtra("image", songList.get(position).getThumbnailUrl());
                    startActivity(intent);
                }//end of if
                else
                {
                    Toast.makeText(getContext(), "Audio URL is not yet available. Please wait.", Toast.LENGTH_SHORT).show();
                }//end of else
            }//end of method
        });//end of method

        queueList.setAdapter(adapter);
        return view;
    }//end of onCreate
}//end of class