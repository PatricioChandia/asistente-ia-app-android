package com.devst.voicegpt.db;

import android.provider.BaseColumns;

// Este 'Contract' define la estructura (schema) de la base de datos.
// Es una 'best practice' para mantener los nombres organizados.
public final class NotesContract {

    // Para prevenir que alguien la instancie por error
    private NotesContract() {}

    // Definición de la tabla 'notes'
    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}