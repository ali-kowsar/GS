package com.kowsar.gs.apod.view;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.kowsar.gs.apod.R;
import com.kowsar.gs.apod.model.db.FavouriteDB;
import com.kowsar.gs.apod.model.response.APODResponse;
import com.kowsar.gs.apod.utility.APODSharedPref;
import com.kowsar.gs.apod.viewmodel.APODViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private final String TAG= this.getClass().getSimpleName();

    APODViewModel apodViewModel;
    APODResponse currentItem;
    TextView date;
    TextView description;
    TextView title;
    ImageView imgAPOD;
    TextView headerTitle;
    ImageButton searchByDate;
    ImageButton fab;
    ImageButton menu;
    private Calendar datePickerCalendar ;
    private String prefKey;
    ProgressDialog progressDialog;
    String detailId;
    private FavouriteDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(): ENTER");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle= getIntent().getExtras();
        if (bundle!=null){
            detailId = bundle.getString("fav_id");
            Log.d(TAG, "Fav details id="+detailId);
        }
        date= (TextView)findViewById(R.id.today);
        description= (TextView)findViewById(R.id.description);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            description.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        initPD();
        showDialog(true);
        db= new FavouriteDB(this);


        title= (TextView)findViewById(R.id.pod_title);
        menu= (ImageButton)findViewById(R.id.menu);
        menu.setOnClickListener(this);
        imgAPOD = (ImageView)findViewById(R.id.apod_img);
        searchByDate = (ImageButton) findViewById(R.id.search_date);
        headerTitle=(TextView)findViewById(R.id.header_title);
        fab= (ImageButton)findViewById(R.id.fab_btn_main);
        fab.setOnClickListener(this);
        datePickerCalendar = Calendar.getInstance();
        searchByDate.setOnClickListener(this);

        if (detailId!=null){
            title.setVisibility(View.GONE);
            menu.setVisibility(View.GONE);
            searchByDate.setVisibility(View.GONE);
        }

        //ViewModel
        apodViewModel = ViewModelProviders.of(this).get(APODViewModel.class);
        apodViewModel.init(detailId);
        apodViewModel.getAPODLiveData().observe(this, apodResponse -> {
            Log.d(TAG, "Observer(): title=" + apodResponse.getDate());
            currentItem = apodResponse;
            showDialog(false);
            prefKey = apodResponse.getDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = new Date();
            String dateStr= formatter.format(date1);
            if (detailId!=null){
                headerTitle.setText(apodResponse.getTitle());
            }else {
                headerTitle.setText(R.string.header_title);
            }
            if (APODSharedPref.getInstance(this).getBoolean(prefKey)){
                fab.setBackgroundResource(R.drawable.ic_favourite_selected);
            }else {
                fab.setBackgroundResource(R.drawable.ic_favourite_de_select);
            }

            date.setText(apodResponse.getDate());
            title.setText(apodResponse.getTitle());
            description.setText(apodResponse.getExplanation());
            String url = null;
            if (apodResponse.getMediaType().equalsIgnoreCase("video")) {
                url = apodResponse.getThumbURL();
            } else {
                url = apodResponse.getUrl();
            }

            Glide.with(this)
                    .load(url)
                    .fitCenter()
                    .placeholder(R.drawable.default_apod_image_gs)
                    .error(R.drawable.default_apod_image_gs)
                    .into(imgAPOD);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.apod_menu, menu);
        return true;
    }

    private void addToFabDB(APODResponse currentItem) {
        Log.d(TAG, "addToFabDB(): mediaType="+currentItem.getMediaType());
        Cursor cursor = db.fetchAllFabData();
        SQLiteDatabase favDB = db.getReadableDatabase();
        try {
            while (cursor.moveToNext()){
                String title= cursor.getString(cursor.getColumnIndex(FavouriteDB.TITLE));
                String id= cursor.getString(cursor.getColumnIndex(FavouriteDB.ITEM_ID));
                String url = cursor.getString(cursor.getColumnIndex(FavouriteDB.THUMB_URL));
                Log.d(TAG, "title="+title+",id="+id+", url="+url);
            }
        }finally {
            if(cursor!=null && !cursor.isClosed()){
                cursor.close();
            }
        }
        if (currentItem.getMediaType().equalsIgnoreCase("video")){
            db.insertFABToDB(currentItem.getDate(),currentItem.getTitle(),currentItem.getThumbURL());
        } else {
            db.insertFABToDB(currentItem.getDate(),currentItem.getTitle(),currentItem.getUrl());
        }
    }
    private void removeFromFabDB(APODResponse currentItem) {
        db.removeFromFab(currentItem.getDate());
    }
    private void initPD() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(100,100);
        progressDialog.getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet(): yesr="+year+", month="+month+", day="+dayOfMonth);
        String dateStr=""+year+"-"+(month+1)+"-"+dayOfMonth;
        Log.d(TAG, "onDateSet(): dateStr="+dateStr);
        apodViewModel.getAPODByDate(dateStr);
        showDialog(true);
    }

    public void showDialog(boolean isShow){
        if (isShow){
            progressDialog.show();
            new Handler().postDelayed(timeOutRunnable,20000);
        }else {
            progressDialog.dismiss();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult():Enter");
        if (APODSharedPref.getInstance(this).getBoolean(prefKey)){
            fab.setBackgroundResource(R.drawable.ic_favourite_selected);
        }else {
            fab.setBackgroundResource(R.drawable.ic_favourite_de_select);
        }
    }

    private void shouMenuPoppup(View view){
        Log.d(TAG, "shouMenuPoppup(): Enter");

        PopupMenu menuPopup= new PopupMenu(this, view);
        menuPopup.inflate(R.menu.apod_menu);
        menuPopup.setOnMenuItemClickListener(item->{
            switch (item.getItemId()){
                case R.id.dark_mode_switch:
                    Log.d(TAG, "menu item clicked: dark mode clicked");
                    return true;
                case R.id.go_favourite:
                    Log.d(TAG, "menu item clicked: Favourite clicked");
                    Intent intent = new Intent();
                    intent.setClass(this, FavouriteActivity.class);
                    startActivityForResult(intent,555);
                    return true;
            }

            return false;
        });
        menuPopup.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_btn_main:
                Log.d(TAG, "onClick(): Fav btn clicked");

                if (APODSharedPref.getInstance(this).getBoolean(prefKey)){
                    fab.setBackgroundResource(R.drawable.ic_favourite_de_select);
                    APODSharedPref.getInstance(this).putBoolean(prefKey,false);
//                apodViewModel.removeFromFabDB(currentItem);
                    removeFromFabDB(currentItem);

                } else {
                    fab.setBackgroundResource(R.drawable.ic_favourite_selected);
                    APODSharedPref.getInstance(this).putBoolean(prefKey,true);
//                apodViewModel.addToFabDB(currentItem);
                    addToFabDB(currentItem);
                }
                break;
            case R.id.search_date:
                Log.d(TAG, "onClick(): Calendaer btn clicked");
                DatePickerDialog dialog= new DatePickerDialog(this, this, datePickerCalendar.get(Calendar.YEAR),
                        datePickerCalendar.get(Calendar.MONTH),
                        datePickerCalendar.get(Calendar.DATE));
                dialog.setCancelable(false);
                dialog.show();
                break;
            case R.id.menu:
                Log.d(TAG, "onClick(): Menu btn clicked");
                shouMenuPoppup(v);
                break;
        }
    }

    Runnable timeOutRunnable= ()->{
        showDialog(false);
        Toast.makeText(this,"Data not fetch due to Network error!!! Please try again",Toast.LENGTH_LONG).show();
    };
}