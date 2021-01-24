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
        db.execSQL("CREATE TABLE COMIDA(NOMBRE VARCHAR(200), PRECIO INTEGER)")
        db.execSQL("INSERT INTO COMIDA " +
                "VALUES ('hamburguesa',35)," +
                "('hotdog',25)," +
                "('tacos',45)," +
                "('torta',10)," +
                "('sincronizada',30)," +
                "('chocomilk',15)," +
                "('refrsco',12)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}