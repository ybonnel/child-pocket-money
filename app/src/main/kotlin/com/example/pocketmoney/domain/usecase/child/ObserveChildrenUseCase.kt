package com.example.pocketmoney.domain.usecase.child

import com.example.pocketmoney.domain.model.Child
import com.example.pocketmoney.domain.repository.ChildRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChildrenUseCase @Inject constructor(
    private val repository: ChildRepository
) {
    operator fun invoke(): Flow<List<Child>> = repository.observeAll()
}
