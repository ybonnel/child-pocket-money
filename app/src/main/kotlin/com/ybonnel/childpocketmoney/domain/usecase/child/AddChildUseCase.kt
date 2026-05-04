package com.ybonnel.childpocketmoney.domain.usecase.child

import com.ybonnel.childpocketmoney.domain.model.Child
import com.ybonnel.childpocketmoney.domain.repository.ChildRepository
import javax.inject.Inject

class AddChildUseCase @Inject constructor(
    private val repository: ChildRepository
) {
    suspend operator fun invoke(child: Child): Long = repository.insert(child)
}
