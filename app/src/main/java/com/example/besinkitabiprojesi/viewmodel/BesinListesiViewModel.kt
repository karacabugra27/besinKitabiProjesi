package com.example.besinkitabiprojesi.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.besinkitabiprojesi.model.Besin
import com.example.besinkitabiprojesi.roomdb.BesinDatabase
import com.example.besinkitabiprojesi.service.BesinAPIServis
import com.example.besinkitabiprojesi.util.OzelSharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BesinListesiViewModel(application: Application) : AndroidViewModel(application) {

    val besinler = MutableLiveData<List<Besin>>()
    val besinHataMesaji = MutableLiveData<Boolean>()
    val besinYukleniyor = MutableLiveData<Boolean>()

    private val besinApiServis = BesinAPIServis()
    private val ozelSharedPreferences = OzelSharedPreferences(getApplication())

    private val guncellemeZamani = 10 * 60 * 1000 * 1000 * 1000L

    fun refreshData() {
        val kaydedilmeZamani = ozelSharedPreferences.zamaniAl()

        if (kaydedilmeZamani != null && kaydedilmeZamani != 0L && System.nanoTime() - kaydedilmeZamani < guncellemeZamani) { //roomdan verileri al
            verileriRoomdanAl()
        } else {
            verileriInternettenAl()
        }
    }

    fun refreshDataFromInternet(){
        verileriInternettenAl()
    }

    private fun verileriInternettenAl() {
        besinYukleniyor.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val besinListesi = besinApiServis.getData()
            withContext(Dispatchers.Main) {
                besinYukleniyor.value = false //room kaydedeceğiz
                roomaKaydet(besinListesi)
                Toast.makeText(getApplication(), "Besinleri internetten aldık", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun besinleriGoster(besinListesi: List<Besin>) {
        besinler.value = besinListesi
        besinHataMesaji.value = false
        besinYukleniyor.value = false
    }

    private fun roomaKaydet(besinListesi: List<Besin>) {
        viewModelScope.launch {

            val dao = BesinDatabase(getApplication()).besinDao()
            dao.deleteAllBesin()

            var uuidListesi = dao.insertAll(*besinListesi.toTypedArray()) //tek tek verir
            var i = 0
            while (i < besinListesi.size) {
                besinListesi[i].uuid = uuidListesi[i].toInt()
                i++
            }

            besinleriGoster(besinListesi)
        }
        ozelSharedPreferences.zamaniKaydet(System.nanoTime())
    }

    private fun verileriRoomdanAl() {
        besinYukleniyor.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val besinListesi = BesinDatabase(getApplication()).besinDao().getAllBesin()
            withContext(Dispatchers.Main) {
                besinleriGoster(besinListesi)
                Toast.makeText(getApplication(),"Besinleri roomdan aldık",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
