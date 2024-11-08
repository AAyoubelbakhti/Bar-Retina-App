package com.example.barretina;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class CtrlPrincipal extends AppCompatActivity {

    public TextView txtTitle;
    public Button btnProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_principal); // Vincula con el layout XML

        // Asocia los elementos de la UI a las variables
        txtTitle = findViewById(R.id.txtTitle);
        btnProducts = findViewById(R.id.btnProducts);


        btnProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.sendMessageToServer("productes", null);
            }
        });

    }
}
