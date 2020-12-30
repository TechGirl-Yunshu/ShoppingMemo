package com.example.mymemo.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymemo.Data.DatabaseHandler;
import com.example.mymemo.Model.Grocery;
import com.example.mymemo.R;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Grocery> groceryItems;
    private AlertDialog.Builder deleteAlertBuilder;
    private AlertDialog deleteAlert;
    private LayoutInflater layoutInflater;
    private AlertDialog.Builder editAlertBuilder;
    private AlertDialog editAlert;

    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems){
        this.context = context;
        this.groceryItems = groceryItems;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        Grocery grocery = groceryItems.get(position);
        holder.groceryItemName.setText(grocery.getName());
        holder.quantity.setText(grocery.getQuantity());
        holder.dateAdded.setText(grocery.getDataItemAdded());

    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView groceryItemName;
        public TextView quantity;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;
        public int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;
            groceryItemName = (TextView) itemView.findViewById(R.id.nameRollerView);
            quantity = (TextView) itemView.findViewById(R.id.quantityRollerView);
            dateAdded = (TextView) itemView.findViewById(R.id.dateAdded);
            editButton = (Button) itemView.findViewById(R.id.editButton);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.editButton:
                    int position = getAdapterPosition();
                    Grocery grocery = groceryItems.get(position);
                    editItem(grocery);

                    break;

                case R.id.deleteButton:
                    position = getAdapterPosition();
                    grocery = groceryItems.get(position);
                    deleteItem(grocery.getId());

                    break;

            }
        }

        public void deleteItem(int id) {
            deleteAlertBuilder = new AlertDialog.Builder(context);
            layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.delete_confirmation, null);

            Button noButton = (Button) view.findViewById(R.id.noButton);
            Button yesButton = (Button) view.findViewById(R.id.yesButton);

            deleteAlertBuilder.setView(view);
            deleteAlert = deleteAlertBuilder.create();
            deleteAlert.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAlert.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);

                    groceryItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    deleteAlert.dismiss();

                }
            });
        }

        public void editItem(Grocery grocery){
            editAlertBuilder = new AlertDialog.Builder(context);
            layoutInflater = LayoutInflater.from(context);
            final View view = layoutInflater.inflate(R.layout.popup, null);
            final EditText itemName = (EditText) view.findViewById(R.id.groceryItem);
            final EditText itemQty = (EditText) view.findViewById(R.id.groceryQty);
            final TextView title = (TextView)view.findViewById(R.id.title);
            Button saveButton = (Button) view.findViewById(R.id.saveButton);

            title.setText("Edit Grocery");
            itemName.setText(grocery.getName());
            itemQty.setText(grocery.getQuantity());

            editAlertBuilder.setView(view);
            editAlert = editAlertBuilder.create();
            editAlert.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler db = new DatabaseHandler(context);

                    grocery.setName(itemName.getText().toString());
                    grocery.setQuantity(itemQty.getText().toString());

                    if (!itemName.getText().toString().isEmpty() && !itemQty.getText().toString().isEmpty()){
                        db.updateItem(grocery);
                        notifyItemChanged(getAdapterPosition(),grocery);
                    } else {
                        Snackbar.make(view, "Add Grocery & Quantity", Snackbar.LENGTH_LONG).show();
                    }
                    editAlert.dismiss();
                }
            });
        }
    }
}
