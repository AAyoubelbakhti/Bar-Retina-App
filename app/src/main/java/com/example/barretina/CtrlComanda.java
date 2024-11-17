package com.example.barretina;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CtrlComanda extends AppCompatActivity {

    private ListView listViewComandas;
    private ComandaAdapter adapter; // Adaptador para mostrar la comanda
    private JSONArray comandas = new JSONArray(); // Lista de comandas
    private Button btnEnviar;
    private Button btnActualizar;
    private TextView txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_comanda);

        listViewComandas = findViewById(R.id.listViewComandas);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnActualizar = findViewById(R.id.btnActualizar);
        txtTotal = findViewById(R.id.txtTotal);

        // Obtener comandas del Intent
        String comandasString = getIntent().getStringExtra("comandas");
        if (comandasString != null) {
            try {
                comandas = new JSONArray(comandasString);
                cargarComandas();
            } catch (JSONException e) {
                Log.e("CtrlComandas", "Error al cargar JSON: " + e.getMessage());
            }
        }

        // Configuración del botón "Enviar"
        btnEnviar.setOnClickListener(v -> enviarComanda());

        // Configuración del botón "Actualizar"
        btnActualizar.setOnClickListener(v -> actualizarVista());
    }

    private void cargarComandas() {
        List<Producte> productesComanda = new ArrayList<>();
        try {
            double total = 0.0;

            for (int i = 0; i < comandas.length(); i++) {
                JSONObject jsonComanda = comandas.getJSONObject(i);
                String id = jsonComanda.getString("id");
                String nom = jsonComanda.getString("nom");
                String descripcio = jsonComanda.getString("descripcio");
                String imatge = jsonComanda.optString("imatge", "");
                double preu = jsonComanda.getDouble("preu");
                int quantitat = jsonComanda.getInt("quantitat");

                total += preu;

                Producte producte = new Producte(id, nom, descripcio, imatge, preu / quantitat, quantitat);
                productesComanda.add(producte);
            }

            txtTotal.setText("Total: €" + total);

            adapter = new ComandaAdapter(this, productesComanda, this::modificarProducto);
            listViewComandas.setAdapter(adapter);

        } catch (JSONException e) {
            Log.e("CtrlComandas", "Error al cargar comandas: " + e.getMessage());
        }
    }

    private void modificarProducto(Producte producte, int delta) {
        try {
            for (int i = 0; i < comandas.length(); i++) {
                JSONObject jsonComanda = comandas.getJSONObject(i);
                if (jsonComanda.getString("id").equals(producte.getId())) {
                    int nuevaCantidad = jsonComanda.getInt("quantitat") + delta;
                    if (nuevaCantidad > 0) {
                        jsonComanda.put("quantitat", nuevaCantidad);
                        jsonComanda.put("preu", nuevaCantidad * producte.getPreu());
                    } else {
                        comandas.remove(i);
                    }
                    break;
                }
            }
            cargarComandas();
        } catch (JSONException e) {
            Log.e("CtrlComandas", "Error al modificar producto: " + e.getMessage());
        }
    }

    private void enviarComanda() {
        // Simula el envío de la comanda al servidor
        Log.d("CtrlComandas", "Comanda enviada: " + comandas.toString());
        finish(); // Finaliza la actividad después del envío
    }

    private void actualizarVista() {
        cargarComandas();
    }
}
