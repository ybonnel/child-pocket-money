package com.ybonnel.childpocketmoney.domain.usecase.child

import com.ybonnel.childpocketmoney.domain.repository.ChildRepository
import javax.inject.Inject

class DeleteChildUseCase @Inject constructor(
    private val repository: ChildRepository
) {
    suspend operator fun invoke(id: Long) = repository.archive(id)
}
