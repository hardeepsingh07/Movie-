package com.example.hardeepsingh.recommender;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MovieDetail extends AppCompatActivity {
    
    Movies movie;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    GridViewAdapter adapter;
    ArrayList<Movies> moviesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        moviesList.add(new Movies("Avengers", "Action\nAdventure", "Apr 12 2016", "PG-13", "$300 Million", "8.5", R.drawable.avenger));
        moviesList.add(new Movies("Shrek", "Adventure\nComedy", "Dec 07 2009", "E", "$92 Million", "7.5", R.drawable.shrek));
        moviesList.add(new Movies("Star Trek", "Adventure\nSci-Fi", "Mar 07 2015", "R", "$100 Million", "7", R.drawable.startrek));
        moviesList.add(new Movies("Star Wars", "Adventure\nSci-Fi", "Jun 19 2004", "PG", "$68 Million", "6.5", R.drawable.starwars));
        moviesList.add(new Movies("Dark Knight", "Action\nAdventure", "Jul 04 2014", "PG-13", "$600 Million", "9.5", R.drawable.knight));


        //Initialize Layouts
        recyclerView = (RecyclerView) findViewById(R.id.grid_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GridViewAdapter(this, moviesList);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MovieDetail.this, "Under Construction!!", Toast.LENGTH_SHORT).show();
            }
        });


        //Get Intent
        Intent i  = getIntent();
        movie = (Movies) i.getSerializableExtra("movie");

        
    }
}