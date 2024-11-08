package com.example.barretina;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class CtrlPrincipal extends AppCompatActivity {

    public TextView txtTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_principal); // Vincula con el layout XML

        // Asocia los elementos de la UI a las variables
        txtTitle = findViewById(R.id.txtTitle);


        // Puedes inicializar otros valores o configurar el texto aquí si es necesario
        txtTitle.setText("Barretina Bar");

    }
}
