package com.example.audio_player.Model;

public class PlaylistModel {

    //playlist variables
    private String playlistId;
    private String title;
    private String thumbnailUrl;


    //songs variables
    private String songTitle;
    private String songThumbnailUrl;
    private String videoId;
    private String duration;
    private String videoUrl;


    //constructor for passing playlist details
    public PlaylistModel(String title, String thumbnailUrl){
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
    }


    //constructor for storing playlist details
    public PlaylistModel(String playlistId, String title, String thumbnailUrl) {
        this.playlistId = playlistId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
    }

    //constructor for storing song details
    public PlaylistModel(String videoId, String songTitle, String songThumbnailUrl, String duration, String videoUrl) {
        this.videoId = videoId;
        this.songTitle = songTitle;
        this.songThumbnailUrl = songThumbnailUrl;
        this.duration = duration;
        this.videoUrl = videoUrl;
    }


    //getters for playlist and song details
    public String getPlaylistId() {
        return playlistId;
    }

    public String getTitle() {
        return title;
    }

    public String getSongThumbnailUrl() {
        return songThumbnailUrl;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getDuration() {
        return duration;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
}
