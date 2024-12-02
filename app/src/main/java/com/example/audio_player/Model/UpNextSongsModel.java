package com.example.audio_player.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UpNextSongsModel implements Parcelable
{
    //global variables
    private String title;
    private String thumbnailUrl;
    private String videoUrl;
    private String duration;

    //constructor
    public UpNextSongsModel(String title, String thumbnailUrl, String videoUrl, String duration)
    {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.duration = duration;
    }

    //constructor for parsing arraylist
    protected UpNextSongsModel(Parcel in) {
        title = in.readString();
        thumbnailUrl = in.readString();
        videoUrl = in.readString();
        duration = in.readString();
    }

    public static final Creator<UpNextSongsModel> CREATOR = new Creator<UpNextSongsModel>() {
        @Override
        public UpNextSongsModel createFromParcel(Parcel in) {
            return new UpNextSongsModel(in);
        }

        @Override
        public UpNextSongsModel[] newArray(int size) {
            return new UpNextSongsModel[size];
        }
    };


    //getters of song details
    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getDuration() {
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(thumbnailUrl);
        parcel.writeString(videoUrl);
        parcel.writeString(duration);
    }
}
