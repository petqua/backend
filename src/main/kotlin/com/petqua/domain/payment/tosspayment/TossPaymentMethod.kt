package com.petqua.domain.payment.tosspayment

enum class TossPaymentMethod(
    private val description: String,
) {

    CREDIT_CARD("카드"),
    VIRTUAL_ACCOUNT("가상 계좌"),
    SIMPLE_PAYMENT("간편 결제"),
    MOBILE_PHONE("휴대폰"),
    ACCOUNT_TRANSFER("계좌 이체"),
    CULTURE_VOUCHER("문화 상품권"),
    BOOK_CULTURE_VOUCHER("도서 문화 상품권"),
    GAME_CULTURE_VOUCHER("게임 문화 상품권"),
}
