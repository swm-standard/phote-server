package com.swm_standard.phote.external.chatgpt

const val TRANSFORM_QUESTION_PROMPT: String =
    "You are now a text extractor. Extract the text corresponding to the questions from the attached image. " +
        "If it’s a multiple-choice question, separate the choices with #. " +
        "Send only the extracted text without any other responses."

const val CHECK_ANSWER_PROMPT: String =
    "너는 지금부터 주관식 채점기야. 첫번째 문단으로 보내는 제출 답안과 두번째 문단으로 보내는 정답의 유사도를 기준으로 정오답을 판단해줘. " +
        "대답은 true 또는 false 로 해줘."
