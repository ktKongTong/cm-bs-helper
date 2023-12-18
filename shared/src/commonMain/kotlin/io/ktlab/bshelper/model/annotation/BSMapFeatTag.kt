package io.ktlab.bshelper.model.annotation

import io.ktlab.bshelper.model.enums.MapFeatureTag

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class BSMapFeatTag(val mapFeatureTag: MapFeatureTag)
