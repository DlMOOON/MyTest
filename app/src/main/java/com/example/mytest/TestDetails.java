package com.example.mytest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class TestDetails extends AppCompatActivity {

    DBTests dbHelper;
    ImageView image;
    TextView name;
    TextView description;
    TextView Category;
    TextView time;
    Button Start;
    Button Start_2;
    Button Delete;
    Button Edit;
    Test testItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_details);
        setTitle("Описание");
        dbHelper = new DBTests(this);
        image = findViewById(R.id.MenuPicture);
        name = findViewById(R.id.Name);
        description = findViewById(R.id.Description);
        Category = findViewById(R.id.Category);
        time = findViewById(R.id.Time_amount);
        Start = findViewById(R.id.Start_Test);
        Start_2 = findViewById(R.id.Start_Test_2);
        Delete = findViewById(R.id.Delete_test);
        Edit = findViewById(R.id.Editing);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        testItem = getIntent().getExtras().getParcelable("testItem");
        image.setImageURI(Uri.parse(getIntent().getExtras().getString("pictureURI")));

        System.out.println(testItem.isMyTest);
        if (testItem.isMyTest == 1){
            Delete.setVisibility(View.VISIBLE);
            Edit.setVisibility(View.VISIBLE);
            Start_2.setVisibility(View.INVISIBLE);
        } else {
            Delete.setVisibility(View.INVISIBLE);
            Start.setVisibility(View.INVISIBLE);
            Edit.setVisibility(View.INVISIBLE);
            Start_2.setVisibility(View.VISIBLE);
        }

        image.setTransitionName(testItem.getName());
        name.setText(testItem.getName());
        description.setText(testItem.getDescription());
        Category.setText(testItem.getCategory());
        if (testItem.getTime_amount() == 0){
            time.setText("Неограниченно");
        } else {
            time.setText(testItem.getTime_amount() + " мин.");
        }

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TestDetails.this);
                builder.setTitle("Удаление");
                builder.setMessage("Вы действительно хотите удалить тест?");
                builder.setCancelable(true);
                builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.delete_test(dbHelper.getTestID(testItem.getName()));
                        finish();
                        Intent intent = new Intent(TestDetails.this, MainActivity.class);
                        Toast.makeText(getApplicationContext(), "Тест удален", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestDetails.this, Add_Test.class);
                intent.putExtra("ID", dbHelper.getTestID(name.getText().toString()));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return true;
    }

    public void Start_test(View view){
        if (testItem.getName().equals("Тест: Какой ты фрукт?") || testItem.getName().equals("Тест «Идиот или гений?»") || testItem.getName().equals("Тест на оригинальность") || testItem.getName().equals("Тест: Какое вы животное?")){
            Toast.makeText(TestDetails.this, "Данный тест находится в разработке", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(TestDetails.this, Pass_test.class);
            intent.putExtra("ID", dbHelper.getTestID(testItem.getName()));
            intent.putExtra("Name", testItem.getName());
            startActivity(intent);
        }
    }
}
