package com.devst.voicegpt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.voicegpt.db.Note;
import com.devst.voicegpt.db.NoteDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NotesActivity extends AppCompatActivity implements NotesAdapter.OnNoteListener {

    private NoteDao noteDao;
    private RecyclerView rvNotes;
    private NotesAdapter notesAdapter;
    private TextView tvNoNotes;
    private Toolbar toolbarNotes;
    private FloatingActionButton fabAddNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // 1. Inicializar el DAO (nuestro CRUD)
        noteDao = new NoteDao(this);

        // 2. Vincular Vistas
        toolbarNotes = findViewById(R.id.toolbarNotes);
        rvNotes = findViewById(R.id.rvNotes);
        tvNoNotes = findViewById(R.id.tvNoNotes);
        fabAddNote = findViewById(R.id.fabAddNote);

        // Configurar Toolbar
        setSupportActionBar(toolbarNotes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarNotes.setNavigationOnClickListener(v -> finish()); // Flecha de volver

        // 3. Configurar RecyclerView
        setupRecyclerView();

        // 4. Configurar Listeners
        fabAddNote.setOnClickListener(v -> showNoteDialog(null)); // null = crear nota nueva

        // 5. Cargar las notas
        loadNotes();
    }

    private void setupRecyclerView() {
        notesAdapter = new NotesAdapter(this); // 'this' = NotesActivity implementa OnNoteListener
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setAdapter(notesAdapter);
    }

    private void loadNotes() {
        // READ (Leer)
        List<Note> noteList = noteDao.getAllNotes();
        notesAdapter.setNotes(noteList);

        // Mostrar u ocultar el mensaje "No hay notas"
        if (noteList.isEmpty()) {
            tvNoNotes.setVisibility(View.VISIBLE);
            rvNotes.setVisibility(View.GONE);
        } else {
            tvNoNotes.setVisibility(View.GONE);
            rvNotes.setVisibility(View.VISIBLE);
        }
    }

    // Método para mostrar el diálogo de CREAR o ACTUALIZAR
    private void showNoteDialog(final Note noteToUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_note, null);
        builder.setView(dialogView);

        final EditText etNoteTitle = dialogView.findViewById(R.id.etNoteTitle);
        final EditText etNoteContent = dialogView.findViewById(R.id.etNoteContent);
        final TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        final boolean isUpdating = (noteToUpdate != null);

        // Si estamos actualizando, rellenar los campos
        if (isUpdating) {
            tvDialogTitle.setText("Editar Nota");
            etNoteTitle.setText(noteToUpdate.getTitle());
            etNoteContent.setText(noteToUpdate.getContent());
        } else {
            tvDialogTitle.setText("Nueva Nota");
        }

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String title = etNoteTitle.getText().toString().trim();
            String content = etNoteContent.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isUpdating) {
                // UPDATE (Actualizar)
                noteDao.updateNote(noteToUpdate.getId(), title, content);
                Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show();
            } else {
                // CREATE (Crear)
                noteDao.createNote(title, content);
                Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show();
            }

            loadNotes(); // Recargar la lista
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // --- Implementación de los clics del Adapter ---

    @Override
    public void onNoteClick(Note note) {
        // Clic corto = Editar (UPDATE)
        showNoteDialog(note);
    }


    @Override
    public void onNoteLongClick(Note note) {
        // Clic largo = Borrar (DELETE)
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Borrado")
                .setMessage("¿Estás seguro de que quieres borrar esta nota?")
                .setPositiveButton("Borrar", (dialog, which) -> {
                    noteDao.deleteNote(note.getId());
                    Toast.makeText(this, "Nota borrada", Toast.LENGTH_SHORT).show();
                    loadNotes(); // Recargar la lista
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}