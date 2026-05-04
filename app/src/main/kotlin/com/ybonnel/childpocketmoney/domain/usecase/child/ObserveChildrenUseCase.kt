package com.ybonnel.childpocketmoney.domain.usecase.child

import com.ybonnel.childpocketmoney.domain.model.Child
import com.ybonnel.childpocketmoney.domain.repository.ChildRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChildrenUseCase @Inject constructor(
    private val repository: ChildRepository
) {
    operator fun invoke(): Flow<List<Child>> = repository.observeAll()
}
