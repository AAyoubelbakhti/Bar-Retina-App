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

import com.example.barretina.R;

public class CtrlConfig extends AppCompatActivity {

    public EditText txtName;
    public EditText txtHost;

    public TextView txtMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctrl_config);

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
            new android.os.Handler().postDelayed(() -> {
                String host = String.valueOf(txtHost.getText());

               // Main.wsClient = UtilsWS.getSharedInstance("wss://" + host + ".ieti.site:443");
                Main.wsClient = UtilsWS.getSharedInstance("ws://10.0.2.2:4545");
                Main.changeView("CtrlPrincipal");
            }, 1500);

        }


    }

    private void connectToServer() {
        Log.d("CtrlConfig", "Conectando al servidor...");
        Main.connectToServer(this, CtrlConfig.this);

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
        // Obtener los valores de los EditText


        // Verificar que los valores no estén vacíos
        if (host.isEmpty() || name.isEmpty()) {
            ctrlConfig.txtMessage.setText("ERROR: rellena todos los campos");
            return;
        }

        // Obtener SharedPreferences y un editor
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Guardar los valores en el archivo XML
        editor.putString("host", host);
        editor.putString("name", name);

        // Confirmar cambios
        if (editor.commit()) {
            Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
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
