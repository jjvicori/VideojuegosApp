package com.example.appkotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class AdapterVideojuego: RecyclerView.Adapter<AdapterVideojuego.ViewHolder>() {

    var videojuegos: ArrayList<Videojuego?> = ArrayList()
    lateinit var context: Context

    fun AdapterVideojuego(videojuegos : ArrayList<Videojuego?>, context: Context){
        this.videojuegos = videojuegos
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = videojuegos.get(position)
        if (item != null) {
            holder.bind(item, context)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_videojuego, parent, false))
    }

    override fun getItemCount(): Int {
        return videojuegos.size
    }



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName = view.findViewById(R.id.item_nombre) as TextView
        val itemDescription = view.findViewById(R.id.item_descripcion) as TextView
        val itemCategoria = view.findViewById(R.id.item_categoria) as TextView
        val itemImagen = view.findViewById(R.id.itemImagen) as ImageView

        fun bind(videojuego:Videojuego, context: Context){
            itemName.text = videojuego.nombre
            itemDescription.text = videojuego.descripcion
            itemCategoria.text = videojuego.categoria
            loadImagen(videojuego.imagen,context)
        }

        fun loadImagen(name: String, context: Context){

            // Reference to an image file in Cloud Storage
           val storageReference = FirebaseStorage.getInstance().reference.child("videojuegos/$name")

            storageReference.downloadUrl.addOnSuccessListener {
                Picasso.with(context).load(it).into(itemImagen);
            }.addOnFailureListener {
                // Handle any errors
            }

//            Glide.with(context)
//                .load(storageReference)
//                .into(itemImagen)


        }
    }

}
