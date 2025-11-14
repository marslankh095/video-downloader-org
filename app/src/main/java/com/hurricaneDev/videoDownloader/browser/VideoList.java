/*
 * Copyright (c) 2021.  Hurricane Development Studios
 */

package com.hurricaneDev.videoDownloader.browser;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hurricaneDev.videoDownloader.adapter.QualitiesAdapter;
import com.hurricaneDev.videoDownloader.download.AnyObjectReturnCallback;
import com.hurricaneDev.videoDownloader.utils.PermissionRequestCodes;
import com.hurricaneDev.videoDownloader.R;
import com.hurricaneDev.videoDownloader.VDApp;
import com.hurricaneDev.videoDownloader.download.DownloadManager;
import com.hurricaneDev.videoDownloader.download.DownloadPermissionHandler;
import com.hurricaneDev.videoDownloader.download.DownloadVideo;
import com.hurricaneDev.videoDownloader.download.list.DownloadQueues;
import com.hurricaneDev.videoDownloader.utils.RenameDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;


public abstract class VideoList {
    private Activity activity;
    private RecyclerView view;
    private List<Video> videos;
    float percentage;
    public class Video {
        public String quality;
        public String size;
        public String type;
        String link;
        String name;
        String page;
        String website;
        boolean chunked = false;
        boolean checked = false;
        boolean expanded = false;
        boolean audio;
    }

    abstract void onItemDeleted();
    abstract void onVideoPlayed(String url);

    VideoList(Activity activity, RecyclerView view) {
        this.activity = activity;
        this.view = view;

        view.setAdapter(new VideoListAdapter());
        view.setLayoutManager(new LinearLayoutManager(activity));
        view.setHasFixedSize(true);

        videos = Collections.synchronizedList(new ArrayList<Video>());

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }
        else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }
        long available = bytesAvailable / (1024 * 1024);
        long total = stat.getTotalBytes() / (1024 * 1024);
        percentage = available * 100 / (float)total;
    }

    void recreateVideoList(RecyclerView view) {
        this.view = view;
        view.setAdapter(new VideoListAdapter());
        view.setLayoutManager(new LinearLayoutManager(activity));
        view.setHasFixedSize(true);


    }

    void addItem(String quality, String size, String type, String link, String name, String page,
                 boolean chunked, String website, boolean audio) {
        Video video = new Video();
        video.quality = quality;
        video.size = size;
        video.type = type;
        video.link = link;
        video.name = name;
        video.page = page;
        video.chunked = chunked;
        video.website = website;
        video.audio = audio;

        if(!audio){
            boolean duplicate = false;
            for (ListIterator<Video> iterator = videos.listIterator(); iterator.hasNext();) {
                Video v = iterator.next();
                if (v.link.equals(video.link)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                videos.add(video);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        view.getAdapter().notifyDataSetChanged();
                    }
                });
            }

        }
    }

    int getSize() {
        return videos.size();
    }

    void deleteAllItems() {
        for (int i = 0; i < videos.size(); ) {
                videos.remove(i);
        }
        ((VideoListAdapter) view.getAdapter()).expandedItem = -1;
        view.getAdapter().notifyDataSetChanged();
    }

    class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoItem> {
        int expandedItem = -1;
        
        @Override
        public VideoItem onCreateViewHolder( ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            return (new VideoItem(inflater.inflate(R.layout.videos_found_item_, parent,
                    false)));
        }

        @Override
        public void onBindViewHolder( VideoItem holder, int position) {
            holder.bind(videos.get(position));
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        class VideoItem extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView size;
            TextView name;
            Button download;
            ImageView rename;
            ImageView videoThumb;
            TextView play;
            TextView progress;
            RecyclerView qualities;
            ProgressBar progressBar;

            VideoItem(View itemView) {
                super(itemView);
                size = itemView.findViewById(R.id.videoFoundSize);
                name = itemView.findViewById(R.id.videoFoundName);
                play = itemView.findViewById(R.id.videoFoundPlay);
                progress = itemView.findViewById(R.id.storage_percentage);
                progressBar = itemView.findViewById(R.id.progress);
                qualities = itemView.findViewById(R.id.recyclerView);
                videoThumb = itemView.findViewById(R.id.videoImg);
                play.setOnClickListener(this);
                download = itemView.findViewById(R.id.videoFoundDownload);
                download.setOnClickListener(this);
                rename = itemView.findViewById(R.id.videoFoundRename);
                rename.setOnClickListener(this);
            }

            void bind(Video video) {
                if (video.size != null) {
                    String sizeFormatted = Formatter.formatShortFileSize(activity,
                            Long.parseLong(video.size));
                    size.setText("Size    "+sizeFormatted);
                } else size.setText(" ");
                name.setText(video.name);
                if(video.website != null){
                    if(video.website.equals("facebook.com") || video.website.equals("twitter.com") || video.website.equals("instagram.com") ||  video.website.equals("m.vlive.tv")){
                        play.setVisibility(View.VISIBLE);
                    }
                    else{
                        play.setVisibility(View.GONE);
                    }
                }
                Glide.with(activity).load(video.link).into(videoThumb);
                qualities.setAdapter(new QualitiesAdapter(activity, videos, new AnyObjectReturnCallback() {
                    @Override
                    public void onClicked(Object object) {

                    }
                }));
                qualities.setLayoutManager(new LinearLayoutManager(activity));
                qualities.setHasFixedSize(false);

                try{
                    progress.setText(Math.round(percentage));
                    progressBar.setProgress(Math.round(percentage));
                } catch (Exception e){

                }

            }


            @Override
            public void onClick(View v) {
                if (v == itemView.findViewById(R.id.videoFoundRename)) {
                    new RenameDialog(activity, name.getText().toString()) {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //nada
                        }

                        @Override
                        public void onOK(String newName) {
                            videos.get(getAdapterPosition()).name = newName;
                            notifyItemChanged(getAdapterPosition());
                        }
                    };
                } else if (v == download) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        new DownloadPermissionHandler(activity) {
                            @Override
                            public void onPermissionGranted() {
                                startDownload();
                            }
                        }.checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                PermissionRequestCodes.DOWNLOADS);
                    } else {
                        startDownload();
                    }
                }  else if (v == play) {
                    Video video = videos.get(getAdapterPosition());
                    onVideoPlayed(video.link);
                }
            }

            void startDownload() {
                Video video = videos.get(getAdapterPosition());
                DownloadQueues queues = DownloadQueues.load(activity);
                queues.insertToTop(video.size, video.type, video.link, video.name, video
                        .page, video.chunked, video.website);
                queues.save(activity);
                DownloadVideo topVideo = queues.getTopVideo();
                Intent downloadService = VDApp.getInstance().getDownloadService();
                DownloadManager.stop();
                downloadService.putExtra("link", topVideo.link);
                downloadService.putExtra("name", topVideo.name);
                downloadService.putExtra("type", topVideo.type);
                downloadService.putExtra("size", topVideo.size);
                downloadService.putExtra("page", topVideo.page);
                downloadService.putExtra("chunked", topVideo.chunked);
                downloadService.putExtra("website", topVideo.website);
                VDApp.getInstance().startService(downloadService);
                videos.remove(getAdapterPosition());
                expandedItem = -1;
                notifyDataSetChanged();
                onItemDeleted();
                Toast.makeText(activity, "Download is started", Toast.LENGTH_LONG).show();
            }

        }
    }

}
