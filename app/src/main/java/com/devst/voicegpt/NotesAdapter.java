package com.devst.voicegpt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.devst.voicegpt.db.Note;
import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private OnNoteListener listener;

    // 1. Constructor que acepta el listener
    public NotesAdapter(OnNoteListener listener) {
        this.listener = listener;
    }

    // 2. ViewHolder (el 'molde' para cada fila)
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoteTitle, tvNoteContentPreview, tvNoteTimestamp;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvNoteContentPreview = itemView.findViewById(R.id.tvNoteContentPreview);
            tvNoteTimestamp = itemView.findViewById(R.id.tvNoteTimestamp);
        }

        // 4. Bind (conectar datos con la vista)
        public void bind(final Note note, final OnNoteListener listener) {
            tvNoteTitle.setText(note.getTitle());
            tvNoteContentPreview.setText(note.getContent());
            tvNoteTimestamp.setText(note.getTimestamp());

            // Listener para un clic CORTO (editar)
            itemView.setOnClickListener(v -> listener.onNoteClick(note));

            // Listener para un clic LARGO (borrar)
            itemView.setOnLongClickListener(v -> {
                listener.onNoteLongClick(note);
                return true; // Importante: consumir el clic
            });
        }
    }

    // 3. OnCreate (crear el ViewHolder)
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    // 5. onBind (llamar al método 'bind' del ViewHolder)
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position), listener);
    }

    // 6. getItemCount (cuántos items hay)
    @Override
    public int getItemCount() {
        return notes.size();
    }

    // 7. Método para actualizar la lista de notas
    public void setNotes(List<Note> noteList) {
        this.notes = noteList;
        notifyDataSetChanged(); // Refrescar toda la lista
    }

    // 8. Interfaz para manejar los clics (editar y borrar)
    public interface OnNoteListener {
        void onNoteClick(Note note); // Para editar
        void onNoteLongClick(Note note); // Para borrar
    }
}