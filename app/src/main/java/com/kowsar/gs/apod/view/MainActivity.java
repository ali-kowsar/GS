package com.kowsar.gs.apod.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kowsar.gs.apod.R;
import com.kowsar.gs.apod.viewmodel.APODViewModel;

public class MainActivity extends AppCompatActivity {

    APODViewModel apodViewModel;
    TextView date;
    TextView description;
    TextView title;
    ImageView imgAPOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        date= (TextView)findViewById(R.id.today);
        description= (TextView)findViewById(R.id.description);
        title= (TextView)findViewById(R.id.pod_title);
        imgAPOD = (ImageView)findViewById(R.id.apod_img);
        apodViewModel = ViewModelProviders.of(this).get(APODViewModel.class);
        apodViewModel.init();
        apodViewModel.getNewsRepository().observe(this, apodResponse -> {
            date.setText(apodResponse.getDate());
            title.setText(apodResponse.getTitle());
            description.setText(apodResponse.getExplanation());

            Glide.with(this)
                    .load(apodResponse.getUrl())
                    .placeholder(R.drawable.default_apod_image_gs)
                    .error(R.drawable.default_apod_image_gs)
                    .centerCrop()
                    .into(imgAPOD);

        });
    }
}