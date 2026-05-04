package com.ybonnel.childpocketmoney.domain.usecase.child

import com.ybonnel.childpocketmoney.domain.model.Child
import com.ybonnel.childpocketmoney.domain.repository.ChildRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveChildUseCase @Inject constructor(
    private val repository: ChildRepository
) {
    operator fun invoke(id: Long): Flow<Child?> = repository.observeById(id)
}
