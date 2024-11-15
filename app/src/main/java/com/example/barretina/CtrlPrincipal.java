package com.example.barretina;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CtrlPrincipal extends AppCompatActivity {
    private ListView listViewProductes;
    private ProducteAdapter adapter;
    private JSONArray comandas = new JSONArray(); // Lista de comandas
    private Button btnVerComandas; // Botón para ver comandas
    private TextView txtContador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_principal);

        listViewProductes = findViewById(R.id.listViewProductes);
        btnVerComandas = findViewById(R.id.btnVerComandas);
        txtContador = findViewById(R.id.txtContador);

        // Obtener el JSON desde los extras
        String productsString = getIntent().getStringExtra("jsonData");
        if (productsString != null) {
            try {
                JSONArray productosJsonArray = new JSONArray(productsString);
                cargarProductos(productosJsonArray);
            } catch (JSONException e) {
                Log.e("CtrlPrincipal", "Error parsing JSON: " + e.getMessage());
            }
        }

        // Configura el botón para mostrar comandas y el precio total
        btnVerComandas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarComandas();
            }
        });
    }

    public void cargarProductos(JSONArray productosJsonArray) {
        List<Producte> productes = new ArrayList<>();
        try {
            for (int i = 0; i < productosJsonArray.length(); i++) {
                JSONObject jsonProducte = productosJsonArray.getJSONObject(i);

                String id = jsonProducte.getString("id");
                String nom = jsonProducte.getString("nom");
                String descripcio = jsonProducte.getString("descripcio");
                String imatge = jsonProducte.optString("imatge", "");
                double preu = jsonProducte.getDouble("preu");

                Producte producte = new Producte(id, nom, descripcio, imatge, preu, 0);
                productes.add(producte);
            }

            adapter = new ProducteAdapter(this, productes);
            listViewProductes.setAdapter(adapter);

            listViewProductes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Producte selectedProducte = productes.get(position);
                    agregarAComandas(selectedProducte);
                }
            });
        } catch (JSONException e) {
            Log.e("CtrlPrincipal", "Error parsing JSON: " + e.getMessage());
        }
    }

    private void agregarAComandas(Producte producte) {
        try {
            boolean exists = false;
            for (int i = 0; i < comandas.length(); i++) {
                JSONObject item = comandas.getJSONObject(i);
                if (item.getString("id").equals(producte.getId())) {
                    exists = true;
                    item.put("quantitat", item.getInt("quantitat") + 1);
                    item.put("preu", item.getDouble("preu") + producte.getPreu());
                    break;
                }
            }

            if (!exists) {
                JSONObject newComanda = new JSONObject();
                newComanda.put("id", producte.getId());
                newComanda.put("nom", producte.getNom());
                newComanda.put("descripcio", producte.getDescripcio());
                newComanda.put("imatge", producte.getImatge());
                newComanda.put("preu", producte.getPreu());
                newComanda.put("quantitat", 1);
                comandas.put(newComanda);
            }

            txtContador.setText("Productos: " + comandas.length());
        } catch (JSONException e) {
            Log.e("CtrlPrincipal", "Error al agregar producto: " + e.getMessage());
        }
    }

    private void mostrarComandas() {
        double totalPrice = 0.0;
        StringBuilder details = new StringBuilder();

        for (int i = 0; i < comandas.length(); i++) {
            try {
                JSONObject product = comandas.getJSONObject(i);
                String name = product.getString("nom");
                double price = product.getDouble("preu");
                int quantitat = product.getInt("quantitat");

                totalPrice += price;
                details.append(name).append(" x").append(quantitat).append(" - €").append(price).append("\n");
            } catch (JSONException e) {
                Log.e("CtrlPrincipal", "Error al leer comando: " + e.getMessage());
            }
        }

        details.append("\nTotal: €").append(totalPrice);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Comandas")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }
}
