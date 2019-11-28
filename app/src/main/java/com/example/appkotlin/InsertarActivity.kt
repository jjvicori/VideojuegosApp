package com.example.appkotlin

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID.randomUUID
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.lang.Exception
import java.util.*


class InsertarActivity : AppCompatActivity() {

    lateinit var etNombre: EditText
    lateinit var etCategoria: EditText
    lateinit var etDescripcion: EditText
    lateinit var imImage: ImageView
    lateinit var imagen: String
    lateinit var uniqueID: String
    private lateinit var database: DatabaseReference
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var storageReference: StorageReference? = null
    private var withImage: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertar)
        init()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imImage.setImageBitmap(bitmap)
                withImage = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun init(){
        uniqueID =  UUID.randomUUID().toString()
        imagen = "default.jpg"
        database = FirebaseDatabase.getInstance().reference.child("videojuegos")
        storageReference = FirebaseStorage.getInstance().reference
        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etCategoria = findViewById(R.id.etCategoria)
        imImage = findViewById(R.id.ivInsertar)
    }


    fun seleccionarImagen(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)

    }

    fun insertar(view: View) {

        if(etNombre.text.isEmpty() || etDescripcion.text.isEmpty() || etCategoria.text.isEmpty()){
            Toast.makeText(this, "Debes de rellenar todos los campos" , Toast.LENGTH_LONG).show()
        }else{

            if(withImage){
                subirImagen()
            }else{
                guardarVideojuego()
            }

        }
    }

    private fun guardarVideojuego(){
        val v = Videojuego(uniqueID,etNombre.text.toString(),etDescripcion.text.toString(),this.imagen, etCategoria.text.toString())
        database.child(uniqueID).setValue(v)
        Toast.makeText(this, "Videojuego insertado" , Toast.LENGTH_LONG).show()

        etNombre.setText("")
        etCategoria.setText("")
        etDescripcion.setText("")

        uniqueID =  UUID.randomUUID().toString()
    }
    private fun subirImagen(){
        if(filePath != null){

            imagen= obtenerNombreImagen((filePath as  Uri))
            val ref = storageReference?.child("videojuegos/"+imagen)
            val uploadTask = ref?.putFile(filePath!!)

             uploadTask?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    guardarVideojuego()
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Carga una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    fun obtenerNombreImagen(uri: Uri): String {
           var result: String = ""
          if (uri.getScheme().equals("content")) {
            var cursor: Cursor? = contentResolver.query(uri, null,null,null,null)
            try {
              if (cursor != null && cursor.moveToFirst()) {
                result = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
              }
            } finally {
              cursor?.close()
            }
          }else{
              result = "default.jpg";
          }

           return result
     }
}
