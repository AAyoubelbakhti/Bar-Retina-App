package com.example.barretina;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ProducteAdapter extends ArrayAdapter<Producte> {

    public ProducteAdapter(Context context, List<Producte> productes) {
        super(context, 0, productes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_producte, parent, false);
        }

        Producte producte = getItem(position);

        TextView txtNom = convertView.findViewById(R.id.producte_nom);
        TextView txtDescripcio = convertView.findViewById(R.id.producte_descripcio);
        TextView txtPreu = convertView.findViewById(R.id.producte_preu);

        txtNom.setText(producte.getNom());
        txtDescripcio.setText(producte.getDescripcio());
        txtPreu.setText("€ " + producte.getPreu());

        return convertView;
    }
}
