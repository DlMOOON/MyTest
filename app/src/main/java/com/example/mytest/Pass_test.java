package com.example.mytest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Pass_test extends AppCompatActivity {

    private Test test;
    private Integer sum_balls = 0;
    private Integer sum_true_answers = 0;
    private HashMap<Integer, String[]> Questions;
    private Button next_question;
    private ImageView Picture_Question;
    private TextView Text_Question;
    private RadioGroup Answers;
    private RadioButton Answer_1;
    private RadioButton Answer_2;
    private RadioButton Answer_3;
    private RadioButton Answer_4;
    private DBTests dbHelper;
    private Integer TestID;
    private Integer numb_question;
    private CountDownTimer timer;
    private ActionBar actionBar;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_test);
        setTitle(getIntent().getExtras().getString("Name"));
        Questions = new HashMap<>();
        Text_Question = findViewById(R.id.text_question);
        Picture_Question = findViewById(R.id.picture_question);
        next_question = findViewById(R.id.next_question);
        TestID = getIntent().getExtras().getInt("ID");
        dbHelper = new DBTests(this);
        Answers = findViewById(R.id.Answers);
        Answer_1 = findViewById(R.id.Button_answer_1);
        Answer_2 = findViewById(R.id.Button_answer_2);
        Answer_3 = findViewById(R.id.Button_answer_3);
        Answer_4 = findViewById(R.id.Button_answer_4);
        scrollView = findViewById(R.id.scroll_pass);
        actionBar = getSupportActionBar();
        numb_question = 1;
        test = dbHelper.getTest(TestID);
        Questions = test.getQuestions();
        Load_Question();
        setTimer();

        next_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check_answer();
                if (Answers.getCheckedRadioButtonId() != -1) {
                    if (numb_question > Questions.size()) {
                        Intent intent = new Intent(getApplicationContext(), Test_Result.class);
                        intent.putExtra("ID", TestID);
                        intent.putExtra("Rating_system", test.rating_system);
                        if (test.rating_system.equals("True_answers")) {
                            intent.putExtra("sum", sum_true_answers);
                        } else {
                            intent.putExtra("sum", sum_balls);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        if (numb_question == Questions.size()){
                            next_question.setText("Показать результат");
                        }
                        Load_Question();
                    }
                } else {
                    Toast.makeText(Pass_test.this, "Выберите вариант ответа", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Pass_test.this);
        builder.setTitle("Выход");
        builder.setMessage("Вы действительно хотите завершить тест?");
        builder.setCancelable(true);
        builder.setPositiveButton("ДА", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    protected void onStop() {
        super.onStop();
        try {
            timer.cancel();
        } catch (Exception ignored) {
        }
    }

    public void Load_Question() {
        actionBar.setTitle("Вопрос " + numb_question + "/" + Questions.size());
        Answers.clearCheck();
        scrollView.scrollTo(0, 0);
        Uri uri = Uri.parse(Questions.get(numb_question)[6]);
        if (uri.toString().equals("null")) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) Picture_Question.getLayoutParams();
            params.height = 0;
            Picture_Question.setLayoutParams(params);
        } else {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) Picture_Question.getLayoutParams();
            params.height = 500;
            Picture_Question.setLayoutParams(params);
            Picasso.get().load(uri).fit().centerInside().into(Picture_Question);
        }
        Text_Question.setText(Questions.get(numb_question)[0]);
        Answer_1.setText(Questions.get(numb_question)[1]);
        Answer_2.setText(Questions.get(numb_question)[2]);
        if (!Questions.get(numb_question)[3].equals("")) {
            Answer_3.setVisibility(View.VISIBLE);
            Answer_3.setText(Questions.get(numb_question)[3]);
        } else {
            Answer_3.setVisibility(View.INVISIBLE);
        }
        if (!Questions.get(numb_question)[4].equals("")) {
            Answer_4.setVisibility(View.VISIBLE);
            Answer_4.setText(Questions.get(numb_question)[4]);
        } else {
            Answer_4.setVisibility(View.INVISIBLE);
        }
        numb_question++;
    }

    public void Check_answer() {
        if (test.rating_system.equals("True_answers")) {
            int answer = 0;
            if (Answers.getCheckedRadioButtonId() == R.id.Button_answer_1) {
                answer = 1;
            } else if (Answers.getCheckedRadioButtonId() == R.id.Button_answer_2) {
                answer = 2;
            } else if (Answers.getCheckedRadioButtonId() == R.id.Button_answer_3) {
                answer = 3;
            } else if (Answers.getCheckedRadioButtonId() == R.id.Button_answer_4) {
                answer = 4;
            }
            if (test.getQuestions().get(numb_question - 1)[5].equals(Integer.toString(answer))) {
                sum_true_answers++;
            }
        } else {
            if (test.rating_system.equals("Balls")) {
                switch (Answers.getCheckedRadioButtonId()) {
                    case R.id.Button_answer_1:
                        sum_balls += test.getBalls().get(numb_question - 1)[0];
                        break;
                    case R.id.Button_answer_2:
                        sum_balls += test.getBalls().get(numb_question - 1)[1];
                        break;
                    case R.id.Button_answer_3:
                        sum_balls += test.getBalls().get(numb_question - 1)[2];
                        break;
                    case R.id.Button_answer_4:
                        sum_balls += test.getBalls().get(numb_question - 1)[3];
                        break;
                }
            }
        }
    }


    public void setTimer() {
        long millisecond = test.getTime_amount() * 60 * 1000;
        if (millisecond != 0) {
            timer = new CountDownTimer(millisecond, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int sec = (int) (millisUntilFinished / 1000);
                    int min = sec / 60;
                    sec %= 60;
                    actionBar.setSubtitle("Времени осталось - " + String.format("%02d", min) + ":" + String.format("%02d", sec) + " мин.");
                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(getApplicationContext(), Test_Result.class);
                    intent.putExtra("ID", TestID);
                    intent.putExtra("Rating_system", test.rating_system);
                    if (test.rating_system.equals("True_answers")) {
                        intent.putExtra("sum", sum_true_answers);
                    } else {
                        intent.putExtra("sum", sum_balls);
                    }
                    startActivity(intent);
                }
            };
            timer.start();

        }
    }

