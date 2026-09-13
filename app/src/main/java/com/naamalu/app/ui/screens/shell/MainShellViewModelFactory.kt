package com.naamalu.app.ui.screens.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naamalu.app.domain.repository.RecitationRepository

class MainShellViewModelFactory(
    private val recitationRepository: RecitationRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainShellViewModel::class.java)) {
            return MainShellViewModel(recitationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
