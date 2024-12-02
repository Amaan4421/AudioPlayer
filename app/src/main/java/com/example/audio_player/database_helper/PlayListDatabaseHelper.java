package com.example.audio_player.database_helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.audio_player.Model.PlaylistModel; // Import the PlaylistModel

import java.util.ArrayList;

public class PlayListDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "audio_player.db";

    //version to keep track of database tables
    private static final int DATABASE_VERSION = 1;

    //table name
    private static final String TABLE_PLAYLIST = "playlist";

    //playlist songs column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "songTitle";
    private static final String COLUMN_IMAGE = "songImageUrl";
    private static final String COLUMN_VIDEO_ID = "songVideoId";
    private static final String COLUMN_VIDEO_DURATION = "songDuration";
    private static final String COLUMN_VIDEO_URL = "songVideoUrl";

    //Playlist-specific columns
    private static final String COLUMN_PLAYLIST_NAME = "playlistName";
    public static final String COLUMN_PLAYLIST_IMAGE_URL = "playlistImageUrl";


    //constructor
    public PlayListDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create table query
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_PLAYLIST_TABLE = "CREATE TABLE " + TABLE_PLAYLIST + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_VIDEO_ID + " TEXT, "
                + COLUMN_VIDEO_DURATION + " TEXT, "
                + COLUMN_VIDEO_URL + " TEXT, "
                + COLUMN_PLAYLIST_NAME + " TEXT, "
                + COLUMN_PLAYLIST_IMAGE_URL + " TEXT)";  // New column for playlist name
        db.execSQL(CREATE_PLAYLIST_TABLE);
    }//end of method


    //call if any update required in table(eg. adding or removing columns)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);  // Drop playlist table if exists
        onCreate(db);
    }//end of method


    //method to add a song to a playlist
    public void addSongToPlaylist(PlaylistModel playlistModel, String playlistName, String playlistImageUrl)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //first check the table exists; if not, create it
        if (isTableExists(db))
        {
            onCreate(db);
        }

        //adding values in table
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, playlistModel.getSongTitle());
        values.put(COLUMN_IMAGE, playlistModel.getSongThumbnailUrl());
        values.put(COLUMN_VIDEO_ID, playlistModel.getVideoId());
        values.put(COLUMN_VIDEO_DURATION, playlistModel.getDuration());
        values.put(COLUMN_VIDEO_URL, playlistModel.getVideoUrl());
        values.put(COLUMN_PLAYLIST_NAME, playlistName);
        values.put(COLUMN_PLAYLIST_IMAGE_URL, playlistImageUrl);
        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }//end of method



    //method to get songs from a specific playlist
    @SuppressLint("Range")
    public ArrayList<PlaylistModel> getSongsFromPlaylist(String playlistName)
    {
        ArrayList<PlaylistModel> playlistSongs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PLAYLIST + " WHERE " + COLUMN_PLAYLIST_NAME + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{playlistName});

        if (cursor.moveToFirst())
        {
            do
            {
                PlaylistModel playlistModel = new PlaylistModel(
                        cursor.getString(cursor.getColumnIndex(COLUMN_VIDEO_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_VIDEO_DURATION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_VIDEO_URL))
                );
                playlistSongs.add(playlistModel);
            } while (cursor.moveToNext());
        }//end of if

        cursor.close();

        db.close();
        return playlistSongs;
    }//end of method


    //method to delete a playlist
    public void deletePlaylist(String playlistName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYLIST, COLUMN_PLAYLIST_NAME + " = ?", new String[]{playlistName});
        db.close();
    }//end of method


    //method to get all playlist names from database
    @SuppressLint("Range")
    public ArrayList<PlaylistModel> getAllPlaylistNames()
    {
        ArrayList<PlaylistModel> playlists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT DISTINCT " + COLUMN_PLAYLIST_NAME + ", " + COLUMN_PLAYLIST_IMAGE_URL + " FROM " + TABLE_PLAYLIST;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYLIST_NAME));
                String imageUrl = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYLIST_IMAGE_URL));
                playlists.add(new PlaylistModel(name, imageUrl));
            } while (cursor.moveToNext());
            cursor.close();
        }//end of if

        db.close();
        return playlists;
    }//end of method


    //method to check the table exists or not
    private boolean isTableExists(SQLiteDatabase db)
    {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{PlayListDatabaseHelper.TABLE_PLAYLIST});
        boolean tableExists = (cursor.getCount() > 0);
        cursor.close();
        return !tableExists;
    }//end of method


    //check table is found or not
    public void isTableFound()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(isTableExists(db))
        {
            onCreate(db);
        }
    }//end of method
}//end of class
