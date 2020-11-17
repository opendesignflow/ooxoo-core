package org.odfi.ooxoo.gradle.plugin

import com.idyria.osi.ooxoo.model.ModelProducer
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters


interface XModelProducerParameters : WorkParameters {

    fun getModelFile(): RegularFileProperty?
    fun getBuildOutput(): RegularFileProperty?
}