public static class Test_Result extends AppCompatActivity {

    private Integer sum_balls = 0;
    private Integer sum_true_answers = 0;
    private Test test;
    private DBTests dbHelper;
    private TextView title;
    private TextView description;
    private TextView count_point;
    private TextView rating_sys;
    private ImageView picture;
    private String rating_system;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_result);
        setTitle("Результат");
        dbHelper = new DBTests(getApplicationContext());
        title = findViewById(R.id.title_result);
        description = findViewById(R.id.description_result);
        count_point = findViewById(R.id.textView17);
        rating_sys = findViewById(R.id.textView22);
        test = dbHelper.getTest(getIntent().getExtras().getInt("ID"));
        rating_system = getIntent().getExtras().getString("Rating_system");
        if (rating_system.equals("True_answers")) {
            sum_true_answers = getIntent().getExtras().getInt("sum");
            find_and_view_result(sum_true_answers);
        } else {
            sum_balls = getIntent().getExtras().getInt("sum");
            find_and_view_result(sum_balls);
        }

        findViewById(R.id.go_main_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void find_and_view_result(int sum) {
        for (Map.Entry<Integer, String[]> entry : test.getResults().entrySet()) {
            String Title = entry.getValue()[0];
            String Description = entry.getValue()[1];
            int min = Integer.parseInt(entry.getValue()[2]);
            int max = Integer.parseInt(entry.getValue()[3]);
            if (sum >= min && sum <= max) {
                if (rating_system.equals("True_answers")) {
                    count_point.setText(sum + "/" + test.getQuestions().size());
                    rating_sys.setText("Правильных ответов");
                } else {
                    count_point.setText(sum + "/" + find_max_count_balls());
                    rating_sys.setText("Баллов");
                }
                title.setText(Title);
                description.setText(Description);
                break;
            }
        }
    }

    public int find_max_count_balls() {
        int max_balls = 0;
        for (Map.Entry<Integer, Integer[]> entry : test.getBalls().entrySet()) {
            int max = -1;
            for (int i = 0; i < 4; i++) {
                if (entry.getValue()[i] > max) {
                    max = entry.getValue()[i];
                }
            }
            max_balls += max;
        }
        return max_balls;
    }
}
}
