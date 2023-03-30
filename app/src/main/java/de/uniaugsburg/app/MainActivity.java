package de.uniaugsburg.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uniaugsburg.app.databinding.ActivityMainBinding;
import de.uniaugsburg.app.util.JsonParser;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final int REQUEST_CAMERA_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);

        Calendar currentDate = Calendar.getInstance();
        int currentDay = currentDate.get(Calendar.DAY_OF_YEAR);

        int lastStoredDay = sharedPref.getInt("lastStoredDay", -1);
        if (lastStoredDay != currentDay) {
            // Reset stored data for new day
            SharedPreferences.Editor editor = sharedPref.edit();
            Map<String, List<Integer>> map= new HashMap<>();
            try {
                JsonParser.writeJsonCalories(map, getBaseContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editor.clear();
            editor.apply();
        }

        // Store the current day as the last stored day
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("lastStoredDay", currentDay);
        editor.apply();

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_camera)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CAMERA_PERMISSION)
    private void methodRequiresTwoPermission() {
        String[] perms = {android.Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_rationale),
                    REQUEST_CAMERA_PERMISSION, perms);
        }
    }
}