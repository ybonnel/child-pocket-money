package com.example.pocketmoney.domain.usecase.child

import com.example.pocketmoney.domain.model.Child
import com.example.pocketmoney.domain.repository.ChildRepository
import javax.inject.Inject

class AddChildUseCase @Inject constructor(
    private val repository: ChildRepository
) {
    suspend operator fun invoke(child: Child): Long = repository.insert(child)
}
