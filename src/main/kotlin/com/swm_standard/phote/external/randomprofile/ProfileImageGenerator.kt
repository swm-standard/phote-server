package com.swm_standard.phote.external.randomprofile

class ProfileImageGenerator {

    val eyes: List<String> = listOf(
    "variant01", "variant02", "variant03", "variant04","variant05", "variant06", "variant07", "variant08", "variant09",
        "variant10", "variant11", "variant12"
    )

    val mouth: List<String> = listOf(
        "happy01", "happy02", "happy03", "happy04", "happy05", "happy06", "happy07", "happy08", "happy09",
        "happy10", "happy11", "happy12", "happy13"
    )

    val seed: List<String> = listOf("Felix", "Aneka")

    val color: List<String> = listOf("b6e3f4", "c0aede", "d1d4f9", "ffd5dc", "ffdfbf", "cb9e6e", "eac393", "ffdbac", "f5cfa0")

    fun imageGenerator(): String {
        return "https://api.dicebear.com/9.x/pixel-art-neutral/svg?seed=${seed.random()}&eyes=${eyes.random()}&mouth=${mouth.random()}&backgroundColor=${color.random()}"
    }
}