package com.example.audio_player.Utils;

import android.content.Context;

import androidx.media3.exoplayer.ExoPlayer;

public class ExoPlayerManager
{
    private static ExoPlayer player;


    //start the instance and set the player for playing song
    public static synchronized ExoPlayer getInstance(Context context) {
        if (player == null) {
            player = new ExoPlayer.Builder(context.getApplicationContext()).build();
        }
        return player;
    }


    //to release player
    public static void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}

