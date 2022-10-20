package org.odfi.ooxoo.gradle.plugin

import org.gradle.api.internal.provider.DefaultProperty
import org.gradle.api.provider.Property

abstract class  OOXOOExtension {

    abstract val javax : Property<Boolean>

    init {
        javax.convention(false)
    }
}