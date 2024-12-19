package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointHistoryTable: PointHistoryTable,
    private val userPointTable: UserPointTable,
) {
    fun getUserPoint(id: Long): UserPoint = UserPoint(0, 0, 0)

    fun getPointHistory(id: Long): List<PointHistory> = emptyList()

    fun chargePoint(
        id: Long,
        amount: Long,
    ): UserPoint = UserPoint(0, 0, 0)

    fun usePoint(
        id: Long,
        amount: Long,
    ): UserPoint = UserPoint(0, 0, 0)
}
