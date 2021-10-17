package com.kowsar.gs.apod.view;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.text.LineBreaker;
import android.os.AsyncTask;
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

import com.kowsar.gs.apod.R;
import com.kowsar.gs.apod.model.db.FavouriteDB;
import com.kowsar.gs.apod.model.db.LastUpdatedAPOD;
import com.kowsar.gs.apod.model.response.APODResponse;
import com.kowsar.gs.apod.utility.APODSharedPref;
import com.kowsar.gs.apod.utility.Constant;
import com.kowsar.gs.apod.utility.Utils;
import com.kowsar.gs.apod.viewmodel.APODViewModel;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

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
    private Calendar datePickerCalendar;
    private String prefKey;
    ProgressDialog progressDialog;
    String detailId;
    private FavouriteDB db;
    private LastUpdatedAPOD lastUpdatedAPOD;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate(): ENTER");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailId = bundle.getString("fav_id");
            Log.d(TAG, "Fav details id=" + detailId);
        }
        date = (TextView) findViewById(R.id.today);
        description = (TextView) findViewById(R.id.description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            description.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        initPD();
        showDialog("Wait.... Fetching data from server.", true);
        db = new FavouriteDB(this);
        lastUpdatedAPOD = new LastUpdatedAPOD(this);

        title = (TextView) findViewById(R.id.pod_title);
        menu = (ImageButton) findViewById(R.id.menu);
        menu.setOnClickListener(this);
        imgAPOD = (ImageView) findViewById(R.id.apod_img);
        searchByDate = (ImageButton) findViewById(R.id.search_date);
        headerTitle = (TextView) findViewById(R.id.header_title);
        fab = (ImageButton) findViewById(R.id.fab_btn_main);
        fab.setOnClickListener(this);
        datePickerCalendar = Calendar.getInstance();
        searchByDate.setOnClickListener(this);
        // detailId is not null means it called for details page.
        if (detailId != null) {
            title.setVisibility(View.GONE);
            menu.setVisibility(View.GONE);
            searchByDate.setVisibility(View.GONE);
        }

        apodViewModel = ViewModelProviders.of(this).get(APODViewModel.class);

        //ViewModel
        apodViewModel.init();
        apodViewModel.getAPODLiveData().observe(this, apodResponse -> {
            Log.d(TAG, "Observer(): id=" + apodResponse.getDate());
            currentItem = apodResponse;
            showDialog(null, false);
            prefKey = apodResponse.getDate();

            if (detailId != null) {
                headerTitle.setText(apodResponse.getTitle());
            } else {
                headerTitle.setText(R.string.header_title);
            }
            if (APODSharedPref.getInstance(this).getBoolean(prefKey)) {
                fab.setBackgroundResource(R.drawable.ic_favourite_selected);
            } else {
                fab.setBackgroundResource(R.drawable.ic_favourite_de_select);
            }

            date.setText(apodResponse.getDate());
            title.setText(apodResponse.getTitle());
            description.setText(apodResponse.getExplanation());
            String url = null;
            if (apodResponse.getMediaType().equalsIgnoreCase(Constant.APODA_MEDIA_TYPE_VIDEO)) {
                url = apodResponse.getThumbURL();
            } else {
                url = apodResponse.getUrl();
            }

            new DownloadTask().execute(stringToURL(url));

//            Glide.with(this)
//                    .load(url)
//                    .fitCenter()
//                    .placeholder(R.drawable.default_apod_image_gs)
//                    .error(R.drawable.default_apod_image_gs)
//                    .into(imgAPOD);
        }); // end of ViewModel
        if (Utils.isNetworkConnected(this)) {
            apodViewModel.getAPODByDate(detailId);
        } else {
            Log.d(TAG, "No Network connection. Load last data");
            showDialog(null, false);
            fetchLastDataFromDB();
        }

    }

    private void fetchLastDataFromDB() {
        Log.d(TAG, "fetchLastDataFromDB(): Enter");
        Cursor cursor = lastUpdatedAPOD.fetchLastData();
        SQLiteDatabase lastDB = lastUpdatedAPOD.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String title1 = cursor.getString(cursor.getColumnIndex(LastUpdatedAPOD.KEY_TITLE));
                title.setText(title1);
                String date1 = cursor.getString(cursor.getColumnIndex(LastUpdatedAPOD.KEY_DATE));
                date.setText(date1);
                String desc = cursor.getString(cursor.getColumnIndex(LastUpdatedAPOD.KEY_DESCRIPTION));
                description.setText(desc);
                byte[] imgaByte = cursor.getBlob(cursor.getColumnIndex(LastUpdatedAPOD.KEY_IMAGE));
                Bitmap bitmap = getImage(imgaByte);
                imgAPOD.setImageBitmap(bitmap);


                Log.d(TAG, "title=" + title + ",id=" + date);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.apod_menu, menu);
        return true;
    }

    private void addToFabDB(APODResponse currentItem) {
        Log.d(TAG, "addToFabDB(): mediaType=" + currentItem.getMediaType());
        Cursor cursor = db.fetchAllFabData();
        SQLiteDatabase favDB = db.getReadableDatabase();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(FavouriteDB.TITLE));
                String id = cursor.getString(cursor.getColumnIndex(FavouriteDB.ITEM_ID));
                String url = cursor.getString(cursor.getColumnIndex(FavouriteDB.THUMB_URL));
                Log.d(TAG, "title=" + title + ",id=" + id + ", url=" + url);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        if (currentItem.getMediaType().equalsIgnoreCase(Constant.APODA_MEDIA_TYPE_VIDEO)) {
            db.insertFABToDB(currentItem.getDate(), currentItem.getTitle(), currentItem.getThumbURL());
        } else {
            db.insertFABToDB(currentItem.getDate(), currentItem.getTitle(), currentItem.getUrl());
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
        progressDialog.getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet(): yesr=" + year + ", month=" + month + ", day=" + dayOfMonth);
        String dateStr = "" + year + "-" + (month + 1) + "-" + dayOfMonth;
        Log.d(TAG, "onDateSet(): dateStr=" + dateStr);
        apodViewModel.getAPODByDate(dateStr);
        showDialog("Wait....Fetching data from server", true);
    }

    public void showDialog(String msg, boolean isShow) {
        if (isShow) {
            progressDialog.setMessage(msg);
            progressDialog.show();
            mHandler.postDelayed(timeOutRunnable, 20000);
        } else {
            mHandler.removeCallbacks(timeOutRunnable);
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult():Enter");
        if (APODSharedPref.getInstance(this).getBoolean(prefKey)) {
            fab.setBackgroundResource(R.drawable.ic_favourite_selected);
        } else {
            fab.setBackgroundResource(R.drawable.ic_favourite_de_select);
        }
    }

    private void shouMenuPoppup(View view) {
        Log.d(TAG, "shouMenuPoppup(): Enter");

        PopupMenu menuPopup = new PopupMenu(this, view);
        menuPopup.inflate(R.menu.apod_menu);
        menuPopup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.dark_mode_switch:
                    Log.d(TAG, "menu item clicked: dark mode clicked");
                    return true;
                case R.id.go_favourite:
                    Log.d(TAG, "menu item clicked: Favourite clicked");
                    Intent intent = new Intent();
                    intent.setClass(this, FavouriteActivity.class);
                    startActivityForResult(intent, 555);
                    return true;
            }

            return false;
        });
        menuPopup.show();
    }

    protected URL stringToURL(String strUrl) {
        try {
            URL url = new URL(strUrl);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_btn_main:
                Log.d(TAG, "onClick(): Fav btn clicked");
                if (!Utils.isNetworkConnected(this)){
                    Toast.makeText(this, "No Network. Please check and Try", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (APODSharedPref.getInstance(this).getBoolean(prefKey)) {
                    fab.setBackgroundResource(R.drawable.ic_favourite_de_select);
                    APODSharedPref.getInstance(this).putBoolean(prefKey, false);
//                apodViewModel.removeFromFabDB(currentItem);
                    removeFromFabDB(currentItem);

                } else {
                    fab.setBackgroundResource(R.drawable.ic_favourite_selected);
                    APODSharedPref.getInstance(this).putBoolean(prefKey, true);
//                apodViewModel.addToFabDB(currentItem);
                    addToFabDB(currentItem);
                }
                break;
            case R.id.search_date:
                Log.d(TAG, "onClick(): Calendaer btn clicked");
                if (Utils.isNetworkConnected(this)) {
                    DatePickerDialog dialog = new DatePickerDialog(this, this, datePickerCalendar.get(Calendar.YEAR),
                            datePickerCalendar.get(Calendar.MONTH),
                            datePickerCalendar.get(Calendar.DATE));
                    dialog.setCancelable(false);
                    dialog.show();
                }else {
                    Toast.makeText(this, "No Network. Please check and Try", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu:
                Log.d(TAG, "onClick(): Menu btn clicked");
                shouMenuPoppup(v);
                break;
        }
    }

    Runnable timeOutRunnable = () -> {
        showDialog(null, false);
        Toast.makeText(this, "Data not fetch due to Network error!!! Please try again", Toast.LENGTH_LONG).show();
    };

    private class DownloadTask extends AsyncTask<URL, Void, Bitmap> {
        protected void onPreExecute() {
            showDialog("Wait....Downloading image.", true);
        }

        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                return BitmapFactory.decodeStream(bufferedInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // When all async task done
        protected void onPostExecute(Bitmap result) {
            // Hide the progress dialog
            showDialog(null, false);
            if (result != null) {
                imgAPOD.setImageBitmap(result);
                insertToDb(result);
            } else {
                // Notify user that an error occurred while downloading image
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // convert from bitmap to byte array
    public byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private void insertToDb(Bitmap bitmap) {
        byte[] imageData = getBytes(bitmap);
        lastUpdatedAPOD.insertItem(currentItem.getTitle(), currentItem.getDate(), currentItem.getExplanation(), imageData);
    }
}