package com.example.hardeepsingh.recommender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Details extends AppCompatActivity {

    Movie movie;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    GridViewAdapter adapter;
    ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
    URLHandler urlHandler = new URLHandler();
    SharedPreferences prefs;

    TextView detail_name, detail_date, detail_imdb_rating, detail_rt_rating,
            detail_mc_rating, detail_db_rating, detail_genre, detail_description;
    ImageView detail_movie_image;
    CoordinatorLayout coordinatorLayout;
    DatabaseAPI databaseAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get Intent, Preferences and API
        Intent i  = getIntent();
        movie = (Movie) i.getSerializableExtra("movie");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        databaseAPI = new DatabaseAPI();

        //Initialize Layouts
        detail_name = (TextView) findViewById(R.id.detail_name);
        detail_date = (TextView) findViewById(R.id.detail_date);
        detail_imdb_rating = (TextView) findViewById(R.id.detail_imdb_rating);
        detail_rt_rating = (TextView) findViewById(R.id.detail_rt_rating);
        detail_mc_rating = (TextView) findViewById(R.id.detail_mc_rating);
        detail_db_rating = (TextView) findViewById(R.id.detail_db_rating);
        detail_genre = (TextView) findViewById(R.id.detail_genre);
        detail_description = (TextView) findViewById(R.id.detail_description);
        detail_movie_image = (ImageView) findViewById(R.id.detail_movie_image);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cordinator_layout);


        recyclerView = (RecyclerView) findViewById(R.id.grid_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //Set Data
        detail_name.setText(movie.getTitle());
        detail_date.setText(movie.getReleaseDate());
        detail_description.setText(movie.getOverview());

        //Get Ratings
        if(movie.getRatings() == null) {
            databaseAPI.makeRequest(urlHandler.getOmdbRatingUrl(movie.getTitle()), new ResponseInterface() {
                @Override
                public void onDataRecieved(String json) {
                    movie.setRatings(databaseAPI.parseRating(json));
                    handleRatingFormat();
                }
            });
        } else {
            handleRatingFormat();
        }

        //Getting Genre Single Line Separated
        if(movie.getGenreHash() == null) {
            databaseAPI.makeRequest(urlHandler.getOmdbRatingUrl(movie.getTitle()), new ResponseInterface() {
                @Override
                public void onDataRecieved(String json) {
                    movie.setGenreHash(databaseAPI.parseGenre(prefs.getString("genre", null)));
                    handleGenreFormat();
                }
            });
        } else {
            handleGenreFormat();
        }

        //Set Image
        imageLoader.get(urlHandler.getImageUrl(movie.getBackdropPath(), "w300"), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                //Skip the Failure
                if (isImmediate && response.getBitmap() == null) return;

                Bitmap myBitmap = response.getBitmap();
                detail_movie_image.setImageBitmap(myBitmap);
                Palette.from(myBitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int defaultColor = 0xffffff;

                        int color = palette.getDarkVibrantColor(defaultColor);

                        //Change Status Bar Color
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(color);

                        //Change Background Color of ScrollView
                        //coordinatorLayout.setBackgroundColor(color);
                    }
                });
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Detail Image Error: ", error.getMessage());
            }
        });


        //Get Recommendation
        final DatabaseAPI databaseAPI = new DatabaseAPI();
        URLHandler urlHandler = new URLHandler();
        databaseAPI.makeRequest(urlHandler.getRecommendationUrl(movie.getId().toString()), new ResponseInterface() {
            @Override
            public void onDataRecieved(String json) {
                ArrayList<Movie> recommendation = databaseAPI.parseMovies(json);
                adapter = new GridViewAdapter(Details.this, recommendation);
                recyclerView.setAdapter(adapter);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
    }

    public void handleGenreFormat() {
        StringBuffer buffer = new StringBuffer();
        Iterator<Map.Entry<Integer, String>> entryIterator = movie.getGenreHash().entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Integer, String> entry = entryIterator.next();
            buffer.append(entry.getValue());
            if (entryIterator.hasNext()) {
                buffer.append("\n");
            }
        }
        detail_genre.setText(buffer.toString());
    }

    public void handleRatingFormat() {
        for (Map.Entry<String, Double> r : movie.getRatings().entrySet()) {
            switch (r.getKey()) {
                case "API Movie Database":
                    detail_db_rating.setText(Double.toString(r.getValue()));
                    break;
                case "Internet Movie Database":
                    detail_imdb_rating.setText(Double.toString(r.getValue()));
                    break;
                case "Rotten Tomatoes":
                    detail_rt_rating.setText(Double.toString(r.getValue()));
                    break;
                case "Metacritic":
                    detail_mc_rating.setText(Double.toString(r.getValue()));
                    break;
            }
        }
    }
}