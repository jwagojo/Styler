/*
    Title: Styler
    Description:
    Group Members: Sowndaryan Jayaprakashanand, Wesley Agojo, Kevin Kieu, and Mena Hanna

    Sources used:


        Background for logo: https://as2.ftcdn.net/v2/jpg/06/36/22/53/1000_F_636225361_buyr8Ie0bS9rrO1eAKXA7EpVNDUmuxvs.jpg (from adobe stock images)
        Background: https://as2.ftcdn.net/v2/jpg/15/62/91/61/1000_F_1562916174_rU9yiNzKH2Vp9mETn6L07k76Q42JYcPt.jpg (from adobe stock images)
        font (road rage): https://www.dafont.com/road-rage.font

*/

package com.mobileapp.styler;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
