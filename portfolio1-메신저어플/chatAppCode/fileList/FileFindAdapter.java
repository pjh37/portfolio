package com.example.myfriends.fileList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myfriends.R;
import com.example.myfriends.chat.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class FileFindAdapter extends RecyclerView.Adapter<FileFindListHolder> {
    private ArrayList<FileFindVO> list=new ArrayList<>();

    @NonNull
    @Override
    public FileFindListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.file_find_item_list,parent,false);
        return new FileFindListHolder(view);
    }
    void addItem(FileFindVO vo){
        list.add(vo);
    }
    @Override
    public void onBindViewHolder(@NonNull FileFindListHolder holder, int position) {
        final String fileName=list.get(position).file_name;
        String fileSize=list.get(position).file_size;
        final String filePath=list.get(position).file_path;
        if(fileSize.equals("0"))return;
        if(fileName.equals("0"))return;

        if(fileName.contains("docx")||fileName.contains("doc")){
            holder.type_image.setImageResource(R.drawable.docx);
        }else if(fileName.contains("pptx")||fileName.contains("ppt")||fileName.contains("pdf")){
            holder.type_image.setImageResource(R.drawable.pptx);
        }else if(fileName.contains("xlsx")){
            holder.type_image.setImageResource(R.drawable.xlsx);
        }else if(fileName.contains("hwp")){
            holder.type_image.setImageResource(R.drawable.hwp);
        }else{

            //holder.type_image.setImageResource(R.drawable.ic_type_file);
            return;
        }
        Log.v("파일타입","fileName : "+fileName+"  fileSize : "+fileSize+"  filePath : "+filePath);
        if(Float.valueOf(fileSize)<(1024*1024)){
            fileSize=Float.valueOf(fileSize)/1024+"";
            fileSize=fileSize.substring(0,4)+"KB";
        }else if(Float.valueOf(fileSize)<1024*1024*1024){
            fileSize=Float.valueOf(fileSize)/(1024*1024)+"";
            fileSize=fileSize.substring(0,4)+"MB";
        }
        holder.fileName.setText(fileName);
        holder.fileSize.setText(fileSize);
        holder.filePath.setText(filePath);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(),fileName,Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("fileName",fileName);
                intent.putExtra("filePath",filePath);
                 Activity activity=(Activity)v.getContext();
                 activity.setResult(Activity.RESULT_OK,intent.putExtra("fileName",fileName));
                activity.setResult(Activity.RESULT_OK,intent.putExtra("filePath",filePath));
                 activity.setResult(Activity.RESULT_OK,intent);
                 activity.finish();

            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public void notifyDataSetChanged(ArrayList<FileFindVO> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    public ArrayList<FileFindVO> getList(){
        return this.list;
    }

}
