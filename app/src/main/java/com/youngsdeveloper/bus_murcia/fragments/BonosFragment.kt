package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.youngsdeveloper.bus_murcia.R
import de.codecrafters.tableview.model.TableColumnWeightModel
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import kotlinx.android.synthetic.main.fragment_bonos.*


class BonosFragment : Fragment(R.layout.fragment_bonos) {


    private var SENCILLOS_DATA =
        arrayOf(
            arrayOf("Tranvía Zona 1", "1,05€"),
            arrayOf("Tranvía Zona 2", "1,40€"),
            arrayOf("Bus Urbano", "1,05€"),
            arrayOf("Bus Pedanías", "1,85€"),

            )
    private var TRICOLOR_DATA =
        arrayOf(
                arrayOf("Tranvía", "0,28€"),
                arrayOf("Bus urbano", "0,28€"),
                arrayOf("Tranvía + Bus urbano", "0,46€"),
                arrayOf("Bus pedanías", "0,46€"),
                arrayOf("Bus pedanias + Tranvía", "0,64€"),
                arrayOf("Bus pedanias + Bus urbano", "0,64€"),
                arrayOf("Tercer transbordo", "+0,06€"),

            )

    private var TRICOLOR_ESTUDIANTE_DATA =
        arrayOf(
            arrayOf("Tranvía", "0,20€"),
            arrayOf("Bus urbano", "0,20€"),
            arrayOf("Tranvía + Bus urbano", "0,34€"),
            arrayOf("Bus pedanías", "0,34€"),
            arrayOf("Bus pedanias + Tranvía", "0,48€"),
            arrayOf("Bus pedanias + Bus urbano", "0,48€"),
            arrayOf("Tercer transbordo", "+0,06€"),

            )

    private var TRICOLOR_FN_DATA =
        arrayOf(
            arrayOf("Viaje tranvia/bus", "0,20€"),
            arrayOf("Viaje con 1 transbordo", "0,38€"),
            arrayOf("Viaje con 2 transbordos", "0,44€"),
        )

    private var TRICOLOR_TRANVIA_DATA =
        arrayOf(
            arrayOf("Bono mensual general", "12€/mes"),
            arrayOf("Bono mensual estudiantes", "8€/mes"),
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val columnModel = TableColumnWeightModel(2)
        columnModel.setColumnWeight(0, 4)
        columnModel.setColumnWeight(1, 2)

        tableSencillos.columnModel = columnModel
        tableSencillos.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        tableSencillos.dataAdapter = SimpleTableDataAdapter(requireContext(), SENCILLOS_DATA)

        tableTricolorGeneral.columnModel = columnModel
        tableTricolorGeneral.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        tableTricolorGeneral.dataAdapter = SimpleTableDataAdapter(requireContext(), TRICOLOR_DATA)

        tableTricolorEstudiante.columnModel = columnModel
        tableTricolorEstudiante.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        tableTricolorEstudiante.dataAdapter = SimpleTableDataAdapter(requireContext(), TRICOLOR_ESTUDIANTE_DATA)

        tableTricolorFamiliaNumerosa.columnModel = columnModel
        tableTricolorFamiliaNumerosa.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        tableTricolorFamiliaNumerosa.dataAdapter = SimpleTableDataAdapter(requireContext(), TRICOLOR_FN_DATA)

        tableTricolorTranvia.columnModel = columnModel
        tableTricolorTranvia.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        tableTricolorTranvia.dataAdapter = SimpleTableDataAdapter(requireContext(), TRICOLOR_TRANVIA_DATA)


    }
}