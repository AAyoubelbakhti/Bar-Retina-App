package com.example.barretina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity {
    private static Context mContext;
    public static UtilsWS wsClient;
    private static CtrlConfig ctrlConfig;
    private static CtrlPrincipal ctrlPrincipal;

    public static Context getContext() {
        return mContext;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = new Intent(Main.this, CtrlConfig.class);
        startActivity(intent);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wsClient != null) {
            wsClient.forceExit();
        }
    }



    public static <T> List<T> jsonArrayToList(JSONArray array, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            T value = null;
            try {
                value = clazz.cast(array.get(i));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            list.add(value);
        }
        return list;
    }

    public static void connectToServer(Activity activity, CtrlConfig ctrlConfig, Boolean isConfig) {

        // Lanza la actividad CtrlConfig mediante un Intent para iniciar la configuración
        ctrlConfig.txtMessage.setText("Click button to Connect");
        if (isConfig) {
            Log.d("CtrlConfig", "Existe el archivo CONFIG");
            String host = String.valueOf(ctrlConfig.txtHost.getText());

        }else {
            Log.d("ConnectServ", "estoy conectando");
            // Implementa delay usando Handler para Android
            String host = String.valueOf(ctrlConfig.txtHost.getText());
            String name = String.valueOf(ctrlConfig.txtName.getText());
            ctrlConfig.saveDataToXml(ctrlConfig, host, name);
            ctrlConfig.txtMessage.setText("Datos obtenidos ...");
        }

        new android.os.Handler().postDelayed(() -> {
            // wsClient = UtilsWS.getSharedInstance("wss://" + host + ".ieti.site:443");
            wsClient = UtilsWS.getSharedInstance("ws://10.0.2.2:4545");
            Main.sendMessageToServer("productes", null);
            wsClient.onMessage((response) -> {
                activity.runOnUiThread(() -> {
                    Log.d("WS_MESSAGE", "Mensaje recibido: " + response);
                    wsMessage(response, ctrlConfig);
                });
            });
            wsClient.onError((response) -> {
                activity.runOnUiThread(() -> {
                    Log.e("WS_ERROR", "Error en WebSocket: " + response);
                    wsError(response, ctrlConfig);
                });
            });


            //Main.changeView("CtrlPrincipal");
        }, 1500);

    }




    private static void wsMessage(String response, CtrlConfig ctrlConfig) {
        try {
            JSONObject msgObj = new JSONObject(response);

            switch (msgObj.getString("type")) {
                case "productes":
                    Log.d("CtrlPrincipal2", msgObj.toString());
                    String productsString = msgObj.getString("products");
                    Main.changeView("CtrlPrincipal", productsString);

                    break;

                case "countdown":
                    int value = msgObj.getInt("value");
                    String txt = value == 0 ? "GO" : String.valueOf(value);
                    if (value == 0) {
                        // Cambiar vista según sea necesario
                    }
                    ctrlConfig.txtMessage.setText(txt);
                    break;

                case "gameOver":
                    String winner = msgObj.getJSONObject("data").getString("winner");
                    String message = winner.equals("") ? "Has ganado" : "Has perdido";
                    ctrlConfig.showEndGameMessage(message, message);
                    break;

                // Agrega otros casos según tus necesidades
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void changeView(String viewName, String jsonData) {
        Intent intent;
        switch (viewName) {
            case "CtrlConfig":
                intent = new Intent(mContext, CtrlConfig.class);
                break;
            case "CtrlPrincipal":
                intent = new Intent(mContext, CtrlPrincipal.class);
                intent.putExtra("jsonData", jsonData); // Pasa el JSON como extra
                break;
            default:
                return;
        }
        mContext.startActivity(intent);
    }


    public static void sendMessageToServer(String type, JSONObject data) {
        if (wsClient != null) {
            JSONObject message = new JSONObject();
            try {
                message.put("type", type);
                message.put("data", data); // Datos específicos de la acción

                wsClient.safeSend(type);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to send message");
            }
        } else {
            System.out.println("WebSocket client is not initialized.");
        }
    }


    private static void wsError(String response, CtrlConfig ctrlConfig) {
        String connectionRefused = "Connection refused";
        if (response.contains(connectionRefused)) {
            ctrlConfig.txtMessage.setText(connectionRefused);
            new android.os.Handler().postDelayed(() -> ctrlConfig.txtMessage.setText(""), 1500);
        }
    }


}
