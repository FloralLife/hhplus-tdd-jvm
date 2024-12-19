package io.hhplus.tdd.point

import io.hhplus.tdd.TestUtils.randomId
import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PointServiceTest {
    @Mock
    private lateinit var pointHistoryTable: PointHistoryTable

    @Mock
    private lateinit var userPointTable: UserPointTable

    @InjectMocks
    private lateinit var pointService: PointService

    // UserPoint의 조회 동작 테스트 (비즈니스 로직이 없기 떄문에 테스트의 의미는 없음)
    @Test
    fun getUserPoint() {
        val userId = randomId()

        `when`(userPointTable.selectById(userId)).thenReturn(
            UserPoint(
                userId,
                0,
                System.currentTimeMillis(),
            ),
        )

        val result = pointService.getUserPoint(userId)

        assertEquals(userId, result.id)
        assertEquals(0, result.point)
    }

    // PointHistory 조회 동작 테스트 (비즈니스 로직이 없기 때문에 테스트의 의미는 없음)
    @Test
    fun getPointHistory() {
        val userId = randomId()
        `when`(pointHistoryTable.selectAllByUserId(userId)).thenReturn(emptyList())

        val result = pointService.getPointHistory(userId)

        assertEquals(0, result.size)
    }

    // charge point 시에 충전할 수 있는 최대 포인트 양보다 많으면 에러 반환 테스트
    @Test
    fun chargeUserPointLargerThanMax() {
        val userId = randomId()
        val chargePoint = Long.MAX_VALUE

        `when`(userPointTable.selectById(userId))
            .thenReturn(UserPoint(userId, 0, System.currentTimeMillis()))

        assertThrows(IllegalArgumentException::class.java) { pointService.chargePoint(userId, chargePoint) }
    }

    // charge point 동작 테스트
    @Test
    fun chargePoint() {
        val userId = randomId()
        val chargePoint = 1_000_000L

        `when`(userPointTable.selectById(userId))
            .thenReturn(UserPoint(userId, 0, System.currentTimeMillis()))
        `when`(userPointTable.insertOrUpdate(userId, chargePoint))
            .thenReturn(
                UserPoint(
                    userId,
                    chargePoint,
                    System.currentTimeMillis(),
                ),
            )

        val result = pointService.chargePoint(userId, chargePoint)

        assertEquals(userId, result.id)
        assertEquals(chargePoint, result.point)
//        verify(pointHistoryTable).insert(eq(userId), eq(chargePoint), eq(TransactionType.CHARGE), any())
    }

    // use point 할 때 잔액보다 큰 포인트를 사용하면 에러 반환 테스트
    @Test
    fun usePointLargerThanBalanceThenThrowException() {
        val userId = randomId()
        val usePoint = Long.MAX_VALUE

        `when`(userPointTable.selectById(userId))
            .thenReturn(UserPoint(userId, 0, System.currentTimeMillis()))

        assertThrows(IllegalArgumentException::class.java) { pointService.chargePoint(userId, usePoint) }
    }

    // use point 동작 테스트
    @Test
    fun usePoint() {
        val userId = randomId()
        val balance = 1_000_000L
        val usePoint = 500_000L

        `when`(userPointTable.selectById(userId))
            .thenReturn(UserPoint(userId, balance, System.currentTimeMillis()))
        `when`(userPointTable.insertOrUpdate(userId, balance - usePoint))
            .thenReturn(UserPoint(usePoint, balance - usePoint, System.currentTimeMillis()))

        val result = pointService.usePoint(userId, usePoint)

        assertEquals(balance - usePoint, result.point)
//        verify(pointHistoryTable).insert(eq(userId), eq(usePoint), eq(TransactionType.USE)!!, anyLong())
    }
}
