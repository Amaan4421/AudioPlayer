package com.example.audio_player.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audio_player.Activity.PlayAudio;
import com.example.audio_player.Fragment.SongDetailsFragment;
import com.example.audio_player.Model.SongHistoryModel;
import com.example.audio_player.Model.YoutubeModel;
import com.example.audio_player.R;
import com.example.audio_player.Utils.AudioExtractor;
import com.example.audio_player.database_helper.HistoryDatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HindiListAdapter extends RecyclerView.Adapter<HindiListAdapter.ViewHolder> {

    private final Context context;
    private final ClickEvent onItemClickListener;
    private final List<YoutubeModel> youtubeModels;

    //constructor
    public HindiListAdapter(Context context, List<YoutubeModel> youtubeModels, ClickEvent onItemClickListener)
    {
        this.context = context;
        this.youtubeModels = youtubeModels;
        this.onItemClickListener = onItemClickListener;
    }


    //set the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_hindi_list, parent, false);
        return new ViewHolder(view);
    }

    //get on click events
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        YoutubeModel youtubeModel = youtubeModels.get(position);
        holder.audioTitle.setText(youtubeModel.getVideoTitle());
        holder.audioDuration.setText(youtubeModel.getDuration());
        Picasso.get().load(youtubeModel.getVideoImageUrl()).into(holder.audioImage);


        //pass the onClick event to main activity for playing that song
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onItemClickListener.onItemClick(youtubeModel);
            }
        });

        //open menu
        holder.menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //show the bottom sheet with options
                SongDetailsFragment bottomSheet = SongDetailsFragment.newInstance();

                //pass song data
                Bundle args = new Bundle();
                args.putString("videoTitle", youtubeModel.getVideoTitle());
                args.putString("duration", youtubeModel.getDuration());
                args.putString("imageUrl", youtubeModel.getVideoImageUrl());
                bottomSheet.setArguments(args);


                //on click events of menu
                bottomSheet.setSongOptionsListener(new SongDetailsFragment.SongOptionsListener()
                {
                    //when user clicks play now button
                    @Override
                    public void onPlayNowClicked()
                    {
                        holder.progressBar.setVisibility(View.VISIBLE);

                        Intent i = new Intent(context, PlayAudio.class);
                        i.putExtra("title", youtubeModel.getVideoTitle());
                        i.putExtra("image", youtubeModel.getVideoImageUrl());

                        AudioExtractor audioExtractor = new AudioExtractor(context);
                        audioExtractor.getAudioFileUrl(youtubeModel.getVideoUrl(), i, holder.progressBar);

                        //add that song into history
                        HistoryDatabaseHelper db = new HistoryDatabaseHelper(v.getContext());
                        SongHistoryModel songHistory = new SongHistoryModel();
                        songHistory.setSongTitle(youtubeModel.getVideoTitle());
                        songHistory.setSongImageUrl(youtubeModel.getVideoImageUrl());
                        songHistory.setSongVideoId(youtubeModel.getVideoId());
                        songHistory.setSongDuration(youtubeModel.getDuration());
                        songHistory.setVideoUrl(youtubeModel.getVideoUrl());
                        db.addSongToHistory(songHistory);
                    }

                    //when user clicks play next button
                    @Override
                    public void onPlayNextClicked() {
                    }

                    //when user clicks playlist button
                    @Override
                    public void onAddToPlaylistClicked() {
                    }

                    //when user clicks favourite button
                    @Override
                    public void onAddToFavouritesClicked() {
                    }
                });

                //show the bottom sheet dialog
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "SongOptionsBottomSheet");
            }
        });
    }//end of method

    @Override
    public int getItemCount() {
        return youtubeModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView audioTitle, audioDuration;
        ImageView audioImage, menuButton;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            audioTitle = itemView.findViewById(R.id.audio_title);
            audioDuration = itemView.findViewById(R.id.audio_duration);
            audioImage = itemView.findViewById(R.id.videoImage);
            menuButton = itemView.findViewById(R.id.optionButton);
            progressBar = itemView.findViewById(R.id.showLoading);
        }//end of method
    }//end of method

    public interface ClickEvent {
        void onItemClick(YoutubeModel youtubeModel);
    }//end of method
}//end of class
