package com.example.mytest;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Test implements Parcelable {

    public int isMyTest;
    public String rating_system;
    private String name;
    private String description;
    private String Category;
    private Uri uri;
    private int time_amount;
    private HashMap<Integer, String[]> Questions;
    private HashMap<Integer, Integer[]> Balls;
    private HashMap<Integer, String[]> Results;

    protected Test(Parcel in) {
        name = in.readString();
        description = in.readString();
        Category = in.readString();
        time_amount = in.readInt();
        isMyTest = in.readInt();
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return Category;
    }

    public Uri getUri() {
        return uri;
    }

    public int getTime_amount() {
        return time_amount;
    }

    public HashMap<Integer, String[]> getQuestions() {
        return Questions;
    }

    public HashMap<Integer, String[]> getResults (){return Results;}

    public HashMap<Integer, Integer[]> getBalls() {
        return Balls;
    }

    public void setQuestions(HashMap<Integer, String[]> Questions) {
        this.Questions = Questions;
    }

    public void setResults(HashMap<Integer, String[]> Results){this.Results = Results;}

    public void setBalls(HashMap<Integer, Integer[]> Balls) {
        this.Balls = Balls;
    }

    public Test(String name, String description, String category, Uri uri, int time_amount_min, String rating_system) {
        this.name = name;
        this.description = description;
        this.Category = category;
        this.uri = uri;
        this.time_amount = time_amount_min;
        this.rating_system = rating_system;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(Category);
        dest.writeInt(time_amount);
        dest.writeInt(isMyTest);
    }
}





