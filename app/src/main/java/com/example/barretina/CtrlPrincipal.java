package com.example.barretina;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    public static CtrlConfig ctrlConfig;
    private ListView listViewProductes;
    private ProducteAdapter adapter;
    private JSONArray comandas = new JSONArray(); // Lista de comandas
    private Button btnVerComandas;
    private TextView txtContador;
    private TextView txtMesa;
    private JSONArray productosJsonArray;

    public void setCtrlCofig(CtrlConfig ctrlConfig){
        this.ctrlConfig = ctrlConfig;
    }
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

      ;

        listViewProductes = findViewById(R.id.listViewProductes);
        txtContador = findViewById(R.id.txtContador);
        txtMesa = findViewById(R.id.txtMesa);

        // Obtener el JSON de productos desde los extras
        String productesString = getIntent().getStringExtra("productesString");
        if (productesString != null) {
            try {
               productosJsonArray = new JSONArray(productesString);
                cargarProductos(productosJsonArray, null);
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

        Button btnPostres = findViewById(R.id.btnPostres);
        Button btnTapas = findViewById(R.id.btnTapas);
        Button btnBebidas = findViewById(R.id.btnBebidas);
        Button btnTodos = findViewById(R.id.btnTodos);
        Button btnConfig = findViewById(R.id.btnConfig);

        // Filtros de categorías
        btnTodos.setOnClickListener(v ->  cargarProductos(productosJsonArray, null));
        btnPostres.setOnClickListener(v ->  cargarProductos(productosJsonArray, "Postre"));
        btnTapas.setOnClickListener(v ->     cargarProductos(productosJsonArray, "Tapa"));
        btnBebidas.setOnClickListener(v ->     cargarProductos(productosJsonArray, "Bebida"));

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarConfig();
            }
        });
    }


    public void cargarProductos(JSONArray productosJsonArray, String categoria) {
        Boolean add = true;

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
                String categoriasString =  jsonProducte.getString("categoria");
                Log.d("CtrlPrincipal", categoriasString);
                if (categoria != null) {
                    add = false;
                    if (categoriasString.toLowerCase().contains(categoria.toLowerCase())) {
                        add = true;
                    }
                }

                if ( add ) {
                    String imageName = imatge.substring(0, imatge.lastIndexOf('.'));

                    // Obtener el ID del recurso de la imagen usando su nombre
                    Log.d("CtrlPrincipal", imageName);

                    int imatgeResId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                    // Si el recurso no se encuentra, usar una imagen predeterminada
                    if (imatgeResId == 0) {
                        imatgeResId = R.drawable.round_button; // Asegúrate de tener esta imagen en res/drawable
                    }

                    Producte producte = new Producte(id, nom, descripcio, imatge, preu, 0, imatgeResId);
                    productes.add(producte);
                }
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
                    //Log.d("CtrlPrincipal", comandasString.toString());
                    if (item.getString("comanda").contains("[")) {
                        JSONArray comandasBBDD = new JSONArray(item.getString("comanda"));
                        for (int y = 0; y < comandasBBDD.length(); y++) {
                            comandas.put(comandasBBDD.getJSONObject(y));
                        }
                    }else {
                        Log.d("CtrlPrincipal", "Manolo: " + item.getString("comanda"));
                        comandas.put(item.getString("comanda"));
                    }
                }
            }
            Log.d("CtrlPrincipal", String.valueOf(comandas.length()));
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

    private void cambiarConfig() {
        // Crear un layout para el AlertDialog con dos campos EditText
        final EditText editTextName = new EditText(this);
        final EditText editTextHost = new EditText(this);
        // Configurar los EditText (puedes poner sugerencias, hints, etc.)
        editTextName.setHint("Introduce el nombre");
        editTextHost.setHint("Introduce el host");
        // Crear un LinearLayout que contenga ambos EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editTextName);
        layout.addView(editTextHost);
        setCtrlCofig(CtrlConfig.getInstance());
        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Configuración")
                .setMessage("Introduce el nombre y host:")
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
                        actulizarConfigXML(host, name);
                    }
                })
                .setNegativeButton("Cancelar", null) // Botón para cancelar
                .show(); // Mostrar el AlertDialog
    }
    public void actulizarConfigXML(String host, String name){
        Log.d("CtrlPrincipal", "Estory actualizando");
        ctrlConfig.saveDataToXml(ctrlConfig, host, name);
    }
}
