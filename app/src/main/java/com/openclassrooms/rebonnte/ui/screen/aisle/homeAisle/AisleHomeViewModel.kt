package com.openclassrooms.rebonnte.ui.screen.aisle.homeAisle

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.model.Aisle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AisleHomeViewModel @Inject constructor() : ViewModel() {
    private val _aisles = MutableStateFlow<List<Aisle>>(emptyList())
    val aisles: StateFlow<List<Aisle>> get() = _aisles
}