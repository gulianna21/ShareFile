package com.example.simplenotes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.simplenotes.databinding.ActivityMainBinding;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null)
            this.save(data.getData().getPath());
        Log.d("Test", Objects.requireNonNull(data.getData()).getPath());
    }

    private AppBarConfiguration appBarConfiguration;

    String filename = "myFile.txt";
    String fileContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.simplenotes.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        binding.fab.setOnClickListener(view -> this.test());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void test() {
        Log.d("Test", this.isExternalStorageWritable());
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(
                Intent.EXTRA_TITLE,
                filename
        );
        startActivityForResult(intent, 0);
    }

    public String isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ? "true" : "false";
    }

    private void save(String filepath) {
        fileContent = "1234567";
        if (isStoragePermissionGranted()) {
            if (!fileContent.equals("")) {
                File myExternalFile = new File(getExternalFilesDir(filepath), filename);
                Log.d("Test", myExternalFile.getPath());
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(myExternalFile);
                    fos.write(fileContent.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Information saved to SD card.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Text field can not be empty.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public boolean isStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }

    private boolean isExternalStorageAvailableForRW() {
        String extStorageState = Environment.getExternalStorageState();
        return extStorageState.equals(Environment.MEDIA_MOUNTED);
    }
}