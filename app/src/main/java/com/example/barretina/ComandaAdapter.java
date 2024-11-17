package com.example.barretina;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class ComandaAdapter extends ArrayAdapter<Producte> {
    private Context context;
    private List<Producte> productes;
    private OnQuantityChangeListener listener;

    public ComandaAdapter(@NonNull Context context, List<Producte> productes, OnQuantityChangeListener listener) {
        super(context, 0, productes);
        this.context = context;
        this.productes = productes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comanda, parent, false);
        }

        Producte producte = productes.get(position);

        TextView txtNombre = convertView.findViewById(R.id.txtNombre);
        TextView txtCantidad = convertView.findViewById(R.id.txtCantidad);
        Button btnSumar = convertView.findViewById(R.id.btnSumar);
        Button btnRestar = convertView.findViewById(R.id.btnRestar);

        txtNombre.setText(producte.getNom());
        txtCantidad.setText("Cantidad: " + producte.getQuantitat());

        btnSumar.setOnClickListener(v -> listener.onQuantityChange(producte, 1));
        btnRestar.setOnClickListener(v -> listener.onQuantityChange(producte, -1));

        return convertView;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChange(Producte producte, int delta);
    }
}
