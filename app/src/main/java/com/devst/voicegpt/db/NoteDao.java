package com.devst.voicegpt.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Esta clase es nuestro DAO (Data Access Object).
// Contiene todos los métodos CRUD.
public class NoteDao {

    private NotesDbHelper dbHelper;
    private static final String TAG = "NoteDao";

    public NoteDao(Context context) {
        this.dbHelper = new NotesDbHelper(context);
    }

    // Método para obtener el timestamp actual
    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // =======================================================
    // CREATE (Crear)
    // =======================================================
    public long createNote(String title, String content) {
        // Obtenemos la base de datos en modo "escritura"
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, title);
        values.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, content);
        values.put(NotesContract.NoteEntry.COLUMN_NAME_TIMESTAMP, getCurrentTimestamp());

        // Insertar la nueva fila, devolviendo el ID
        long newRowId = db.insert(NotesContract.NoteEntry.TABLE_NAME, null, values);

        Log.d(TAG, "Nota creada con ID: " + newRowId);
        db.close();
        return newRowId;
    }

    // =======================================================
    // READ (Leer todas)
    // =======================================================
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Columnas que queremos obtener
        String[] projection = {
                NotesContract.NoteEntry._ID,
                NotesContract.NoteEntry.COLUMN_NAME_TITLE,
                NotesContract.NoteEntry.COLUMN_NAME_CONTENT,
                NotesContract.NoteEntry.COLUMN_NAME_TIMESTAMP
        };

        // Ordenar por ID descendente (las más nuevas primero)
        String sortOrder = NotesContract.NoteEntry._ID + " DESC";

        Cursor cursor = db.query(
                NotesContract.NoteEntry.TABLE_NAME, // La tabla
                projection,                         // Las columnas
                null,                               // WHERE (null = todas)
                null,                               // Argumentos del WHERE
                null,                               // group by
                null,                               // having
                sortOrder                           // order by
        );

        // Iteramos sobre el cursor y creamos objetos Note
        while(cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(NotesContract.NoteEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NoteEntry.COLUMN_NAME_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NoteEntry.COLUMN_NAME_CONTENT));
            String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(NotesContract.NoteEntry.COLUMN_NAME_TIMESTAMP));
            notes.add(new Note(id, title, content, timestamp));
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Notas leídas: " + notes.size());
        return notes;
    }

    // =======================================================
    // UPDATE (Actualizar)
    // =======================================================
    public int updateNote(long id, String newTitle, String newContent) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesContract.NoteEntry.COLUMN_NAME_TITLE, newTitle);
        values.put(NotesContract.NoteEntry.COLUMN_NAME_CONTENT, newContent);
        values.put(NotesContract.NoteEntry.COLUMN_NAME_TIMESTAMP, getCurrentTimestamp()); // Actualizar timestamp

        // Qué fila actualizar (WHERE _id = ?)
        String selection = NotesContract.NoteEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(
                NotesContract.NoteEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        Log.d(TAG, "Notas actualizadas: " + count);
        db.close();
        return count; // Devuelve el número de filas afectadas
    }

    // =======================================================
    // DELETE (Borrar)
    // =======================================================
    public int deleteNote(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Qué fila borrar (WHERE _id = ?)
        String selection = NotesContract.NoteEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        int deletedRows = db.delete(NotesContract.NoteEntry.TABLE_NAME, selection, selectionArgs);

        Log.d(TAG, "Notas borradas: " + deletedRows);
        db.close();
        return deletedRows; // Devuelve el número de filas borradas
    }
}