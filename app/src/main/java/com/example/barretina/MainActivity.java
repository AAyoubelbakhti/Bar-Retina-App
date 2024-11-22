
package com.example.barretina;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "image_prefs";  // Nombre del archivo SharedPreferences
    private static final String KEY_IMAGES = "images";       // Clave para almacenar las imágenes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Simulación de un JSON con imágenes en base64
        String jsonString = "[\"iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAIAAAD/gAIDAAAA6klEQVR4nO3QQQ3AIADAQEDINOOYWVhfZMmdgqbz2Wfwzbod8CdmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFZgVmBWYFL3ZqAnVgMPRtAAAAAElFTkSuQmCC\", \"/9j/4AAQSkZJRgABAQEAAAAAAAD/2wBDAAICAgMCAgMFBwkJDhgKDx8PEjgsJzMzNDYy...\"]";

        try {
            JSONArray jsonArray = new JSONArray(jsonString);  // Suponiendo que recibimos un array JSON con imágenes base64

            // Guardar el JSONArray en SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_IMAGES, jsonArray.toString());
            editor.apply();  // Guarda los datos de manera asíncrona

            // Iniciar la siguiente actividad
            Intent intent = new Intent(MainActivity.this, ImageListActivity.class);
            startActivity(intent);

        } catch (Exception e) {
            Log.e("MainActivity", "Error parsing JSON", e);
            Toast.makeText(this, "Error cargando las imágenes", Toast.LENGTH_SHORT).show();
        }
    }
}
