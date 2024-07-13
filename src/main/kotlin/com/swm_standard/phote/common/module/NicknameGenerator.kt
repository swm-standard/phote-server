package com.swm_standard.phote.common.module


class NicknameGenerator {
    fun randomNickname(): String {
        val adjectives = listOf(
            "행복한", "슬픈", "게으른", "슬기로운", "수줍은",
            "아련한", "배고픈", "배부른", "부자", "재벌",
            "웃고있는", "깨발랄한", "신나는", "멋있는", "매력적인",
            "친절한", "낭만적인", "용감한", "똑똑한", "귀여운", "화려한",
            "지적인", "긍정적인", "창의적인", "진지한", "엉뚱한", "모험적인",
            "유머러스한", "설레는", "멋진", "훌륭한", "우아한",
            "발랄한", "도전적인", "몽환적인", "맑은", "유쾌한"
        )

        val animals = listOf(
            "강아지", "고양이", "토끼", "사자", "호랑이",
            "곰", "코끼리", "원숭이", "기린", "얼룩말",
            "뱀", "거북이", "다람쥐", "해마", "낙타",
            "침팬지", "앵무새", "펭귄", "해달", "라마",
            "팬더", "오랑우탄", "캥거루", "하마", "수달",
            "바다거북", "불가사리", "갈매기", "라이거", "악어",
            "북극곰", "기니피그", "햄스터", "고슴도치", "오소리",
            "두더지", "붉은여우", "젖소", "카멜레온", "타조"
        )

        val shuffledAdjectives = adjectives.shuffled()
        val adj = shuffledAdjectives[0]

        val shuffledAnimals = animals.shuffled()
        val animal = shuffledAnimals[0]

        val numeric: String = (1..100000).random().toString()
        return "$adj $animal $numeric"
    }
}