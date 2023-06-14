package com.example.monitordenivel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.monitordenivel.models.Equipamento;

import java.util.ArrayList;

public class EquipamentoAdapter extends ArrayAdapter<Equipamento> {

    private final Context context;
    private final ArrayList<Equipamento> elementos;


    public EquipamentoAdapter(Context context, ArrayList<Equipamento> elementos) {
        super(context, R.layout.linha_equipamento, elementos);
        this.context = context;
        this.elementos = elementos;

    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linha_equipamento, parent, false);

        TextView tvId = (TextView) rowView.findViewById(R.id.equipnentID);
        TextView tvMac = (TextView) rowView.findViewById(R.id.equipnentMac);

        tvId.setText(elementos.get(position).getId() + "");
        tvMac.setText(elementos.get(position).getMac() + "");

        return rowView;
    }
}
