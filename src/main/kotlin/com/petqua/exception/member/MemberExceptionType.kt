package com.petqua.exception.member

import com.petqua.common.exception.BaseExceptionType
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.LOOP_DETECTED
import org.springframework.http.HttpStatus.NOT_FOUND

enum class MemberExceptionType(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val errorMessage: String,
) : BaseExceptionType {

    NOT_FOUND_MEMBER(NOT_FOUND, "M01", "존재하지 않는 회원입니다."),
    HAS_SIGNED_UP_MEMBER(BAD_REQUEST, "M03", "이미 가입된 회원입니다."),

    INVALID_MEMBER_FISH_TANK_NAME(BAD_REQUEST, "M10", "유효하지 않은 수조 이름입니다."),
    INVALID_MEMBER_FISH_LIFE_YEAR(BAD_REQUEST, "M11", "유효하지 않은 물생활 경력입니다."),
    INVALID_MEMBER_PET_FISH_COUNT(BAD_REQUEST, "M12", "유효하지 않은 반려어 수입니다."),
    INVALID_MEMBER_FISH_TANK_SIZE(BAD_REQUEST, "M13", "유효하지 않은 수조 크기입니다."),
    INVALID_MEMBER_FISH_SEX(BAD_REQUEST, "M14", "유효하지 않은 반려어 성별입니다."),
    INVALID_MEMBER_PET_FISH(BAD_REQUEST, "M15", "유효하지 않은 반려어입니다."),
    INVALID_MEMBER_NICKNAME(BAD_REQUEST, "M16", "유효하지 않은 회원 닉네임입니다."),

    CONTAINING_BANNED_WORD_NAME(BAD_REQUEST, "M20", "이름에 금지 단어를 포함할 수 없습니다."),
    ALREADY_EXIST_NICKNAME(BAD_REQUEST, "M21", "이미 사용 중인 닉네임입니다."),

    INVALID_MEMBER_STATE(INTERNAL_SERVER_ERROR, "M90", "유효하지 않은 회원 상태입니다."),
    FAILED_NICKNAME_GENERATION(LOOP_DETECTED, "M91", "서버에서 회원 닉네임을 생성하지 못했습니다.")
    ;

    override fun httpStatus(): HttpStatus {
        return httpStatus
    }

    override fun code(): String {
        return code
    }

    override fun errorMessage(): String {
        return errorMessage
    }
}

