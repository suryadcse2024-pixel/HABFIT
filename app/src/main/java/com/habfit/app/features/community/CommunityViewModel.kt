package com.habfit.app.features.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habfit.app.domain.model.ContentPost
import com.habfit.app.domain.model.CreatorProfile
import com.habfit.app.domain.repository.HabfitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: HabfitRepository
) : ViewModel() {

    val posts: StateFlow<List<ContentPost>> = repository.getAllPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creators: StateFlow<List<CreatorProfile>> = repository.getAllCreators()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLike(post: ContentPost) {
        viewModelScope.launch {
            repository.toggleLike(post)
        }
    }

    fun toggleFollow(creator: CreatorProfile) {
        viewModelScope.launch {
            repository.toggleFollowCreator(creator)
        }
    }

    fun createPost(title: String, body: String, tag: String) {
        if (body.isBlank()) return
        viewModelScope.launch {
            repository.createPost(title, body, tag)
        }
    }
}
