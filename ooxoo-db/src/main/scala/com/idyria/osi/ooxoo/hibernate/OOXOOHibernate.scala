/*-
 * #%L
 * OOXOO Db Project
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.idyria.osi.ooxoo.hibernate

import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.boot.MetadataSources
import javax.persistence.EntityManager
import scala.jdk.CollectionConverters._

class RegisteredHibernate(val configMap: Map[String, String])  {

  //-- Prepare Registry Builder
  val registryBuilder = new StandardServiceRegistryBuilder()
  registryBuilder.applySettings(configMap.asJava)

  val configuration = new Configuration(registryBuilder.getBootstrapServiceRegistry)

  //-- Create a Registry and metadatasource
  val registry = registryBuilder.build()

  val metadataSource = new MetadataSources(registry)
  //metadataSource.getMetadataBuilder.applyBasicType(new IntegerBufferUserType, "integerbuffer")

  //--
  var entityManager: Option[EntityManager] = None

  def get = synchronized {
    entityManager match {
      case Some(e) => e
      case None =>
        val sessionFactory = metadataSource.buildMetadata().buildSessionFactory();


       entityManager = Some(sessionFactory.createEntityManager())
        entityManager.get
    }
  }
  
  def invalidateManager = this.entityManager = None
  
  def registerEntity(cl:Class[_]) = {
    invalidateManager
    this.metadataSource.addAnnotatedClass(cl)
  }
  
  def registerPackage(p:Package) = {
    invalidateManager
    this.metadataSource.addPackage(p)
  }
}

object OOXOOHibernate {

  // Created Hibernates
  //----------------
  var hibernates = List[RegisteredHibernate]()

  var classes = List[Class[_]]()
  var packages = List[Package]()
  
  // Apply Config
  //----------------
  def createEntityManager(configMap: Map[String, String]) = {

   val h = new RegisteredHibernate(configMap)
   packages.foreach(h.registerPackage)
   classes.foreach(h.registerEntity)
   hibernates = hibernates :+ h
   h

  }

  def registerModel(n: String) = {
    println("Registering Model: " + n)
    val cl = Thread.currentThread().getContextClassLoader.loadClass(n)
    hibernates.foreach {
      h => 
        h.registerEntity(cl)
    }
    
    classes = classes :+ cl
  }
  def registerPackage(p:Package) = {
    println("Registering Package: " + p)
    
    hibernates.foreach {
      h => 
        h.registerPackage(p)
    }
    
    packages = packages :+ p
  }
  

}
