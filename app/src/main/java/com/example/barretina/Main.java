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
    CtrlConfig ctrlConfig;

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
//        if (wsClient != null) {
//            wsClient.forceExit();
//        }
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

    public static void connectToServer(Activity activity, CtrlConfig ctrlConfig) {
        ctrlConfig.txtMessage.setText("Connecting ...");
        Log.d("barretina", "estoy conectando");
        // Implementa delay usando Handler para Android
        new android.os.Handler().postDelayed(() -> {
            String host = String.valueOf(ctrlConfig.txtHost.getText());
            String name = String.valueOf(ctrlConfig.txtName.getText());

            ctrlConfig.saveDataToXml(ctrlConfig, host, name);
            ctrlConfig.txtMessage.setText("Datos obtenidos ...");

            wsClient = UtilsWS.getSharedInstance("wss://" + host + ".ieti.site:443");

            Main.changeView("CtrlPrincipal");




        }, 1500);
    }




    private static void wsMessage(String response, CtrlConfig ctrlConfig) {
        try {
            JSONObject msgObj = new JSONObject(response);
            switch (msgObj.getString("type")) {
                case "clients":
                    // Aquí puedes manejar la vista correspondiente
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

    public static void changeView(String viewName) {
        // Lanza la actividad CtrlConfig mediante un Intent para iniciar la configuración
        Intent intent;

        switch (viewName) {
            case "CtrlConfig":
                intent = new Intent(mContext, CtrlConfig.class);
                mContext.startActivity(intent);
                break;
            case "CtrlPrincipal":
                intent = new Intent(mContext, CtrlPrincipal.class);
                mContext.startActivity(intent);
                break;
        }
    }

//    public static void sendMessageToServer(String type, JSONObject data) {
//        if (wsClient != null) {
//            JSONObject message = new JSONObject();
//            try {
//                message.put("type", type);
//                message.put("data", data); // Datos específicos de la acción
//
//                wsClient.safeSend(message.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Failed to send message");
//            }
//        } else {
//            System.out.println("WebSocket client is not initialized.");
//        }
//    }

    private void test(){

    }
    private static void wsError(String response, CtrlConfig ctrlConfig) {
        String connectionRefused = "Connection refused";
        if (response.contains(connectionRefused)) {
            ctrlConfig.txtMessage.setText(connectionRefused);
            new android.os.Handler().postDelayed(() -> ctrlConfig.txtMessage.setText(""), 1500);
        }
    }
}
