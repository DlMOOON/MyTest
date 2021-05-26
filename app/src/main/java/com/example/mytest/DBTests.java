package com.example.mytest;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public class DBTests {

    private static final int DATABASE_VERSION = 59;
    private static final String DATABASE_NAME = "TestsDB";

    private static final String TABLE_TEST_INFO = "Test_Info";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_DESCRIPTION = "Description";
    private static final String COLUMN_CATEGORY = "Category";
    private static final String COLUMN_PICTURE_URI = "Picture";
    private static final String COLUMN_TIME_AMOUNT = "Time_amount";
    private static final String COLUMN_RATING_SYSTEM = "Rating_system";
    private static final String COLUMN_IS_MY_TEST = "is_MyTest";

    private static final String TABLE_QUESTIONS = "Question_Answers";
    private static final String COLUMN_QUESTION = "Question";
    private static final String COLUMN_ANSWER_1 = "Answer_1";
    private static final String COLUMN_ANSWER_2 = "Answer_2";
    private static final String COLUMN_ANSWER_3 = "Answer_3";
    private static final String COLUMN_ANSWER_4 = "Answer_4";
    private static final String COLUMN_BALL_1 = "Ball_1";
    private static final String COLUMN_BALL_2 = "Ball_2";
    private static final String COLUMN_BALL_3 = "Ball_3";
    private static final String COLUMN_BALL_4 = "Ball_4";
    private static final String COLUMN_TRUE_ANSWER = "True_answer";
    private static final String COLUMN_PICTURE_QUE = "Picture_Question";
    private static final String COLUMN_TEST_ID = "TestID";

    private static final String TABLE_RESULTS = "Results";
    private static final String COLUMN_TITLE = "Title";
    private static final String COLUMN_MIN = "Min_ball";
    private static final String COLUMN_MAX = "Max_ball";

    private SQLiteDatabase DataBase;

    public DBTests(Context context) {
        OpenHelper dbHelper = new OpenHelper(context);
        DataBase = dbHelper.getWritableDatabase();
    }

    public ArrayList<Test> getAllTests(Boolean is_MyTests) {
        int MyTest = (is_MyTests)? 1:0;
        Cursor cursor = DataBase.rawQuery("SELECT Name, Description, Category, Picture, Time_amount, is_MyTest FROM Test_Info WHERE is_MyTest = '"+ MyTest +"'", null);
        ArrayList<Test> arrayList = new ArrayList<>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
                Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PICTURE_URI)));
                int time_amount = cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_AMOUNT));
                Test test = new Test(name, description, category, uri, time_amount, null);
                test.isMyTest = MyTest;
                arrayList.add(test);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public Test getTest(int TestID){
        Cursor cursor = DataBase.rawQuery("SELECT Name, Description, Category, Picture, Time_amount, Rating_system FROM Test_Info WHERE _id = '"+ TestID +"' ", null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
        String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));
        String rating_system = cursor.getString(cursor.getColumnIndex(COLUMN_RATING_SYSTEM));
        Uri uri = Uri.parse(cursor.getString(cursor.getColumnIndex(COLUMN_PICTURE_URI)));
        int time_amount = cursor.getInt(cursor.getColumnIndex(COLUMN_TIME_AMOUNT));

        cursor = DataBase.rawQuery("SELECT DISTINCT Question, Answer_1, Answer_2, Answer_3, Answer_4, Ball_1, Ball_2, Ball_3, Ball_4, True_answer, Picture_Question FROM Question_Answers QA, Test_Info WHERE QA.TestID = '"+ TestID +"'", null);
        cursor.moveToFirst();
        HashMap<Integer, String[]> Question = new HashMap<>();
        HashMap<Integer, Integer[]> Balls = new HashMap<>();
        int numb_question = 1;
        String true_answer = "0";
        if (!cursor.isAfterLast()){
            do {
                String question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION));
                String answer_1 = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER_1));
                String answer_2 = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER_2));
                String answer_3 = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER_3));
                String answer_4 = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER_4));
                int ball_1 = cursor.getInt(cursor.getColumnIndex(COLUMN_BALL_1));
                int ball_2 = cursor.getInt(cursor.getColumnIndex(COLUMN_BALL_2));
                int ball_3 = cursor.getInt(cursor.getColumnIndex(COLUMN_BALL_3));
                int ball_4 = cursor.getInt(cursor.getColumnIndex(COLUMN_BALL_4));
                true_answer = String.valueOf(cursor.getInt(cursor.getColumnIndex(COLUMN_TRUE_ANSWER)));
                String question_uri = cursor.getString(cursor.getColumnIndex(COLUMN_PICTURE_QUE));
                Question.put(numb_question, new String[]{question, answer_1, answer_2, answer_3, answer_4, true_answer, question_uri});
                Balls.put(numb_question, new Integer[]{ball_1, ball_2, ball_3, ball_4});
                numb_question++;
            } while (cursor.moveToNext());
        }

        cursor = DataBase.rawQuery("SELECT DISTINCT Title, Description, Min_ball, Max_ball FROM Results WHERE TestID = '"+ TestID +"'", null);
        cursor.moveToFirst();
        HashMap<Integer, String[]> Results = new HashMap<>();
        int numb_result = -1;
        if (!cursor.isAfterLast()){
            do {
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String result_description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String min_ball = cursor.getString(cursor.getColumnIndex(COLUMN_MIN));
                String max_ball = cursor.getString(cursor.getColumnIndex(COLUMN_MAX));
                numb_result++;
                Results.put(numb_result, new String[]{title, result_description, min_ball, max_ball});
            } while (cursor.moveToNext());
        }
        cursor.close();

        Test test = new Test(name, description, category, uri, time_amount, rating_system);
        test.setQuestions(Question);
        test.setResults(Results);
        test.setBalls(Balls);
        return test;
    }

    public int getTestID(String name){
        int id;
        try {
            Cursor cursor = DataBase.rawQuery("SELECT _id FROM Test_Info WHERE Name LIKE '"+name+"'", null);
            cursor.moveToNext();
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        } catch (Exception exception){
            id = -1;
        }
        return id;
    }

    public void add_test_info(String name, String description, String category, Uri uri, int time_amount_min, String rating_system, Boolean is_MyTest){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_PICTURE_URI, String.valueOf(uri));
        cv.put(COLUMN_TIME_AMOUNT, time_amount_min);
        cv.put(COLUMN_RATING_SYSTEM, rating_system);
        cv.put(COLUMN_IS_MY_TEST, (is_MyTest)? 1: 0);
        DataBase.insert(TABLE_TEST_INFO, null, cv);
    }

    public void add_questions(String question, String answer_1, String answer_2, String answer_3, String answer_4, Integer ball_1, Integer ball_2, Integer ball_3, Integer ball_4, Integer true_answer, Uri uri, int TestID){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QUESTION, question);
        cv.put(COLUMN_ANSWER_1, answer_1);
        cv.put(COLUMN_ANSWER_2, answer_2);
        cv.put(COLUMN_ANSWER_3, answer_3);
        cv.put(COLUMN_ANSWER_4, answer_4);
        cv.put(COLUMN_BALL_1, ball_1);
        cv.put(COLUMN_BALL_2, ball_2);
        cv.put(COLUMN_BALL_3, ball_3);
        cv.put(COLUMN_BALL_4, ball_4);
        cv.put(COLUMN_TRUE_ANSWER, true_answer);
        cv.put(COLUMN_PICTURE_QUE, String.valueOf(uri));
        cv.put(COLUMN_TEST_ID, TestID);
        DataBase.insert(TABLE_QUESTIONS, null, cv);
    }

    public void add_result(String title, String description, String min_ball, String max_ball, int TestID){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_MIN, min_ball);
        cv.put(COLUMN_MAX, max_ball);
        cv.put(COLUMN_TEST_ID, TestID);
        DataBase.insert(TABLE_RESULTS, null, cv);
    }

    public void update_test_info(String name, String description, String category, Uri uri, int time_amount_min, String rating_system, Integer TestID){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_PICTURE_URI, String.valueOf(uri));
        cv.put(COLUMN_TIME_AMOUNT, time_amount_min);
        cv.put(COLUMN_RATING_SYSTEM, rating_system);
        cv.put(COLUMN_IS_MY_TEST, true);
        DataBase.update(TABLE_TEST_INFO, cv, COLUMN_ID + "=\"" + TestID + "\"", null);
    }

    public void update_questions(String question, String answer_1, String answer_2, String answer_3, String answer_4, Integer ball_1, Integer ball_2, Integer ball_3, Integer ball_4, Integer true_answer, Uri uri, int TestID){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QUESTION, question);
        cv.put(COLUMN_ANSWER_1, answer_1);
        cv.put(COLUMN_ANSWER_2, answer_2);
        cv.put(COLUMN_ANSWER_3, answer_3);
        cv.put(COLUMN_ANSWER_4, answer_4);
        cv.put(COLUMN_BALL_1, ball_1);
        cv.put(COLUMN_BALL_2, ball_2);
        cv.put(COLUMN_BALL_3, ball_3);
        cv.put(COLUMN_BALL_4, ball_4);
        cv.put(COLUMN_TRUE_ANSWER, true_answer);
        cv.put(COLUMN_PICTURE_QUE, String.valueOf(uri));
        cv.put(COLUMN_TEST_ID, TestID);
        DataBase.update(TABLE_QUESTIONS, cv, COLUMN_TEST_ID + "=\"" + TestID + "\" AND " + COLUMN_QUESTION + " LIKE '"+question+"'", null);
    }

    public void delete_test(Integer TestID){
        DataBase.delete(TABLE_TEST_INFO, COLUMN_ID + "=\"" + TestID + "\"", null);
        DataBase.delete(TABLE_QUESTIONS, COLUMN_TEST_ID + "=\"" + TestID + "\"", null);
        DataBase.delete(TABLE_RESULTS, COLUMN_TEST_ID + "=\"" + TestID + "\"", null);
    }

    public void delete_all(){
        DataBase.delete(TABLE_TEST_INFO, null, null);
        DataBase.delete(TABLE_QUESTIONS, null, null);
        DataBase.delete(TABLE_RESULTS, null, null);
    }

    class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("CREATE TABLE "   + TABLE_TEST_INFO + "(" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME          + " TEXT, " +
                    COLUMN_DESCRIPTION   + " TEXT, " +
                    COLUMN_CATEGORY      + " TEXT, " +
                    COLUMN_PICTURE_URI   + " TEXT, " +
                    COLUMN_RATING_SYSTEM + " TEXT, " +
                    COLUMN_IS_MY_TEST    + " INTEGER, " +
                    COLUMN_TIME_AMOUNT   + " INT);");

            db.execSQL("CREATE TABLE "   + TABLE_QUESTIONS + "(" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_QUESTION      + " TEXT, " +
                    COLUMN_ANSWER_1      + " TEXT, " +
                    COLUMN_ANSWER_2      + " TEXT, " +
                    COLUMN_ANSWER_3      + " TEXT, " +
                    COLUMN_ANSWER_4      + " TEXT, " +
                    COLUMN_BALL_1        + " INTEGER, " +
                    COLUMN_BALL_2        + " INTEGER, " +
                    COLUMN_BALL_3        + " INTEGER, " +
                    COLUMN_BALL_4        + " INTEGER, " +
                    COLUMN_TRUE_ANSWER   + " INTEGER, " +
                    COLUMN_PICTURE_QUE   + " TEXT, "    +
                    COLUMN_TEST_ID       + " INTEGER, " +
                    "FOREIGN KEY (TestID) REFERENCES Test_Info (_id));");

            db.execSQL("CREATE TABLE "   + TABLE_RESULTS + "(" +
                    COLUMN_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE         + " TEXT, " +
                    COLUMN_DESCRIPTION   + " TEXT, " +
                    COLUMN_MIN           + " TEXT,"  +
                    COLUMN_MAX           + " TEXT,"  +
                    COLUMN_TEST_ID       + " INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST_INFO);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
            onCreate(db);
        }
    }
}
