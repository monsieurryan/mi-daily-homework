package com.example.gameinfoquerydemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameinfoquerydemo.model.GameInfo
import com.example.gameinfoquerydemo.repository.GameException
import com.example.gameinfoquerydemo.repository.GameRepository
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val repository = GameRepository()
    private val _gameInfo = MutableLiveData<GameInfo>()
    val gameInfo: LiveData<GameInfo> = _gameInfo
    
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private fun resetState() {
        _gameInfo.value = null
        _error.value = null
        _isLoading.value = false
    }
    
    fun fetchGameInfo(gameId: String) {
        viewModelScope.launch {
            try {
                resetState()
                _isLoading.value = true
                _gameInfo.value = repository.getGameInfo(gameId)
            } catch (e: GameException) {
                _error.value = e.message
                _gameInfo.value = null
            } catch (e: Exception) {
                _error.value = "发生未知错误，请稍后重试"
                _gameInfo.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
} 