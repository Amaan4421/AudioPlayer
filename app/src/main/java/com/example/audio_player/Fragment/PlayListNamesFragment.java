package com.example.audio_player.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audio_player.Adapter.PlayListNamesAdapter;
import com.example.audio_player.Model.PlaylistModel;
import com.example.audio_player.R;
import com.example.audio_player.database_helper.PlayListDatabaseHelper;

import java.util.List;

public class PlayListNamesFragment extends Fragment {

    private RecyclerView playlistView;
    private TextView noPlaylists;
    private PlayListNamesAdapter playListAdapter;
    private PlayListDatabaseHelper dbHelper;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_playlist_names, container, false);

        playlistView = view.findViewById(R.id.playlistView);
        noPlaylists = view.findViewById(R.id.found);
        playlistView.setLayoutManager(new LinearLayoutManager(getContext()));

        //initializing database helper
        dbHelper = new PlayListDatabaseHelper(getContext());

        //check first table is created or not
        dbHelper.isTableFound();

        //load playlist names from the database
        loadPlaylistNames();

        return view;
    }//end of method


    //method to get playlist names
    private void loadPlaylistNames()
    {
        //call method from database
        List<PlaylistModel> playlistNames = dbHelper.getAllPlaylistNames();
        if (playlistNames == null || playlistNames.isEmpty())
        {
            noPlaylists.setVisibility(View.VISIBLE);
            playlistView.setVisibility(View.GONE);
        }//end of if
        else
        {
            noPlaylists.setVisibility(View.GONE);
            playlistView.setVisibility(View.VISIBLE);

            //set the adapter to show playlist names
            playListAdapter = new PlayListNamesAdapter(playlistNames, new PlayListNamesAdapter.ClickEvent() {
                @Override
                public void onItemClick(PlaylistModel playlistName)
                {
                    //open that playlist by passing data
                    PlayListSongsFragment playListSongsFragment = new PlayListSongsFragment();
                    Bundle args = new Bundle();
                    args.putString("playlistName", playlistName.getTitle());
                    args.putString("playlistImageUrl", playlistName.getThumbnailUrl());
                    playListSongsFragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, playListSongsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            playlistView.setAdapter(playListAdapter);
        }//end of else
    }//end of method
}//end of class

