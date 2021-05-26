package com.example.mytest;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Add_Test extends AppCompatActivity {

    DBTests dbHelper;
    EditText Name;
    EditText Description;
    ImageView Test_Picture;
    Spinner Category;
    Switch Switch_Time;
    EditText Time;
    RadioGroup Rating_system;
    RadioButton Balls;
    RadioButton True_answers;
    Button Create;
    Button Save;
    Uri uri;
    int TestID_for_editing;
    Boolean editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_test);
        setTitle("Основные параметры");
        dbHelper = new DBTests(this);
        Switch_Time = findViewById(R.id.Switch_Timer);
        Time = findViewById(R.id.Test_Time);
        Test_Picture = findViewById(R.id.Test_Picture);
        Category = findViewById(R.id.Test_Category);
        Create = findViewById(R.id.Create_test_info);
        Save = findViewById(R.id.Save_test_info);
        Name = findViewById(R.id.Test_Name);
        Description = findViewById(R.id.Test_Description);
        Rating_system = findViewById(R.id.Rating_system);
        Balls = findViewById(R.id.sys_balls);
        True_answers = findViewById(R.id.sys_true_answers);
        editing = false;

        String[] category = {"Животные", "Игры", "История", "Литература", "Музыка", "Мультфильмы", "Психология", "Сериалы", "Фильмы", "География", "Развлекательный", "Прочие тесты"};
        ArrayAdapter<String> adapter_category = new ArrayAdapter<>(this, R.layout.spinner, R.id.spinner_text, category);
        Category.setAdapter(adapter_category);

        try {
            TestID_for_editing = getIntent().getExtras().getInt("ID");
            Test test = dbHelper.getTest(TestID_for_editing);
            Create.setVisibility(View.INVISIBLE);
            Save.setVisibility(View.VISIBLE);
            findViewById(R.id.go_forward_from_add_test).setVisibility(View.VISIBLE);
            Name.setText(test.getName());
            Description.setText(test.getDescription());
            Rating_system.setVisibility(View.INVISIBLE);
            findViewById(R.id.textView14).setVisibility(View.INVISIBLE);
            if (test.getTime_amount() != 0) {
                Switch_Time.setChecked(true);
                Time.setVisibility(View.VISIBLE);
                findViewById(R.id.textView15).setVisibility(View.VISIBLE);
                Time.setText(String.valueOf(test.getTime_amount()));
            }
            for (int i = 0; i < category.length; i++) {
                if (category[i].equals(test.getCategory())) {
                    Category.setSelection(i);
                    break;
                }
            }
            setTitle("Редактирование");
            editing = true;
            Picasso.get().load(test.getUri()).fit().centerCrop().into(Test_Picture);
            uri = test.getUri();
            findViewById(R.id.plus).setVisibility(View.INVISIBLE);

        } catch (Exception ignored) {
        }

        findViewById(R.id.go_forward_from_add_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_Test.this, Add_Question.class);
                String rating_system;
                if (Rating_system.getCheckedRadioButtonId() == R.id.sys_balls) {
                    rating_system = "Balls";
                } else {
                    rating_system = "True_answers";
                }
                intent.putExtra("Rating_system", rating_system);
                intent.putExtra("Name", Name.getText().toString());
                intent.putExtra("editing", editing);
                startActivity(intent);
            }
        });

        Switch_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Switch_Time.isChecked()) {
                    Time.setVisibility(View.VISIBLE);
                    findViewById(R.id.textView15).setVisibility(View.VISIBLE);

                } else {
                    Time.setVisibility(View.INVISIBLE);
                    findViewById(R.id.textView15).setVisibility(View.INVISIBLE);
                }
            }
        });

        Test_Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GalleryIntent = new Intent(Intent.ACTION_PICK);
                GalleryIntent.setType("image/*");
                startActivityForResult(GalleryIntent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            Picasso.get().load(uri).centerInside().fit().into(Test_Picture);
        }
        findViewById(R.id.plus).setVisibility(View.INVISIBLE);
    }

    public void Save_test_info(View view) {
        int time_amount = 0;
        String name = Name.getText().toString();
        String description = Description.getText().toString();
        String category = Category.getSelectedItem().toString();
        if (Time.isShown() && !Time.getText().toString().equals("")) {
            time_amount = Integer.parseInt(Time.getText().toString());
        }

        if (!name.equals("") && ((Time.isShown() && time_amount != 0) || (!Time.isShown()))) {
            Intent intent = new Intent(Add_Test.this, Add_Question.class);
            intent.putExtra("Name", name);
            String rating_system;
            if (Rating_system.getCheckedRadioButtonId() == R.id.sys_balls) {
                rating_system = "Balls";
            } else {
                rating_system = "True_answers";
            }
            intent.putExtra("Rating_system", rating_system);
            if (editing) {
                dbHelper.update_test_info(name, description, category, uri, time_amount, rating_system, TestID_for_editing);
                Toast.makeText(Add_Test.this, "Сохранено", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.add_test_info(name, description, category, uri, time_amount, rating_system, true);
                startActivity(intent);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Не заполнены поля!", Toast.LENGTH_SHORT).show();
        }
    }

    public static class Add_Question extends AppCompatActivity {

        Uri uri;
        Integer TestID;
        DBTests dbHelper;
        Button Add_Question;
        ImageView Add_Answer;
        ImageView Delete_Answer;
        ImageView Picture;
        EditText Question;
        EditText Answer_1;
        EditText Ball_1;
        EditText Answer_2;
        EditText Ball_2;
        EditText Answer_3;
        EditText Ball_3;
        EditText Answer_4;
        EditText Ball_4;
        EditText Number_true_answer;
        String system_rating;
        Boolean editing = false;
        Test test_for_editing;
        HashMap<Integer, String[]> Questions_for_editing;
        int number_que;


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.add_question);
            setTitle("Добавление вопроса");

            dbHelper = new DBTests(this);
            Add_Question = findViewById(R.id.Add_Question);
            Add_Answer = findViewById(R.id.Add_answer);
            Delete_Answer = findViewById(R.id.Delete_Answer);
            Picture = findViewById(R.id.Picture_Question);
            Question = findViewById(R.id.Question);
            Answer_1 = findViewById(R.id.Answer_1);
            Ball_1 = findViewById(R.id.Ball_1);
            Answer_2 = findViewById(R.id.Answer_2);
            Ball_2 = findViewById(R.id.Ball_2);
            Answer_3 = findViewById(R.id.Answer_3);
            Ball_3 = findViewById(R.id.Ball_3);
            Answer_4 = findViewById(R.id.Answer_4);
            Ball_4 = findViewById(R.id.Ball_4);
            Number_true_answer = findViewById(R.id.Number_true_answer);
            system_rating = getIntent().getExtras().getString("Rating_system");
            TestID = dbHelper.getTestID(getIntent().getExtras().getString("Name"));
            number_que = 1;

            editing = getIntent().getExtras().getBoolean("editing");
            if (editing) {
                setTitle("Редактирование вопросов");
                findViewById(R.id.edit_next_que).setVisibility(View.VISIBLE);
                findViewById(R.id.plus2).setVisibility(View.INVISIBLE);
                findViewById(R.id.go_forward_from_add_que).setVisibility(View.INVISIBLE);
                test_for_editing = dbHelper.getTest(TestID);
                Questions_for_editing = test_for_editing.getQuestions();
                Add_Question.setText("Сохранить");
                next_question_for_editing();
            }

            if (system_rating.equals("Balls")) {
                Add_Answer.setVisibility(View.VISIBLE);
                Delete_Answer.setVisibility(View.VISIBLE);
                findViewById(R.id.textView6).setVisibility(View.INVISIBLE);
                Number_true_answer.setVisibility(View.INVISIBLE);
            } else {
                findViewById(R.id.Add_answer_2).setVisibility(View.VISIBLE);
                findViewById(R.id.Delete_Answer_2).setVisibility(View.VISIBLE);
                findViewById(R.id.textView6).setVisibility(View.VISIBLE);
                Number_true_answer.setVisibility(View.VISIBLE);
                Ball_1.setVisibility(View.INVISIBLE);
                Ball_2.setVisibility(View.INVISIBLE);
                Ball_3.setVisibility(View.INVISIBLE);
                Ball_4.setVisibility(View.INVISIBLE);
            }

            findViewById(R.id.edit_next_que).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    next_question_for_editing();
                }
            });

            Add_Question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String question = Question.getText().toString();
                    String answer_1 = Answer_1.getText().toString();
                    String answer_2 = Answer_2.getText().toString();
                    String answer_3 = Answer_3.getText().toString();
                    String answer_4 = Answer_4.getText().toString();
                    int ball_1 = 0;
                    int ball_2 = 0;
                    int ball_3 = 0;
                    int ball_4 = 0;
                    int numb_true_ans = 0;
                    if (system_rating.equals("Balls")) {
                        if (Question.getText().toString().equals("") || Answer_1.getText().toString().equals("") || Ball_1.getText().toString().equals("") || Answer_2.getText().toString().equals("") || Ball_2.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Заполнены не все поля", Toast.LENGTH_SHORT).show();
                        } else if (Answer_3.isShown() && Answer_3.getText().toString().equals("") || Ball_3.getText().toString().equals("") && Ball_3.isShown()) {
                            Toast.makeText(getApplicationContext(), "Заполнены не все поля", Toast.LENGTH_SHORT).show();
                        } else if (Answer_4.isShown() && Answer_4.getText().toString().equals("") || Ball_4.getText().toString().equals("") && Ball_4.isShown()) {
                            Toast.makeText(getApplicationContext(), "Заполнены не все поля", Toast.LENGTH_SHORT).show();
                        } else {
                            ball_1 = Integer.parseInt(Ball_1.getText().toString());
                            ball_2 = Integer.parseInt(Ball_2.getText().toString());
                            if (Ball_3.isShown()) {
                                ball_3 = Integer.parseInt(Ball_3.getText().toString());
                            }
                            if (Ball_4.isShown()) {
                                ball_4 = Integer.parseInt(Ball_4.getText().toString());
                            }
                            if (editing) {
                                dbHelper.update_questions(question, answer_1, answer_2, answer_3, answer_4, ball_1, ball_2, ball_3, ball_4, numb_true_ans, uri, TestID);
                                Questions_for_editing = dbHelper.getTest(TestID).getQuestions();
                                Toast.makeText(Add_Test.Add_Question.this, "Сохранено", Toast.LENGTH_SHORT).show();
                            } else {
                                dbHelper.add_questions(question, answer_1, answer_2, answer_3, answer_4, ball_1, ball_2, ball_3, ball_4, numb_true_ans, uri, TestID);
                            }
                            if (!editing) {
                                ClearAll();
                            }
                        }
                    } else if (system_rating.equals("True_answers")) {
                        if (Number_true_answer.getText().toString().equals("") || Number_true_answer.getText().toString().equals("0") || question.equals("") || answer_1.equals("") || answer_2.equals("")) {
                            if (!Number_true_answer.getText().toString().equals("") && Number_true_answer.getText().toString().equals("0") && !question.equals("") && !answer_1.equals("") && !answer_2.equals("")) {
                                Toast.makeText(getApplicationContext(), "Правильный ответ не может быть 0", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Заполнены не все поля", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            numb_true_ans = Integer.parseInt(Number_true_answer.getText().toString());
                            if (editing) {
                                dbHelper.update_questions(question, answer_1, answer_2, answer_3, answer_4, ball_1, ball_2, ball_3, ball_4, numb_true_ans, uri, TestID);
                                Questions_for_editing = dbHelper.getTest(TestID).getQuestions();
                                Toast.makeText(Add_Test.Add_Question.this, "Сохранено", Toast.LENGTH_SHORT).show();
                            } else {
                                dbHelper.add_questions(question, answer_1, answer_2, answer_3, answer_4, ball_1, ball_2, ball_3, ball_4, numb_true_ans, uri, TestID);
                            }
                            if (!editing) {
                                ClearAll();
                            }
                        }
                    }
                }
            });

            findViewById(R.id.go_back_from_add_que).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Add_Test.class);
                    intent.putExtra("ID", TestID);
                    startActivity(intent);
                }
            });

            findViewById(R.id.go_forward_from_add_que).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Add_Result.class);
                    intent.putExtra("ID", TestID);
                    intent.putExtra("system_rating", system_rating);
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onBackPressed() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("Выход");
            if (editing){
                builder.setMessage("Вы действительно хотите завершить редактирование?");
            } else {
                builder.setMessage("Вы действительно хотите завершить создание теста?");
            }
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

        public void add_answer(View view) {
            if (!Answer_3.isShown()) {
                Answer_3.setVisibility(View.VISIBLE);
                if (system_rating.equals("Balls")) {
                    Ball_3.setVisibility(View.VISIBLE);
                } else {
                    Ball_3.setVisibility(View.INVISIBLE);
                }
            } else {
                Answer_4.setVisibility(View.VISIBLE);
                if (system_rating.equals("Balls")) {
                    Ball_4.setVisibility(View.VISIBLE);
                } else {
                    Ball_4.setVisibility(View.INVISIBLE);
                }
            }
        }

        public void next_question_for_editing() {
            ClearAll();
            if (number_que > Questions_for_editing.size()) {
                number_que = 1;
                next_question_for_editing();
            } else {
                uri = Uri.parse(Questions_for_editing.get(number_que)[6]);
                Picture.setImageURI(uri);
                Question.setText(Questions_for_editing.get(number_que)[0]);
                Answer_1.setText(Questions_for_editing.get(number_que)[1]);
                Answer_2.setText(Questions_for_editing.get(number_que)[2]);
                if (!Questions_for_editing.get(number_que)[3].equals("")) {
                    Answer_3.setVisibility(View.VISIBLE);
                    Answer_3.setText(Questions_for_editing.get(number_que)[3]);
                } else {
                    Answer_3.setVisibility(View.INVISIBLE);
                }
                if (!Questions_for_editing.get(number_que)[4].equals("")) {
                    Answer_4.setVisibility(View.VISIBLE);
                    Answer_4.setText(Questions_for_editing.get(number_que)[4]);
                } else {
                    Answer_4.setVisibility(View.INVISIBLE);
                }
                Ball_1.setText(String.valueOf(test_for_editing.getBalls().get(number_que)[0]));
                Ball_2.setText(String.valueOf(test_for_editing.getBalls().get(number_que)[1]));
                String ball_3 = String.valueOf(test_for_editing.getBalls().get(number_que)[2]);
                String ball_4 = String.valueOf(test_for_editing.getBalls().get(number_que)[3]);
                if (!ball_3.equals("0")) {
                    Ball_3.setVisibility(View.VISIBLE);
                    Ball_3.setText(ball_3);
                } else {
                    Ball_3.setVisibility(View.INVISIBLE);
                }
                if (!ball_4.equals("0")) {
                    Ball_4.setVisibility(View.VISIBLE);
                    Ball_4.setText(ball_4);
                } else {
                    Ball_4.setVisibility(View.INVISIBLE);
                }
                Number_true_answer.setText(String.valueOf(Questions_for_editing.get(number_que)[5]));
                number_que++;
            }
        }

        public void delete_answer(View view) {
            if (Answer_4.isShown()) {
                Answer_4.setVisibility(View.INVISIBLE);
                Ball_4.setVisibility(View.INVISIBLE);
            } else {
                Answer_3.setVisibility(View.INVISIBLE);
                Ball_3.setVisibility(View.INVISIBLE);
            }
        }

        public void Add_picture(View view) {
            Intent GalleryIntent = new Intent(Intent.ACTION_PICK);
            GalleryIntent.setType("image/*");
            startActivityForResult(GalleryIntent, 0);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                Picasso.get().load(uri).centerInside().fit().into(Picture);
                Picture.setBackground(null);
                findViewById(R.id.plus2).setVisibility(View.INVISIBLE);
            }
        }

        public void ClearAll() {
            Question.setText(null);
            Picture.setImageBitmap(null);
            uri = null;
            Answer_1.setText(null);
            Ball_1.setText(null);
            Answer_2.setText(null);
            Ball_2.setText(null);
            Answer_3.setText(null);
            Answer_3.setVisibility(View.INVISIBLE);
            Ball_3.setText(null);
            Ball_3.setVisibility(View.INVISIBLE);
            Answer_4.setText(null);
            Answer_4.setVisibility(View.INVISIBLE);
            Ball_4.setText(null);
            Ball_4.setVisibility(View.INVISIBLE);
            Number_true_answer.setText(null);
            if (Picture.getDrawable() == null) {
                findViewById(R.id.plus2).setVisibility(View.VISIBLE);
            }
            Picture.setBackgroundResource(R.drawable.back_for_image_view);
        }
    }

    public static class Add_Result extends AppCompatActivity {

        int TestID;
        Button end_add;
        Button add_result;
        EditText min_ball;
        EditText max_ball;
        EditText title;
        EditText description;
        DBTests dbHelper;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.add_result);
            setTitle("Редактирование результатов");
            TestID = getIntent().getExtras().getInt("ID");
            dbHelper = new DBTests(this);
            end_add = findViewById(R.id.end_add);
            add_result = findViewById(R.id.add_result);
            min_ball = findViewById(R.id.min_ball);
            max_ball = findViewById(R.id.max_ball);
            title = findViewById(R.id.title_add_result);
            description = findViewById(R.id.description_add_result);

            if (getIntent().getExtras().getString("system_rating").equals("True_answers")) {
                TextView textView = findViewById(R.id.textView10);
                textView.setText("Количество правильных ответов:");
            }

            add_result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Title = title.getText().toString();
                    String Description = description.getText().toString();
                    String Min = min_ball.getText().toString();
                    String Max = max_ball.getText().toString();
                    dbHelper.add_result(Title, Description, Min, Max, TestID);
                    ClearAll();
                }
            });

            findViewById(R.id.go_back2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Add_Question.class);
                    startActivity(intent);
                }
            });

            end_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Тест создан", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void ClearAll() {
            title.setText(null);
            description.setText(null);
            min_ball.setText(null);
            max_ball.setText(null);
        }
    }
}
