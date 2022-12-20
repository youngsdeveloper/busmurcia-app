package com.youngsdeveloper.bus_murcia.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.youngsdeveloper.bus_murcia.R
import it.skrape.core.htmlDocument
import it.skrape.selects.html5.img
import it.skrape.selects.html5.table
import it.skrape.selects.html5.tr
import kotlinx.android.synthetic.main.fragment_alert.*
import kotlinx.android.synthetic.main.fragment_route.*
import kotlinx.android.synthetic.main.fragment_route.loading_realtime
import java.lang.Float.max
import java.lang.Float.min
import java.net.URL
import java.nio.charset.Charset
import kotlin.concurrent.thread
import com.bumptech.glide.request.target.Target


class AlertFragment : Fragment(R.layout.fragment_alert) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loading_realtime.indeterminateDrawable.setColorFilter(
            resources.getColor(R.color.tmp_murcia),
            android.graphics.PorterDuff.Mode.SRC_IN);

        fetchData()

    }

    private fun fetchData(){


        thread {

            val source = URL("http://tmpmurcia.es/Cuerpo.asp?codigo="+requireArguments().getString("codigo")).readText(Charset.forName("ISO-8859-1"))
            val imageSRC = parseHTML(source)
            requireActivity().runOnUiThread {
                println(imageSRC)

                Glide
                    .with(this)
                    .load("http://tmpmurcia.es$imageSRC")
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            //TODO: something on exception
                            loading_realtime.visibility = View.GONE

                            return false
                        }
                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            //do something when picture already loaded
                            loading_realtime.visibility = View.GONE
                            return false
                        }
                    })
                    .into(image_alerta)
            }
        }
    }

    private fun parseHTML(html:String):String{
        var imageSRC = htmlDocument(html) {
            table{
                findFirst {
                    tr {
                        img{
                            findSecond {
                                eachSrc
                            }
                        }
                    }
                }
            }
        }
        return imageSRC[0].replace(" ","")
    }

}