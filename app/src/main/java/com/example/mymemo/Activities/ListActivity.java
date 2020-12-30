package com.example.mymemo.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.mymemo.Data.DatabaseHandler;
import com.example.mymemo.Model.Grocery;
import com.example.mymemo.UI.RecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mymemo.R;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Grocery> groceryList;
    private List<Grocery> listItems;
    private DatabaseHandler db;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText groceryItem;
    private EditText quantity;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });

        db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groceryList = new ArrayList<>();
        listItems = new ArrayList<>();

        //get items from database
        groceryList = db.getAllItems();

        for (Grocery grocery : groceryList){
            Grocery individualGrocery = new Grocery();
            individualGrocery.setName(grocery.getName());
            individualGrocery.setQuantity("Qty: " + grocery.getQuantity());
            individualGrocery.setId(grocery.getId());
            individualGrocery.setDataItemAdded("Added on: " + grocery.getDataItemAdded());

            listItems.add(individualGrocery);
        }

        recyclerViewAdapter = new RecyclerViewAdapter(this, listItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    private void createPopupDialog(){

        dialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        groceryItem = (EditText) view.findViewById(R.id.groceryItem);
        quantity = (EditText) view.findViewById(R.id.groceryQty);
        saveButton = (Button) view.findViewById(R.id.saveButton);

        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (!groceryItem.getText().toString().isEmpty() && !quantity.getText().toString().isEmpty()){
                    saveGroceryToDB(v);
                }
            }
        });
    }

    private void saveGroceryToDB(View v){

        Grocery grocery = new Grocery();
        String newGrocery = groceryItem.getText().toString();
        String newQty = quantity.getText().toString();

        grocery.setName(newGrocery);
        grocery.setQuantity(newQty);

        //add to database
        db.addItem(grocery);
        Snackbar.make(v, "SAVED!", Snackbar.LENGTH_LONG).show();


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                dialog.dismiss();
                startActivity(new Intent(ListActivity.this, ListActivity.class));
                finish();
            }
        }, 1000);

    }
}