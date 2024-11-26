package com.example.barretina;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
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
    private double preuComanda = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_comanda);

        // Configuración del OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Prepara el resultado con las comandas actualizadas
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedComandas", comandas.toString());
                setResult(RESULT_OK, resultIntent);

                // Finaliza la actividad
                finish();
            }
        });


        listViewComandas = findViewById(R.id.listViewComandas);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnActualizar = findViewById(R.id.btnActualizar);
        txtTotal = findViewById(R.id.txtTotal);

        // Obtener comandas del Intent
        String comandasString = getIntent().getStringExtra("comandas");
        if (comandasString != null) {
            try {
                comandas = new JSONArray(comandasString);
                listarComandas();
            } catch (JSONException e) {
                Log.e("CtrlComandas", "Error al cargar JSON: " + e.getMessage());
            }
        }

        // Configuración del botón "Enviar"
        btnEnviar.setOnClickListener(v -> enviarComanda());

        // Configuración del botón "Actualizar"
        btnActualizar.setOnClickListener(v -> actualizarVista("pendent"));
    }






    private void listarComandas() {
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


                String imageName = imatge.substring(0, imatge.lastIndexOf('.'));

                // Obtener el ID del recurso de la imagen usando su nombre
                Log.d("CtrlPrincipal", imageName);

                int imatgeResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                // Si el recurso no se encuentra, usar una imagen predeterminada
                if (imatgeResId == 0) {
                    imatgeResId = R.drawable.round_button; // Asegúrate de tener esta imagen en res/drawable
                }


                Producte producte = new Producte(id, nom, descripcio, imatge, preu / quantitat, quantitat,imatgeResId);
                productesComanda.add(producte);
            }
            preuComanda = total;
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
            listarComandas();
            actualizarVista("en curs"); // Actualizar el estado tras sumar/restar
        } catch (JSONException e) {
            Log.e("CtrlComandas", "Error al modificar producto: " + e.getMessage());
        }
    }

    private void enviarComanda() {
        Log.d("CtrlComandas", "Comanda enviada: " + comandas.toString());
        JSONObject comandasJson = new JSONObject();
        try {
            comandasJson.put("idTaula", Main.mesaId);
            comandasJson.put("idCambrer", 1);
            comandasJson.put("estatComanda", "pendent");
            comandasJson.put("preuComanda", preuComanda);
            comandasJson.put("comandaTxt", comandas);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Main.sendMessageToServer("insert-comanda", comandasJson);
        finish(); // Finaliza la actividad después del envío
    }

    private void actualizarVista(String estatComanda) {
        Log.d("CtrlComandas", "Actualizando comanda con estado: " + estatComanda);
        JSONObject comandasJson = new JSONObject();
        try {
            comandasJson.put("idTaula", Main.mesaId);
            comandasJson.put("idCambrer", 1);
            comandasJson.put("estatComanda", estatComanda);
            comandasJson.put("preuComanda", preuComanda);
            comandasJson.put("comandaTxt", comandas);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Main.sendMessageToServer("update-comanda", comandasJson);
        listarComandas(); // Actualizar la lista de comandas en la vista
    }
}

