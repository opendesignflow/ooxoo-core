package org.odfi.ooxoo.gradle.plugin

import org.gradle.api.file.RegularFileProperty
import org.gradle.workers.WorkParameters


interface XModelProducerParameters : WorkParameters {

    fun getModelFile(): RegularFileProperty?
    fun getBuildOutput(): RegularFileProperty?
}