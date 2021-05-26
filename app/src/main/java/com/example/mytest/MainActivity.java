package com.example.mytest;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navigation;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager.beginTransaction().add(R.id.frag_container, new frag_all_tests()).commit();
        setTitle("Все тесты");
        navigation = findViewById(R.id.navigation);
        navigation.setItemIconTintList(getColorStateList(R.color.white));

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            boolean all = true;

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.navigation_my:
                        if (all) {
                            all = false;
                            frag_my_tests fragment_my = new frag_my_tests();

                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.animator.slide_in_left_back, R.animator.slide_in_left)
                                    .replace(R.id.frag_container, fragment_my)
                                    .commit();
                            setTitle("Мои тесты");
                        }
                        break;

                    case R.id.navigation_all:
                        if (!all) {
                            all = true;
                            frag_all_tests fragment_all = new frag_all_tests();
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.animator.slide_in_right_back, R.animator.slide_in_right)
                                    .replace(R.id.frag_container, fragment_all)
                                    .commit();
                            setTitle("Все тесты");
                        }
                        break;
                }
                return false;
            }
        });
    }
}












