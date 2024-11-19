package com.example.barretina;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;

public class CtrlTaula extends AppCompatActivity {
    private static final int NUM_MESAS = 10; // Número total de mesas
    private GridLayout gridLayoutMesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_taula);

        gridLayoutMesas = findViewById(R.id.gridLayoutMesas);
        crearMesas();


    }


    /**
     * Crear botones redondos que representan las mesas.
     */
    private void crearMesas() {
        for (int i = 1; i <= NUM_MESAS; i++) {
            Button mesaButton = new Button(this);
            mesaButton.setText(String.valueOf(i)); // ID de la mesa
            mesaButton.setTag(i); // Asigna ID como etiqueta
            mesaButton.setBackgroundResource(R.drawable.round_button);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.setMargins(16, 16, 16, 16);
            mesaButton.setLayoutParams(layoutParams);

            

            mesaButton.setOnClickListener(v -> {
                int mesaId = (int) v.getTag();
                Main.mesaId = mesaId;

                Main.sendMessageToServer("productes", null);
                Log.d("CtrlPrincipal", "Mesa seleccionada: " + Main.mesaId);
            });

            gridLayoutMesas.addView(mesaButton);
        }
    }

}
