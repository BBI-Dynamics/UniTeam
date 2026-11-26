package com.example.uniteam;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolder> {

    public interface OnTareaAccionListener {
        void onCambiarEstado(Tarea tarea);
    }

    private List<Tarea> tareas = new ArrayList<>();
    private final OnTareaAccionListener listener;

    public TareaAdapter(OnTareaAccionListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(List<Tarea> nuevasTareas) {
        this.tareas = nuevasTareas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tarea t = tareas.get(position);
        holder.tvTitulo.setText(t.getTitulo());
        holder.tvMateria.setText("Materia: " + t.getMateria());
        holder.tvCreador.setText("Creada por: " + t.getCreadorNombre());
        holder.tvImportancia.setText(t.getImportancia());

        if (t.getDescripcion() == null || t.getDescripcion().isEmpty()) {
            holder.tvDescripcion.setVisibility(View.GONE);
        } else {
            holder.tvDescripcion.setVisibility(View.VISIBLE);
            holder.tvDescripcion.setText(t.getDescripcion());
        }

        // Color del badge según importancia
        switch (t.getImportancia()) {
            case "Alta":
                holder.tvImportancia.setBackgroundColor(Color.parseColor("#D32F2F"));
                break;
            case "Media":
                holder.tvImportancia.setBackgroundColor(Color.parseColor("#F57C00"));
                break;
            default:
                holder.tvImportancia.setBackgroundColor(Color.parseColor("#388E3C"));
                break;
        }

        if (t.isCompletada()) {
            holder.btnAccion.setText("Mover a pendientes");
        } else {
            holder.btnAccion.setText("Marcar como completada");
        }

        holder.btnAccion.setOnClickListener(v -> listener.onCambiarEstado(t));
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvImportancia, tvMateria, tvDescripcion, tvCreador;
        MaterialButton btnAccion;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTareaTitulo);
            tvImportancia = itemView.findViewById(R.id.tvTareaImportancia);
            tvMateria = itemView.findViewById(R.id.tvTareaMateria);
            tvDescripcion = itemView.findViewById(R.id.tvTareaDescripcion);
            tvCreador = itemView.findViewById(R.id.tvTareaCreador);
            btnAccion = itemView.findViewById(R.id.btnTareaAccion);
        }
    }
}