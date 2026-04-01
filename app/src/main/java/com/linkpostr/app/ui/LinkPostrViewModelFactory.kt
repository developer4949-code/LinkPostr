package com.linkpostr.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.linkpostr.app.data.LinkPostrRepository
import com.linkpostr.app.data.ThemePreferences

class LinkPostrViewModelFactory(
    private val repository: LinkPostrRepository,
    private val themePreferences: ThemePreferences,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LinkPostrViewModel::class.java)) {
            return LinkPostrViewModel(repository, themePreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
