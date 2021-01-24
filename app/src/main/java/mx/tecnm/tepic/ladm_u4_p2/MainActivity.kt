package mx.tecnm.tepic.ladm_u4_p2

import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val siPermiso = 1
    var hilito = Hilo(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
            && (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS,android.Manifest.permission.SEND_SMS),siPermiso)
        }

        hilito.start()

        registrar.setOnClickListener {
            if(nombreP.text.toString() == "" || precioP.text.toString() == ""){
                Toast.makeText(this,"LLENE TODOS LOS CAMPOS PORFAVOR",Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            //GUARDAR SOBRE TABLA SQLITE
            try {
                var baseDatos = BaseDatos(this, "entrantes", null, 1)
                var insertar = baseDatos.writableDatabase
                var nombre = nombreP.text.toString().toLowerCase()
                var precio = precioP.text.toString().toInt()

                var SQL = "INSERT INTO COMIDA VALUES('${nombre}','${precio}')"

                insertar.execSQL(SQL)
                baseDatos.close()
            }catch (err: SQLiteException){
                Toast.makeText(this, err.message, Toast.LENGTH_LONG)
                    .show()
            }
            nombreP.setText("")
            precioP.setText("")
            Toast.makeText(this,"PLATILLO REGISTRADO",Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == siPermiso){
            mensajeRecibir()
        }

    }

    private fun mensajeRecibir() {
        AlertDialog.Builder(this).setMessage("SE OTORGARON PERMISOS DE LEER Y ENVIAR").show()
    }

}

class Hilo(p: MainActivity): Thread(){
    var puntero = p

    override fun run(){
        super.run()

        while(true){
            try {
                val cursor = BaseDatos(puntero,"entrantes",null,1)
                    .readableDatabase
                    .rawQuery("SELECT * FROM ENTRANTES",null)

                var ultimo = ""

                if(cursor.moveToFirst()){
                    do{
                        ultimo = "ULTIMO MENSAJE RECIBIDO\nCELULAR ORIGEN: "+cursor.getString(0)+
                                "\nMENSAJE SMS: "+cursor.getString(1)

                    } while (cursor.moveToNext())
                } else {
                    ultimo = "SIN MENSAJES AUN, TABLA VACIA"
                }
                puntero.runOnUiThread {
                    puntero.ultimoMensaje.setText(ultimo)
                }
                cursor.close()
            } catch (err: SQLiteException){
                puntero.runOnUiThread {
                    Toast.makeText(puntero,err.message, Toast.LENGTH_LONG).show()
                }
            }
            sleep(200)
        }
        sleep(200)
    }
}