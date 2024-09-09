package com.swm_standard.phote.common.module

class Qualifier {
    @MustBeDocumented
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
    annotation class Generated
}
