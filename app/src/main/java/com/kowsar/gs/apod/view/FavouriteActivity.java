package com.kowsar.gs.apod.view;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kowsar.gs.apod.R;
import com.kowsar.gs.apod.model.db.FavouriteDB;
import com.kowsar.gs.apod.utility.APODSharedPref;

import java.util.ArrayList;

public class FavouriteActivity extends AppCompatActivity implements APODFabAdapter.ICommunication {
    private  final String TAG = this.getClass().getSimpleName();
    ArrayList<APODItem> fabList= new ArrayList<>();
    private static final int REQUEST_CODE=1000;
    APODFabAdapter adapter;
    private FavouriteDB db;
    private ImageButton calendar;
    private ImageButton menu;
    private ImageButton fav;
    private TextView header;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fab_home);
        db= new FavouriteDB(this);
        calendar=(ImageButton)findViewById(R.id.search_date);
        calendar.setVisibility(View.GONE);
        menu=(ImageButton)findViewById(R.id.menu);
        menu.setVisibility(View.GONE);
        fav=(ImageButton)findViewById(R.id.fab_btn_main);
        fav.setVisibility(View.GONE);
        header=(TextView) findViewById(R.id.header_title);
        header.setText(R.string.title_favourite);
        getFabList();
        RecyclerView rv= (RecyclerView)findViewById(R.id.fav_rv);
        adapter= new APODFabAdapter(fabList, this,this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

    }

    private void getFabList() {
        Log.d(TAG, "getFabList(): Enter");
        fabList.clear();
        Cursor cursor = db.fetchAllFabData();
        SQLiteDatabase favDB = db.getReadableDatabase();
        try {
            while (cursor.moveToNext()){
                String title= cursor.getString(cursor.getColumnIndex(FavouriteDB.TITLE));
                String id= cursor.getString(cursor.getColumnIndex(FavouriteDB.ITEM_ID));
                String url = cursor.getString(cursor.getColumnIndex(FavouriteDB.THUMB_URL));
                fabList.add(new APODItem(id, title, url));
                Log.d(TAG, "title="+title+",id="+id+", url="+url);
            }
        }finally {
            if(cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"onActivityResult(): ENTER");
        getFabList();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void deleteFromFAV(APODItem item) {
        Log.d(TAG, "deleteFromFAV(): Id="+item.getId());
        APODSharedPref.getInstance(this).putBoolean(item.getId(), false);
        db.removeFromFab(item.getId());
    }

    @Override
    public void favdetail(APODItem item) {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        Bundle bundle= new Bundle();
        bundle.putString("fav_id",item.getId());
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE);
    }
}
