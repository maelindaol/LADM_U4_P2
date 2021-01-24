package mx.tecnm.tepic.ladm_u4_p2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ENTRANTES(CELULAR VARCHAR(200), MENSAJE VARCHAR(2000))")
        db.execSQL("CREATE TABLE ALUMNO(NOMBRE VARCHAR(200), CALIFICACION INTEGER)")
        db.execSQL("INSERT INTO ALUMNO " +
                "VALUES ('Elizabeth',100)," +
                "('Alejandra',100)," +
                "('anahi',100)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}