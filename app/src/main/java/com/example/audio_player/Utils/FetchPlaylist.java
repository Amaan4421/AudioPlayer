package com.example.audio_player.Utils;

import android.os.AsyncTask;

import com.example.audio_player.Model.PlaylistModel;
import com.example.audio_player.database_helper.PlayListDatabaseHelper;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.PlaylistItem;

import java.io.IOException;
import java.util.List;

public class FetchPlaylist
{
    //global variables
    private YouTube youTube;
    private String playlistId;
    private PlayListDatabaseHelper dbHelper;

    //constructor
    public FetchPlaylist(YouTube youTube, String playlistId, PlayListDatabaseHelper dbHelper) {
        this.youTube = youTube;
        this.playlistId = playlistId;
        this.dbHelper = dbHelper;
    }

    //callback method to fetch playlist details
    public void fetchPlaylistDetails(FetchPlaylistCallback callback) {
        new FetchPlaylistTask(callback).execute();
    }

    //asyncTask to fetch the playlist
    private class FetchPlaylistTask extends AsyncTask<Void, Void, PlaylistModel> {
        private FetchPlaylistCallback callback;

        //constructor
        public FetchPlaylistTask(FetchPlaylistCallback callback) {
            this.callback = callback;
        }

        @Override
        protected PlaylistModel doInBackground(Void... voids) {
            try
            {
                //fetch the playlist details from youtube by playlist id
                YouTube.Playlists.List request = youTube.playlists().list("snippet");
                request.setId(playlistId);
                PlaylistListResponse response = request.execute();

                List<Playlist> playlists = response.getItems();
                if (!playlists.isEmpty())
                {
                    Playlist playlist = playlists.get(0);
                    String title = playlist.getSnippet().getTitle();

                    //check the higher level of image resolution is available or not
                    String thumbnailUrl = null;
                    if(playlist.getSnippet().getThumbnails().getMaxres() != null)   //highest resolution
                    {
                        thumbnailUrl = playlist.getSnippet().getThumbnails().getMaxres().getUrl();
                    }
                    else if (playlist.getSnippet().getThumbnails().getStandard() != null)   //standard resolution
                    {
                        thumbnailUrl = playlist.getSnippet().getThumbnails().getStandard().getUrl();
                    }
                    else if (playlist.getSnippet().getThumbnails().getHigh() != null)   //high resolution
                    {
                        thumbnailUrl = playlist.getSnippet().getThumbnails().getHigh().getUrl();
                    }
                    else if (playlist.getSnippet().getThumbnails().getMedium() != null)    //medium resolution
                    {
                        thumbnailUrl = playlist.getSnippet().getThumbnails().getMedium().getUrl();
                    }
                    else    //default resolution(lowest)
                    {
                        thumbnailUrl = playlist.getSnippet().getThumbnails().getDefault().getUrl();
                    }

                    //create PlaylistModel for the playlist
                    PlaylistModel playlistModel = new PlaylistModel(playlistId, title, thumbnailUrl);

                    //fetch playlist items and store them in the database
                    fetchAndStorePlaylistItems(title, thumbnailUrl);

                    return playlistModel;
                }//end of if
            }//end of try block
            catch (IOException e)
            {
                e.printStackTrace();
            }//end of catch block
            return null;
        }

        //fetch and store song details in database
        private void fetchAndStorePlaylistItems(String title, String playlistThumbnailUrl)
        {
            try
            {
                //fetch playlist items
                YouTube.PlaylistItems.List request = youTube.playlistItems().list("snippet,contentDetails");
                request.setPlaylistId(playlistId);
                request.setMaxResults(300L);
                List<PlaylistItem> items = request.execute().getItems();

                //store each item in the database
                for (PlaylistItem item : items)
                {
                    String videoId = item.getContentDetails().getVideoId();
                    String songTitle = item.getSnippet().getTitle();

                    //check the higher level of image resolution is available or not
                    String thumbnailUrl = null;
                    if(item.getSnippet().getThumbnails().getMaxres() != null)   //highest resolution
                    {
                        thumbnailUrl = item.getSnippet().getThumbnails().getMaxres().getUrl();
                    }
                    else if (item.getSnippet().getThumbnails().getStandard() != null)   //standard resolution
                    {
                        thumbnailUrl = item.getSnippet().getThumbnails().getStandard().getUrl();
                    }
                    else if (item.getSnippet().getThumbnails().getHigh() != null)   //high resolution
                    {
                        thumbnailUrl = item.getSnippet().getThumbnails().getHigh().getUrl();
                    }
                    else if (item.getSnippet().getThumbnails().getMedium() != null)    //medium resolution
                    {
                        thumbnailUrl = item.getSnippet().getThumbnails().getMedium().getUrl();
                    }
                    else    //default resolution(lowest)
                    {
                        thumbnailUrl = item.getSnippet().getThumbnails().getDefault().getUrl();
                    }
                    String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                    //fetch video duration
                    String duration = fetchVideoDuration(videoId);

                    //create a PlaylistModel for each song/video
                    PlaylistModel playlistModel = new PlaylistModel(videoId, songTitle, thumbnailUrl, duration, videoUrl);
                    dbHelper.addSongToPlaylist(playlistModel, title, playlistThumbnailUrl);
                }//end of if
            }//end of try block
            catch (IOException e)
            {
                e.printStackTrace();
            }//end of catch block
        }

        //fetching video duration method
        private String fetchVideoDuration(String videoId) {
            return "00:00";
        }

        @Override
        protected void onPostExecute(PlaylistModel playlistModel)
        {
            if (playlistModel != null)
            {
                callback.onFetchPlaylist(playlistModel);
            }
            else
            {
                callback.onError("Failed to fetch playlist");
            }
        }//end of method
    }//end of method

    //callback interface
    public interface FetchPlaylistCallback
    {
        void onFetchPlaylist(PlaylistModel playlistModel);
        void onError(String error);
    }//end of interface
}//end of class
