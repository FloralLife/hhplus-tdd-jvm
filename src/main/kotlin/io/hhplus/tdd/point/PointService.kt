package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.PointConstants.MAX_POINT
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointHistoryTable: PointHistoryTable,
    private val userPointTable: UserPointTable,
) {
    fun getUserPoint(id: Long): UserPoint = userPointTable.selectById(id)

    fun getPointHistory(id: Long): List<PointHistory> = pointHistoryTable.selectAllByUserId(id)

    fun chargePoint(
        id: Long,
        amount: Long,
    ): UserPoint {
        val userPoint = userPointTable.selectById(id)
        require(amount <= MAX_POINT - userPoint.point) { "최대 ${MAX_POINT} 포인트까지 충전할 수 있습니다." }

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis())

        return userPointTable.insertOrUpdate(id, userPoint.point + amount)
    }

    fun usePoint(
        id: Long,
        amount: Long,
    ): UserPoint {
        val userPoint = userPointTable.selectById(id)
        require(userPoint.point >= amount) { "잔액보다 큰 포인트는 사용할 수 없습니다." }

        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis())
        return userPointTable.insertOrUpdate(id, userPoint.point - amount)
    }
}
