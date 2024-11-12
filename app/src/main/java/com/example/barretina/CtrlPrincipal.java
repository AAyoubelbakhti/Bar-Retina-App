package com.example.barretina;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CtrlPrincipal extends AppCompatActivity {
    public static CtrlConfig ctrlConfig;
    private ListView listViewProductes;
    private ProducteAdapter adapter;
    private JSONArray comandas = new JSONArray(); // Lista de comandas
    private Button btnVerComandas; // Botón para ver comandas
    private Button btnConfig;
    private TextView txtContador;

    public void setCtrlCofig(CtrlConfig ctrlConfig){
        this.ctrlConfig = ctrlConfig;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_principal);

        listViewProductes = findViewById(R.id.listViewProductes);
        btnVerComandas = findViewById(R.id.btnVerComandas);
        btnConfig = findViewById(R.id.btnConfig);
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

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarConfig();
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
                String imatge = jsonProducte.getString("imatge");
                double preu = jsonProducte.getDouble("preu");

                Producte producte = new Producte(id, nom, descripcio, imatge, preu, 0);
                productes.add(producte);
            }

            adapter = new ProducteAdapter(this, productes);
            listViewProductes.setAdapter(adapter);

            // Agrega un OnItemClickListener para manejar el clic en cada producto
            listViewProductes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Producte selectedProducte = productes.get(position);

                    // Convertir el producto a JSONObject y añadirlo a "comandas"
                    JSONObject comanda = new JSONObject();

                    try {
                        Boolean exist = false;
                        for (int i = 0; i < comandas.length(); i++) {
                            JSONObject item = comandas.getJSONObject(i);

                            if(item.has("id") && item.getString("nom").equals(selectedProducte.getNom())){
                                exist = true;
                                selectedProducte.setQuantiat(selectedProducte.getQuantiat()+1);
                                double oldPrice = item.getDouble("preu");
                                int quantitat = selectedProducte.getQuantiat();

                                Log.d("CtrlPrincipal",String.valueOf(quantitat));
                                comandas.getJSONObject(i).put("preu", oldPrice + selectedProducte.getPreu());
                                comandas.getJSONObject(i).put("quantitat", (quantitat));
                                break;
                            }
                        }
                        if (!exist){
                            comanda.put("id", selectedProducte.getId());
                            comanda.put("nom", selectedProducte.getNom());
                            comanda.put("descripcio", selectedProducte.getDescripcio());
                            comanda.put("imatge", selectedProducte.getImatge());
                            comanda.put("preu", selectedProducte.getPreu());
                            comanda.put("quantitat", 1);

                            comandas.put(comanda); // Añadir el producto a "comandas"
                        }
                        Log.d("CtrlPrincipal", "Producto añadido a comandas: " + comanda.toString());
                        String numProductes = String.valueOf(comandas.length());
                        txtContador.setText("Productos: " + numProductes);

                    } catch (JSONException e) {
                        Log.e("CtrlPrincipal", "Error al añadir producto a comandas: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            Log.e("CtrlPrincipal", "Error parsing JSON: " + e.getMessage());
        }
    }

    private void mostrarComandas() {
        // Calcula el precio total y muestra el detalle de las comandas
        double totalPrice = 0.0;
        StringBuilder details = new StringBuilder();

        for (int i = 0; i < comandas.length(); i++) {
            try {
                JSONObject product = comandas.getJSONObject(i);
                String name = product.getString("nom");
                double price = product.getDouble("preu");
                int quantitat = product.getInt("quantitat");

                // Suma el precio al total y agrega el nombre al detalle
                totalPrice += price;
                details.append(name).append(" x").append(quantitat).append(" - $").append(price).append("\n");

            } catch (JSONException e) {
                Log.e("CtrlPrincipal", "Error al leer comando: " + e.getMessage());
            }
        }

        // Añade el precio total al final del detalle
        details.append("\nTotal: $").append(totalPrice);

        // Muestra un diálogo con el detalle y el total
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Comandas")
                .setMessage(details.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void cambiarConfig() {
        // Crear un layout para el AlertDialog con dos campos EditText
        final EditText editTextName = new EditText(this);
        final EditText editTextHost = new EditText(this);

        // Configurar los EditText (con hints y estilos)
        editTextName.setHint("Introduce el nombre");
        editTextHost.setHint("Introduce el host");

        // Aplicar estilo a los EditText para mejorar su apariencia
        editTextName.setPadding(20, 20, 20, 20); // Añadir padding interno
        editTextHost.setPadding(20, 20, 20, 20); // Añadir padding interno

        // Establecer el fondo blanco con bordes redondeados para los EditText
        editTextName.setBackgroundResource(R.drawable.edit_text_style);
        editTextHost.setBackgroundResource(R.drawable.edit_text_style);

        // Configurar un LinearLayout con orientación vertical
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32); // Añadir padding externo
        layout.setGravity(Gravity.CENTER_HORIZONTAL); // Centrar los elementos

        // Añadir los EditText al LinearLayout
        layout.addView(editTextName);
        layout.addView(editTextHost);

        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Configuración")
                .setMessage("Introduce el nombre y el host:")
                .setView(layout) // Establece el layout con los EditText
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener los valores ingresados por el usuario
                        String name = editTextName.getText().toString();
                        String host = editTextHost.getText().toString();

                        // Imprimir los valores en Log.d
                        Log.d("Config", "Nombre: " + name);
                        Log.d("Config", "Host: " + host);
                        actualizarConfigXML(host, name);
                    }
                })
                .setNegativeButton("Cancelar", null) // Botón para cancelar
                .show(); // Mostrar el AlertDialog
    }


    public void actualizarConfigXML(String host, String name){
        Log.d("CtrlPrincipal", "Estory actualizando");
        ctrlConfig.saveDataToXml(ctrlConfig, host, name);
    }

}
