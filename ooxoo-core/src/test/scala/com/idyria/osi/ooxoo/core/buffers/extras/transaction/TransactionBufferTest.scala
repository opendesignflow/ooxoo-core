package com.idyria.osi.ooxoo.core.buffers.extras.transaction

import org.scalatest.BeforeAndAfterEach
import com.idyria.osi.ooxoo.core.buffers.structural.BaseBufferTrait
import org.scalatest.GivenWhenThen
import com.idyria.osi.tea.listeners.ListeningSupport
import org.scalatest.FeatureSpec
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo.core.buffers.datatypes.LongBuffer
import org.scalatest.Matchers
import com.idyria.osi.tea.logging.TLog

class TransactionBufferTest extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterEach {

  override def beforeEach = {
    Transaction.discardAll
  }

  /**
   * Buffer designed to catch transaction propagations and report what it saw
   */
  //class ReceiveTransactionBuffer extends BaseBufferTrait with ListeningSupport {

  //}

  feature("Transaction Begin") {

    scenario("Two Begins in one thread give one transaction") {

      var transaction1 = Transaction()
      var transaction2 = Transaction()

      assert(transaction1.hashCode == transaction2.hashCode)

    }

    scenario("Two Begins in one thread give Two threads give two transactions") {

      Given("A Transaction from one thread")

      var transaction1: Transaction = null
      var th1 = new Thread() {
        override def run() = {
          transaction1 = Transaction()
        }
      }

      And("A transaction fron another thread")

      var transaction2: Transaction = null
      var th2 = new Thread() {
        override def run() = {
          transaction2 = Transaction()
        }
      }

      Then("The two transactions are different")
      th1.start();

/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2008 - 2014 OSI / Computer Architecture Group @ Uni. Heidelberg
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
      th2.start();
      List(th1, th2).foreach(_.join)

      assert(transaction1.hashCode != transaction2.hashCode)

    }

    scenario(" Transaction Data Unit Holding") {

      Given("A Transactional Buffer chain")
      //--------------------

      var baseBuffer = new LongBuffer()
      baseBuffer - new TransactionBuffer()

      Transaction.join {

        When("Setting Value on base long buffer")
        //------------
        baseBuffer.set(42)

        Then(" there is transaction in TransactionBuffer, with one registered partitial function")
        //---------------
        var transaction = Transaction()
        assert(transaction != null, "Transaction for current Thread must not be null")

        transaction.actions.size should equal(1)

        And("If resetting value on same buffer, only one action is still present")
        baseBuffer.set(42)
        transaction.actions.size should equal(1)

      }
    }

  }

  feature("Transaction Commit") {

    scenario("Single Value set commit ") {

      Given("A Transactional Buffer chain")
      //--------------------
      var resultDataUnit: DataUnit = null
      var baseBuffer = new LongBuffer()
      baseBuffer - new TransactionBuffer()
      baseBuffer - new BaseBufferTrait() {

        override def pushRight(du: DataUnit) = {
          resultDataUnit = du
        }

      }

      Transaction.join {

        When("Setting Value on base start buffer, ")
        //------------------------------
        baseBuffer.set(42)

        Then("the receiving buffer should't get any results")
        //-----------------------------
        assertResult(null)(resultDataUnit)

        When("commiting the transaction")
        //---------------------------------
        Transaction().commit

        Then("The holded data unit gets received by the buffer after transaction buffer")
        //---------------------
        assert(resultDataUnit != null)

      }

    }

    scenario("Multiple Buffer commit")(pending)

  }

  feature("Transaction Cancel") {

    scenario("Single Cancel") {

      Given("A Transactional Buffer chain")
      //--------------------
      var resultDataUnit: DataUnit = null
      var baseBuffer = new LongBuffer()
      baseBuffer - new TransactionBuffer()
      baseBuffer - new BaseBufferTrait() {

        override def pushRight(du: DataUnit) = {
          resultDataUnit = du
        }

      }

      Transaction().begin

      When("Setting Value on base start buffer, ")
      //------------------------------
      baseBuffer.set(42)

      And("Cancelling the transaction")
      //---------------------------------
      var transaction = Transaction()
      Transaction().cancel

      Then("No result should be received")
      //-----------------------
      assert(resultDataUnit == null)

      And("Transaction has been discarded")
      //----------------
      var transactionAfterCancel = Transaction()
      assert(transaction.hashCode != transactionAfterCancel.hashCode)
      assertResult(Transaction.State.Stopped)(transactionAfterCancel.state)

    }
  }

  feature("Transaction Rollback") {

    scenario("Single Rollback") {

    }

  }

  feature("Transaction Initiator") {

    scenario("No Initiator Provided") {

      Given("A Normal transaction")
      var tr = Transaction()

      Then("Initiator must be null")
      assert(tr.initiator == null)

    }

    scenario("Initiator Provided") {

      Given("A Normal transaction")
      var tr = Transaction(TransactionBufferTest.this)

      Then("Initiator must be null")

      assertResult(TransactionBufferTest.this.getClass.getName)(tr.initiator.getClass.getName)

    }

  }

  feature("Transaction Read Cache") {
 
    scenario("Simple Read Cache") {

      Given("A Transactional Buffer chain")
      //--------------------
      TLog.setLevel(classOf[TransactionBuffer], TLog.Level.FULL)
      var resultDataUnit: DataUnit = null
      var pullCount = 0
      var baseBuffer = new LongBuffer()
      baseBuffer - new TransactionBuffer()
      baseBuffer - new BaseBufferTrait() {

        pullCount = 0

        override def pull(du: DataUnit): DataUnit = {
          pullCount += 1
          du.value = "45"
          du
        }

      }
      
      And("The transaction gets started")
      Transaction.discardAll
      Transaction().begin

      Then("Pulling Twice, should trigger only one pull on the buffer behind the transactional buffer")
      baseBuffer.pull()
      baseBuffer.pull()

      assertResult(1)(pullCount)

    }

  }

}

