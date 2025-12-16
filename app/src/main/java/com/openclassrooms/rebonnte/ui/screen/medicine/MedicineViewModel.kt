package com.openclassrooms.rebonnte.ui.screen.medicine

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.model.Aisle
import com.openclassrooms.rebonnte.domain.model.History
import com.openclassrooms.rebonnte.domain.model.Medicine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import java.util.Locale
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor() : ViewModel() {
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines

    init {
        _medicines.value = ArrayList() // Initialiser avec une liste vide
    }

    fun addRandomMedicine(aisles: List<Aisle>) {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.add(
            Medicine(
                "Medicine " + (currentMedicines.size + 1),
                Random().nextInt(100),
                aisles[Random().nextInt(aisles.size)].name,
                emptyList()
            )
        )
        _medicines.value = currentMedicines
    }

    fun filterByName(name: String) {
        val currentMedicines: List<Medicine> = medicines.value
        val filteredMedicines: MutableList<Medicine> = ArrayList()
        for (medicine in currentMedicines) {
            if (medicine.name.lowercase(Locale.getDefault())
                    .contains(name.lowercase(Locale.getDefault()))
            ) {
                filteredMedicines.add(medicine)
            }
        }
        _medicines.value = filteredMedicines
    }

    fun sortByNone() {
        _medicines.value = medicines.value.toMutableList() // Pas de tri
    }

    fun sortByName() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparing(Medicine::name))
        _medicines.value = currentMedicines
    }

    fun sortByStock() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparingInt(Medicine::stock))
        _medicines.value = currentMedicines
    }

    fun incrementStock(medicineName: String) {
        _medicines.update { list ->
            list.map { medicine ->
                if (medicine.name == medicineName) {
                    medicine.copy(
                        stock = medicine.stock + 1,
                        histories = medicine.histories + History(
                            medicineName = medicine.name,
                            userId = "efeza56f1e65f",
                            date = Date().toString(),
                            details = "Stock incremented"
                        )
                    )
                } else medicine
            }
        }
    }

    fun decrementStock(medicineName: String) {
        _medicines.update { list ->
            list.map { medicine ->
                if (medicine.name == medicineName && medicine.stock > 0) {
                    medicine.copy(
                        stock = medicine.stock - 1,
                        histories = medicine.histories + History(
                            medicineName = medicine.name,
                            userId = "efeza56f1e65f",
                            date = Date().toString(),
                            details = "Stock decremented"
                        )
                    )
                } else medicine
            }
        }
    }

}