package com.tabour.hospitality.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tabour.hospitality.models.Eshtablishment
import com.tabour.hospitality.repository.SearchRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {

    lateinit var searchRepo: SearchRepo

    companion object{
        var eshtb_progressbar = MutableLiveData<Boolean>()
        var eshtblshmntListData = MutableLiveData<ArrayList<Eshtablishment>>()
    }

    fun getEshtablshmnt(context: Context, restro_name: String,seating_options: String, distance_radius: String){
        searchRepo = SearchRepo.getInstance()

        viewModelScope.launch(Dispatchers.IO)
        {
            searchRepo.getEshtablishment(context = context, restro_name,seating_options, distance_radius)
        }

    }


}