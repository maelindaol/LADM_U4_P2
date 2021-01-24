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
                var alumn = ""
                var calificacion = ""

                if(info.size>1 && info[0] == "calificacion") {
                    try {
                        val cursor = BaseDatos(context,"entrantes",null,1)
                            .readableDatabase
                            .rawQuery("SELECT * FROM COMIDA WHERE NOMBRE = '${info[1]}'",null)

                        if(cursor.moveToFirst()){
                            alumn = cursor.getString(0)
                            calificacion = cursor.getInt(1).toString()

                            respuesta = "La calificacion de $alumn es de $calificacion %"
                        } else {
                            respuesta = "Alumno no registrado!!"
                        }
                        cursor.close()
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    respuesta = "ERROR \n Ingrese 'CALIFICACION' y el nombre alumno"
                }
                SmsManager.getDefault().sendTextMessage(cliente,null, respuesta,null,null)
                Toast.makeText(context,"GRACIAS",Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}