package com.example.mytest;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter {
    LayoutInflater inflater;
    ArrayList<Test> tests;
    Boolean MyTest;

    public TestAdapter(Context context, ArrayList<Test> tests, Boolean is_MyTest) {
        this.tests = tests;
        this.inflater = LayoutInflater.from(context);
        MyTest = is_MyTest;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view;
        RecyclerView.ViewHolder viewHolder;
        if (viewType == 0 && MyTest){
            view = inflater.inflate(R.layout.item_create_test, parent, false);
            viewHolder = new ViewHolder_2(view);
        } else {
            view = inflater.inflate(R.layout.item, parent, false);
            viewHolder = new ViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == 0 && MyTest) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Add_Test.class);
                    v.getContext().startActivity(intent);
                }
            });
        } else{
            Test test = tests.get(position);
            Picasso.get().load(test.getUri()).fit().centerCrop().into(((ViewHolder)holder).picture);
            ((ViewHolder)holder).name.setText(test.getName());
            ((ViewHolder)holder).description.setText(test.getDescription());
            ((ViewHolder)holder).picture.setTransitionName(test.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TestDetails.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext(), ((ViewHolder)holder).picture, test.getName());
                    intent.putExtra("testItem", test);
                    intent.putExtra("pictureURI", test.getUri().toString());
                    v.getContext().startActivity(intent, options.toBundle());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return tests.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;
        TextView name;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.picture);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
        }
    }

    public class ViewHolder_2 extends RecyclerView.ViewHolder {

        public ViewHolder_2(View itemView) {
            super(itemView);
        }
    }
}


