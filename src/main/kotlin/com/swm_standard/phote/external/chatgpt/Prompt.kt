package com.swm_standard.phote.external.chatgpt

const val TRANSFORM_QUESTION_PROMPT: String =
    "Now you have a text extractor. Extract the text corresponding to your question from the attached image. " +
        "If it is a multiple choice question, separate each option with a question using '#'. " +
        "Exclude multiple choice numbers and any gap. " +
        "Send only the extracted text(question and multiple choice) " +
        "without any other response, gap and escape sequence."

const val CHECK_ANSWER_PROMPT: String =
    "From now on, you are a subjective grader. " +
        "Judge the incorrect answer based on the semantic similarity between the answer " +
        "submitted in the first paragraph and the correct answer sent in the second paragraph. Answer true or false."
