package com.example.mytest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class frag_my_tests extends Fragment {

    DBTests dbHelper;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBTests(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_my_tests, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list2);
        if (dbHelper.getTestID("0") == -1) {
            dbHelper.add_test_info("0", null, null, null, 0, null, true);
        }
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        TestAdapter testAdapter = new TestAdapter(getContext(), dbHelper.getAllTests(true), true);
        recyclerView.setAdapter(testAdapter);
        return view;
    }
}


