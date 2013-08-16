package com.idyira.osi.ooxoo.maven


import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations._


@Mojo( name = "generate-sources")
class GenerateSourcesMojo extends AbstractMojo {

 
    @throws(classOf[MojoExecutionException])
    override def execute()  {
        getLog().info( "Hello, world." );
    }
}
