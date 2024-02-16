package com.petqua.domain.payment.tosspayment

enum class TossPaymentStatus(
    private val description: String,
) {

    READY("결제 준비 중"),
    IN_PROGRESS("결제 진행 중"),
    WAITING_FOR_DEPOSIT("입금 대기 중"),
    DONE("결제 완료"),
    CANCELED("결제 취소"),
    PARTIAL_CANCELED("부분 취소"),
    ABORTED("결제 실패"),
    EXPIRED("결제 만료");
}
