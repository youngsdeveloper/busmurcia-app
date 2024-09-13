package com.youngsdeveloper.bus_murcia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.youngsdeveloper.bus_murcia.R
import com.youngsdeveloper.bus_murcia.databinding.FragmentBonosBinding
import de.codecrafters.tableview.TableDataAdapter
import de.codecrafters.tableview.model.TableColumnWeightModel
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter


class BonosFragment : Fragment() {

    private var _binding: FragmentBonosBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBonosBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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

        binding.tableSencillos.columnModel = columnModel
        binding.tableSencillos.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        binding.tableSencillos.dataAdapter = SimpleTableDataAdapter(requireContext(), SENCILLOS_DATA) as TableDataAdapter<Any>

        binding.tableTricolorGeneral.columnModel = columnModel
        binding.tableTricolorGeneral.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        binding.tableTricolorGeneral.dataAdapter = SimpleTableDataAdapter(requireContext(), TRICOLOR_DATA) as TableDataAdapter<Any>

        binding.tableTricolorEstudiante.columnModel = columnModel
        binding.tableTricolorEstudiante.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        binding.tableTricolorEstudiante.dataAdapter = SimpleTableDataAdapter(requireContext(), TRICOLOR_ESTUDIANTE_DATA) as TableDataAdapter<Any>

        binding.tableTricolorFamiliaNumerosa.columnModel = columnModel
        binding.tableTricolorFamiliaNumerosa.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        binding.tableTricolorFamiliaNumerosa.dataAdapter = SimpleTableDataAdapter(requireContext(), TRICOLOR_FN_DATA) as TableDataAdapter<Any>

        binding.tableTricolorTranvia.columnModel = columnModel
        binding.tableTricolorTranvia.headerAdapter = SimpleTableHeaderAdapter(requireContext(), "Nombre", "Precio")
        binding.tableTricolorTranvia.dataAdapter = SimpleTableDataAdapter(requireContext(), TRICOLOR_TRANVIA_DATA) as TableDataAdapter<Any>


    }
}