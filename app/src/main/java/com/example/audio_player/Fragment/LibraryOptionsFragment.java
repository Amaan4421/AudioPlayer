package com.example.audio_player.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.audio_player.BuildConfig;
import com.example.audio_player.Model.PlaylistModel;
import com.example.audio_player.R;
import com.example.audio_player.Utils.FetchPlaylist;
import com.example.audio_player.database_helper.PlayListDatabaseHelper;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;

import java.io.IOException;

@SuppressLint({"MissingInflatedId", "LocalSuppress"})
public class LibraryOptionsFragment extends Fragment {

    private YouTube youTube;
    private PlayListDatabaseHelper dbHelper;
    ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        //initializing database helper
        dbHelper = new PlayListDatabaseHelper(getContext());


        //get the buttons from xml file by their ids
        Button historyButton = view.findViewById(R.id.history);
        Button playlistButton = view.findViewById(R.id.playlist);
        Button favoritesButton = view.findViewById(R.id.favorites);
        Button settingsButton = view.findViewById(R.id.settings);
        Button importPlaylist = view.findViewById(R.id.import_playlist);
        progressBar = view.findViewById(R.id.showLoading);


        //set click listeners for buttons
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "History Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, new PlayListNamesFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Favorites Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Settings Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        importPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogBox();
            }
        });

        return view;
    }


    //dialog box for importing playlist
    private void openDialogBox()
    {
        //set the layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.raw_import_playlist_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        //access EditText and Button from layout
        EditText playlistLinkInput = dialogView.findViewById(R.id.playlistLinkInput);
        Button importButton = dialogView.findViewById(R.id.importButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        //onClickListener for importButton in the dialog
        importButton.setOnClickListener(v -> {

            //playlist link variable
            String playlistLink = playlistLinkInput.getText().toString().trim();
            if (!playlistLink.isEmpty())
            {
                //get id from that link
                String playlistId = getPlaylistIdFromUrl(playlistLink);  // Extract playlist ID
                if (playlistId != null)
                {
                    dialog.dismiss();
                    progressBar.setVisibility(View.VISIBLE);

                    //get the api key from gradle file
                    String api_key = BuildConfig.API_KEY;


                    //create object of youtube to make http request and pass the api key
                    youTube = new YouTube.Builder(
                            new com.google.api.client.http.javanet.NetHttpTransport(),
                            new com.google.api.client.json.jackson2.JacksonFactory(),
                            new HttpRequestInitializer() {
                                @Override
                                public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
                                }
                            }
                    ).setYouTubeRequestInitializer(new YouTubeRequestInitializer(api_key))
                            .setApplicationName(getString(R.string.app_name)).build();

                    //fetch the playlist songs from youtube
                    FetchPlaylist fetchPlaylist = new FetchPlaylist(youTube, playlistId, dbHelper);
                    fetchPlaylist.fetchPlaylistDetails(new FetchPlaylist.FetchPlaylistCallback() {
                        @Override
                        public void onFetchPlaylist(PlaylistModel playlistModel)
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Playlist Added: " + playlistModel.getTitle(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error)
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }//end of if
                else
                {
                    Toast.makeText(getContext(), "Invalid playlist link!!!", Toast.LENGTH_SHORT).show();
                }//end of else
            }//end of if
            else
            {
                Toast.makeText(getContext(), "Please enter playlist link!!!", Toast.LENGTH_SHORT).show();
            }//end of else
        });
    }//end of method


    //method to extract playlist ID from the link provided by user
    private String getPlaylistIdFromUrl(String url)
    {
        Uri uri = Uri.parse(url);
        return uri.getQueryParameter("list");
    }//end of method
}//end of class
