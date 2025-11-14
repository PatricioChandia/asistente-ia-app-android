package com.devst.voicegpt.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDbHelper extends SQLiteOpenHelper {

    // Versión de la base de datos. Si cambias el schema, debes incrementar este número.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Notes.db";

    // Comando SQL para CREAR la tabla de notas
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + NotesContract.NoteEntry.TABLE_NAME + " (" +
                    NotesContract.NoteEntry._ID + " INTEGER PRIMARY KEY," + // _ID viene de BaseColumns
                    NotesContract.NoteEntry.COLUMN_NAME_TITLE + " TEXT," +
                    NotesContract.NoteEntry.COLUMN_NAME_CONTENT + " TEXT," +
                    NotesContract.NoteEntry.COLUMN_NAME_TIMESTAMP + " TEXT)";

    // Comando SQL para BORRAR la tabla (si actualizamos)
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + NotesContract.NoteEntry.TABLE_NAME;

    public NotesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Se llama la primera vez que se accede a la BD
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // Se llama si DATABASE_VERSION cambia
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Por ahora, solo borramos y recreamos (se pierden los datos)
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}