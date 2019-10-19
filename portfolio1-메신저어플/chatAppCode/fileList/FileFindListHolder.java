package com.example.myfriends.fileList;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfriends.R;

public class FileFindListHolder extends RecyclerView.ViewHolder {
    public ImageView type_image;
    public TextView fileName;
    public TextView fileSize;
    public TextView filePath;
    public FileFindListHolder(View itemView) {
        super(itemView);
        type_image=(ImageView)itemView.findViewById(R.id.type_image);
        fileName=(TextView)itemView.findViewById(R.id.file_name);
        fileSize=(TextView)itemView.findViewById(R.id.file_size);
        filePath=(TextView)itemView.findViewById(R.id.file_path);
    }
}
