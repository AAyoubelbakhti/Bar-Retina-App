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
    public static int mesaId;

    public static Context getContext() {
        return mContext;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mesaId = 0;
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
//            wsClient = UtilsWS.getSharedInstance("wss://barretina1.ieti.site:443");
            wsClient = UtilsWS.getSharedInstance("ws://10.0.2.2:4545");
            Main.changeView("CtrlTaula", null, null);

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
                    String productesString = msgObj.getString("products");
                    String comandasString = msgObj.getString("body");

                    Main.changeView("CtrlPrincipal", productesString, comandasString);
                    break;
                case "comanda_llesta":
                    break;
//                case "comandes":
//                    String comnadasString = msgObj.getString("body");
//                    ctrlPrincipal.cargarComandas(comnadasString);
//                    break;



                // Agrega otros casos según tus necesidades
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void changeView(String viewName, String productesString, String comandasString) {
        Intent intent;
        switch (viewName) {
            case "CtrlConfig":
                intent = new Intent(mContext, CtrlConfig.class);
                break;
            case "CtrlTaula":
                intent = new Intent(mContext, CtrlTaula.class);
                break;
            case "CtrlPrincipal":
                intent = new Intent(mContext, CtrlPrincipal.class);
                intent.putExtra("productesString", productesString);
                intent.putExtra("comandasString", comandasString);
                intent.putExtra("mesaId", mesaId); // Pasa el ID de la mesa
                break;

            case "CtrlComanda":
                intent = new Intent(mContext, CtrlComanda.class);
                intent.putExtra("comandas", comandasString); // Pasa el JSON como extra
                //intent.putExtra("mesaId", Main.mesaId);
                Log.d("changeView", "Datos JSON pasados: " + comandasString);
                break;

            default:
                Log.e("changeView", "Vista desconocida: " + viewName);
                return;


        }
        if (mContext != null ) {
            Log.d("changeView", "Contexto bien.");
            try {
                mContext.startActivity(intent);
            } catch (Exception e){
                Log.e("changeView", e.toString());
            }
        } else {
            Log.e("changeView", "Contexto es null. No se puede cambiar de vista.");
        }
        //mContext.startActivity(intent);
    }



    public static void sendMessageToServer(String type, JSONObject data) {
        if (wsClient != null) {
            JSONObject message = new JSONObject();
            try {
                message.put("type", type);
                message.put("body", data);



                wsClient.safeSend(message.toString());
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
