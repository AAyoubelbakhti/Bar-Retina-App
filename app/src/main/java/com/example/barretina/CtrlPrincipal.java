package com.example.barretina;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private Button btnVerComandas;
    private TextView txtContador;
    private TextView txtMesa;
    public interface OnComandasReceivedListener {
        void onComandasReceived(String comandasJson);
    }

    private OnComandasReceivedListener listener;

    // Permite que otras clases configuren el listener
    public void setOnComandasReceivedListener(OnComandasReceivedListener listener) {
        this.listener = listener;
    }

//    public void cargarComandas(String comandasJson) {
//        // Aquí procesas el JSON de las comandas
//        Log.d("CtrlPrincipal", "Comandas recibidas: " + comandasJson);
//    }




    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_principal);
        btnVerComandas = findViewById(R.id.btnVerComandas);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Recibe las comandas actualizadas desde CtrlComanda
                        String updatedComandas = result.getData().getStringExtra("updatedComandas");
                        cargarComandas(updatedComandas);
                    }
                }
        );

        btnVerComandas.setOnClickListener(v -> {
            Intent intent = new Intent(this, CtrlComanda.class);
            intent.putExtra("comandas", comandas.toString());
            launcher.launch(intent); // Lanza CtrlComanda esperando el resultado
        });



        listViewProductes = findViewById(R.id.listViewProductes);
        txtContador = findViewById(R.id.txtContador);
        txtMesa = findViewById(R.id.txtMesa);

        // Obtener el JSON de productos desde los extras
        String productesString = getIntent().getStringExtra("productesString");
        if (productesString != null) {
            try {
                JSONArray productosJsonArray = new JSONArray(productesString);
                cargarProductos(productosJsonArray);
            } catch (JSONException e) {
                Log.e("CtrlPrincipal", "Error parsing JSON 1: " + e.getMessage());
            }
        }

        int mesaId = getIntent().getIntExtra("mesaId", 0);
        txtMesa.setText("Mesa: " + mesaId);

        String comandasString = getIntent().getStringExtra("comandasString");
        if (comandasString != null) {
            try {

                cargarComandas(comandasString);
            } catch (Exception e) {
                Log.e("CtrlPrincipal", "Error parsing JSON 2: " + e.getMessage());
            }
        }

//        // Solicitar las comandas al servidor
//        Main.sendMessageToServer("select-comanda", null);

        // Configura el botón para mostrar las comandas
        btnVerComandas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.changeView("CtrlComanda",null, comandas.toString());
            }
        });
    }

    // en comandes usar la "comandaTXT para sacar la info) *********
    public void cargarProductos(JSONArray productosJsonArray) {
        //txtMesa.setText("Mesa: " + String.valueOf(Main.mesaId));
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
            Log.e("CtrlPrincipal", "Error parsing JSON 3: " + e.getMessage());
        }
    }

    // Método para cargar las comandas desde el servidor
    public void cargarComandas(String comandasString) {
        comandas = new JSONArray();
        try {
            JSONArray comandasArray = new JSONArray(comandasString);

            for (int i = 0; i < comandasArray.length(); i++) {
                JSONObject item = comandasArray.getJSONObject(i);
                String estado = item.getString("estat_comanda");
                int mesa = item.getInt("id_taula"); // Asegúrate de que id_taula sea un entero
                if (estado.equals("en curs") && mesa == Main.mesaId) {
                    JSONArray comandasBBDD = new JSONArray(item.getString("comanda"));
                    for (int y = 0; y < comandasBBDD.length(); y++) {
                        comandas.put(comandasBBDD.getJSONObject(y));
                    }
                }
            }

            txtContador.setText("Productos: " + comandas.length());
        } catch (JSONException e) {
            Log.e("CtrlPrincipal", "Error al cargar las comandas: " + e.getMessage());
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
}
