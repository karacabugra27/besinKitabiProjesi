package com.example.besinkitabiprojesi.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.besinkitabiprojesi.adapter.BesinRecyclerAdapter
import com.example.besinkitabiprojesi.databinding.FragmentBesinListeBinding
import com.example.besinkitabiprojesi.service.BesinAPI
import com.example.besinkitabiprojesi.viewmodel.BesinListesiViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class BesinListeFragment : Fragment() {

    private var _binding: FragmentBesinListeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var viewModel: BesinListesiViewModel
    private val besinRecyclerAdapter = BesinRecyclerAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBesinListeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BesinListesiViewModel::class.java]
        viewModel.refreshData()

        binding.besinRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.besinRecyclerView.adapter = besinRecyclerAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.besinRecyclerView.visibility = View.GONE
            binding.besinHataMesaji.visibility = View.GONE
            binding.besinProgressBar.visibility = View.GONE
            viewModel.refreshDataFromInternet()
            binding.swipeRefreshLayout.isRefreshing = false // otomatik çıkan refresh görselini kapattık
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.besinler.observe(viewLifecycleOwner) {
            besinRecyclerAdapter.besinListesiniGuncelle(it)
            binding.besinRecyclerView.visibility = View.VISIBLE
        }

        viewModel.besinHataMesaji.observe(viewLifecycleOwner) {
            if (it) {
                binding.besinHataMesaji.visibility = View.VISIBLE
                binding.besinRecyclerView.visibility = View.GONE
            } else {
                binding.besinHataMesaji.visibility = View.GONE
            }
        }

        viewModel.besinYukleniyor.observe(viewLifecycleOwner) {
            if (it) {
                binding.besinProgressBar.visibility = View.VISIBLE
                binding.besinRecyclerView.visibility = View.GONE
                binding.besinHataMesaji.visibility = View.GONE
            } else {
                binding.besinProgressBar.visibility = View.GONE
            }
        }
    }

}