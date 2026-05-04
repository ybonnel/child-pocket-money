package com.example.pocketmoney.domain.usecase.child

import com.example.pocketmoney.domain.repository.ChildRepository
import javax.inject.Inject

class DeleteChildUseCase @Inject constructor(
    private val repository: ChildRepository
) {
    suspend operator fun invoke(id: Long) = repository.archive(id)
}
