package com.idyria.osi.ooxoo.db.tests

import org.h2.tools.Server
import org.hibernate.cfg.AvailableSettings

import com.idyria.osi.ooxoo.hibernate.OOXOOHibernate
import com.idyria.osi.ooxoo.db.tests.jpa.TryJPAConfig
import scala.util.Random
import com.idyria.osi.ooxoo.db.tests.jpa.TryJPAConfigUser

object TryJPAApp extends App {

  println("Test TryJPAApp")

  com.idyria.osi.ooxoo.db.tests.jpa.registerModels

  //-- Create H2 Mem db config
  val server = Server.createWebServer("-webPort", "50100").start();
  val config = Map(

    // AvailableSettings.CONNECTION_PROVIDER -> "org.hibernate.jpa.HibernatePersistenceProvider",
    AvailableSettings.HBM2DDL_AUTO -> "update",

    "hibernate.show_sql" -> "true",
    AvailableSettings.DRIVER -> classOf[org.h2.Driver].getCanonicalName,
    AvailableSettings.URL -> "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE",
    AvailableSettings.USER -> "sa",
    AvailableSettings.PASS -> "")

  //-- Get Entity manager
  val manager = OOXOOHibernate.createEntityManager(config)

  //-- Persist config
  val entityManager = manager.get

  //-- Generate random numbers
  entityManager.getTransaction.begin
  (0 until 1000).foreach {
    i =>
      var jpaConfig = new TryJPAConfig
      jpaConfig.value = Random.nextGaussian() * 1000

      (0 until Random.nextInt() % 5).foreach {
        ui =>
          val user = new TryJPAConfigUser
          user.name = s"John$ui"
          entityManager.persist(user)
          jpaConfig.users.add(user)

      }

      //jpaConfig.user = user

      //   jpaConfig.users.add(user)

      entityManager.persist(jpaConfig)
  }
  entityManager.getTransaction.commit()

  // Search for Values < 500
  //----------
  val q = entityManager.createQuery("SELECT c FROM com.idyria.osi.ooxoo.db.tests.jpa.TryJPAConfig AS c WHERE c.value<=500")

  println("Results count: " + q.getResultList.size())

  val first = q.getResultList.get(q.getFirstResult).asInstanceOf[TryJPAConfig]
  println("First: " + first.value + " -> " + first.users.size)
  
  //-- First With a user
  val q2 = entityManager.createQuery("SELECT c FROM com.idyria.osi.ooxoo.db.tests.jpa.TryJPAConfig AS c WHERE c.users.size>0")
  println("First config with an user: "+q2.getResultList.get(0).asInstanceOf[TryJPAConfig].users.size())
  // println("Value set: " + jpaConfig.valueOption)

}