package com.example.barretina;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class CtrlConfig extends AppCompatActivity {

    public static CtrlConfig instance;
    public EditText txtName;
    public EditText txtHost;

    public TextView txtMessage;


    public static CtrlConfig getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_config);
        instance = this;
        txtName = findViewById(R.id.txtName);
        txtHost = findViewById(R.id.txtHost);

        txtMessage = findViewById(R.id.txtMessage);

        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnConfig = findViewById(R.id.btnConfig);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServer();
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConfig();
            }
        });

        // Lanza la actividad CtrlConfig mediante un Intent para iniciar la configuración
        if (isConfigFile()) {
            Log.d("CtrlConfig", "Existe el archivo CONFIG");
            Main.connectToServer(this, CtrlConfig.this, true);

        }


    }

    private void connectToServer() {
        Log.d("CtrlConfig", "Conectando al servidor...");
        Main.connectToServer(this, CtrlConfig.this, false);

    }



    private void setConfig() {
        txtName.setText("Jose Juan");
        txtHost.setText("barretina1");
    }

    // Método para mostrar el mensaje final del juego
    public void showEndGameMessage(String title, String message) {
        // Muestra el mensaje al usuario
        txtMessage.setText(title + ": " + message);
    }

    public void saveDataToXml(CtrlConfig ctrlConfig, String host, String name) {
        try {
            if (host.isEmpty() || name.isEmpty()) {
                ctrlConfig.txtMessage.setText("ERROR: rellena todos los campos");
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("host", host);
            editor.putString("name", name);

            if (editor.commit()) {
                Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("CtrlConfig", "Error al guardar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean isConfigFile() {
        // Obtener SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        if (sharedPreferences.contains("host") && sharedPreferences.contains("name")) {
            Log.d("CtrlConfig",  "Estoy en el isConfigFile()");
            // Leer los valores de las claves
            String host = sharedPreferences.getString("host", "");
            String name = sharedPreferences.getString("name", "");

            txtName.setText(name);
            txtHost.setText(host);
            return true;
        }
        return false;
    }

}
