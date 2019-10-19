package com.example.myfriends.fileList;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.myfriends.R;

import java.util.ArrayList;

public class FileFindActivity extends AppCompatActivity {
    SearchView searchView;
    RecyclerView recyclerView;
    ArrayList<FileFindVO> list;
    ArrayList<FileFindVO> copyList;
    FileFindAdapter fileFindAdapter;
    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_find);
        list=new ArrayList<>();
        copyList=new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(this);
        gridLayoutManager=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fileFindAdapter=new FileFindAdapter();
        recyclerView=(RecyclerView)findViewById(R.id.file_list_recyclerView);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new FileFindItemDecoration());
        recyclerView.setAdapter(fileFindAdapter);
        new FileFindTask().execute();
        Log.v("fileadapter","뷰생성");
    }
    private SearchView.OnQueryTextListener queryTextListener=new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            filtering(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                return false;
            }else{
                filtering(newText);
                return true;
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.file_find_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.searchViewMenu);

        searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("검색");
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }
    private void getFileList(){
        Log.v("fileadapter","getlist 진입");
        final String[] projection = new String[] {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATA};
        //MEDIA_TYPE_NONE인 데이터 쿼리
        final String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?";
        final String[] selectionArgs = new String[] {String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_NONE)};//String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_NONE)

        // 쿼리 수행 후, 컬럼명, 값 출력
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, null);
        while (cursor != null && cursor.moveToNext()) {
            int size_index=cursor.getColumnIndex("_size");
            int file_path_index=cursor.getColumnIndex("_data");
            FileFindVO vo=new FileFindVO();
            String fileName=cursor.getString(file_path_index).substring(cursor.getString(file_path_index).lastIndexOf("/")+1);
            if(fileName.equals(""))break;
            if(fileName.contains("docx")||fileName.contains("doc")){
                vo.setFile_name(cursor.getString(1));
            }else if(fileName.contains("pptx")||fileName.contains("ppt")||fileName.contains("pdf")){
                vo.setFile_name(cursor.getString(1));
            }else if(fileName.contains("xlsx")){
                vo.setFile_name(cursor.getString(1));
            }else if(fileName.contains("hwp")) {
                vo.setFile_name(cursor.getString(1));
            }else{
                continue;
            }
            vo.setFile_size(cursor.getString(size_index));
            vo.setFile_path(cursor.getString(file_path_index));
            list.add(vo);
            copyList.add(vo);
            fileFindAdapter.addItem(vo);
            /*
            for(int i=0;i<columnCount;i++){
                FileFindVO vo=new FileFindVO();
                String fileName=cursor.getString(1);
                if(fileName==null)break;
                if(fileName.contains("docx")||fileName.contains("doc")){
                    vo.setFile_name(cursor.getString(1));
                }else if(fileName.contains("pptx")||fileName.contains("ppt")||fileName.contains("pdf")){
                    vo.setFile_name(cursor.getString(1));
                }else if(fileName.contains("xlsx")){
                    vo.setFile_name(cursor.getString(1));
                }else if(fileName.contains("hwp")) {
                    vo.setFile_name(cursor.getString(1));
                }else{
                    continue;
                }
                vo.setFile_size(cursor.getString(3));
                vo.setFile_path(cursor.getString(4));
                list.add(vo);
                copyList.add(vo);
                fileFindAdapter.addItem(vo);
                Log.v("파일정보",cursor.getColumnName(1)+"    "+cursor.getString(1));
            }
            */
        }

        cursor.close();

        Log.v("fileadapter","getlist 완료");
    }
    public void filtering(String text){
        copyList.clear();

        for(FileFindVO item : list){
            if(item.getFile_name().contains(text)){
                copyList.add(item);
            }
        }
        Log.v("searchviewtext","list.size() : "+list.size());
        fileFindAdapter.notifyDataSetChanged(copyList);
    }
    public class FileFindTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            getFileList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fileFindAdapter.notifyDataSetChanged();
                            Log.v("fileadapter","getlist 크기 : "+list.size());
                        }
                    });
                }
            }).start();

        }
    }
}
