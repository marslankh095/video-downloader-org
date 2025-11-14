/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.hurricaneDev.videoDownloader.adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hurricaneDev.videoDownloader.R;
import com.hurricaneDev.videoDownloader.browser.VideoList;
import com.hurricaneDev.videoDownloader.download.AnyObjectReturnCallback;
import com.hurricaneDev.videoDownloader.model.GuideModel;

import java.util.List;

public class QualitiesAdapter extends RecyclerView.Adapter<QualitiesAdapter.GuideHolder> {

    private Context context;
    private List<VideoList.Video> list;
    public AnyObjectReturnCallback objectReturnCallback;

    public QualitiesAdapter(Context context, List<VideoList.Video> list, AnyObjectReturnCallback objectReturnCallback) {
        this.context = context;
        this.list = list;
        this.objectReturnCallback = objectReturnCallback;
    }

    @NonNull
    @Override
    public GuideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quality_item_view, parent, false);
        return new GuideHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideHolder holder, final int position) {
        final VideoList.Video video = list.get(position);

        if (video.size != null) {
            String sizeFormatted = Formatter.formatShortFileSize(context,
                    Long.parseLong(video.size));
            holder.videoFoundSize.setText("Size    "+sizeFormatted);
        } else holder.videoFoundSize.setText(" ");
        if (video.quality != null)
        holder.name.setText(video.quality);
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objectReturnCallback.onClicked(video);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GuideHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView videoFoundSize;
        private CheckBox checkbox;

        public GuideHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            videoFoundSize = itemView.findViewById(R.id.videoFoundSize);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }

}
