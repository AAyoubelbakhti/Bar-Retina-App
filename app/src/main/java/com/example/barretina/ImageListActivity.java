package com.example.barretina;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.ArrayList;

public class ImageListActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "image_prefs";  // Nombre del archivo SharedPreferences
    private static final String KEY_IMAGES = "images";       // Clave para obtener las imágenes

    private ListView listView;
    private ArrayList<Bitmap> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        listView = findViewById(R.id.listView);
        images = new ArrayList<>();

        try {
            // Recuperar el JSON desde SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String imagesJson = sharedPreferences.getString(KEY_IMAGES, "");  // Recupera el JSON guardado

            if (!imagesJson.isEmpty()) {
                JSONArray jsonArray = new JSONArray(imagesJson);

                // Decodificar las imágenes en Base64
                for (int i = 0; i < jsonArray.length(); i++) {
                    String base64Image = jsonArray.getString(i);
                    Bitmap bitmap = decodeBase64(base64Image);
                    images.add(bitmap);
                }

                // Configurar el ListView con el adaptador
                ImageAdapter adapter = new ImageAdapter(this, images);
                listView.setAdapter(adapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeBase64(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
