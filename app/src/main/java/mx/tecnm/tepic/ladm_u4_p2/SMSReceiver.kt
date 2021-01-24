package mx.tecnm.tepic.ladm_u4_p2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.Toast
import java.lang.Exception

class SmsReceiver : BroadcastReceiver() {
    var cliente = ""

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras

        if(extras != null) {
            var sms = extras.get("pdus") as Array<Any>

            for(indice in sms.indices){
                var formato = extras.getString("format")

                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray, formato)
                } else {
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }

                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                cliente = celularOrigen!!

                //GUARDAR SOBRE TABLA SQLITE
                try {
                    var baseDatos = BaseDatos(context, "entrantes", null, 1)
                    var insertar = baseDatos.writableDatabase
                    var SQL = "INSERT INTO ENTRANTES VALUES('${celularOrigen}','${contenidoSMS}')"

                    insertar.execSQL(SQL)
                    baseDatos.close()
                }catch (err: SQLiteException){
                    Toast.makeText(context, err.message, Toast.LENGTH_LONG).show()
                }

                val info = contenidoSMS.toLowerCase().split(" ");
                var respuesta = ""
                var producto = ""
                var costo = ""

                if(info.size>1 && info[0] == "precio") {
                    //CONSULTAR EN TABLA SQLITE
                    try {
                        val cursor = BaseDatos(context,"entrantes",null,1)
                            .readableDatabase
                            .rawQuery("SELECT * FROM COMIDA WHERE NOMBRE = '${info[1]}'",null)

                        if(cursor.moveToFirst()){
                            producto = cursor.getString(0)
                            costo = cursor.getInt(1).toString()

                            respuesta = "El costo de $producto es de $costo pesos"
                        } else {
                            respuesta = "Lo sentimos, no contamos con ese platillo"
                        }
                        cursor.close()
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    respuesta = "Por favor respete la sintaxis\nMande la palabra 'PRECIO' seguido del nombre del platillo del que quiere saber el costo \n EJEMPLO:\n PRECIO HAMBURGUESA"
                }
                SmsManager.getDefault().sendTextMessage(cliente,null, respuesta,null,null)
                Toast.makeText(context,"SE ENVIO RESPUESTA",Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}