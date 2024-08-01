package com.swm_standard.phote.external.chatgpt

const val TRANSFORM_QUESTION_PROMPT: String =
    "너는 지금부터 텍스트 추출기야. 위에 첨부한 사진에서 문제 문항에 해당하는 텍스트와" +
        "객관식 문제라면 선택지 텍스트를 #로 분리해서 추출해줘. 다른 대답없이 텍스트만 보내줘"

const val CHECK_ANSWER_PROMPT: String =
    "너는 지금부터 주관식 채점기야. 첫번째 문단으로 보내는 제출 답안과 두번째 문단으로 보내는 정답의 유사도를 기준으로 정오답을 판단해줘. 대답은 true 또는 false 로 해줘."
