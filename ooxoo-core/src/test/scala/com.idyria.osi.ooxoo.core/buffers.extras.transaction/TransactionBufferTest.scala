package com.idyria.osi.ooxoo.core.buffers.extras.transaction



import org.scalatest._
import org.scalatest.matchers._


import com.idyria.osi.ooxoo.core.buffers.extras.transaction._
import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.ooxoo.core.buffers.datatypes._

import com.idyria.osi.tea.listeners._


class TransactionBufferTest extends FeatureSpec with GivenWhenThen  with ShouldMatchers {

    /**
        Buffer designed to catch transaction propagations and report what it saw
    */
    class ReceiveTransactionBuffer extends BaseBuffer  with ListeningSupport {




    }


    feature("Transaction Begin") {

         scenario("Two Begins in one thread give one transaction") {

            var transaction1 = Transaction()
            var transaction2 = Transaction()

            assert(transaction1.hashCode == transaction2.hashCode)

         }

         scenario("Two Begins in one thread give Two threads give two transactions") {

            Given ("A Transaction from one thread")

            var transaction1 : Transaction = null
            var th1 = new Thread() {
                override def run() = {
                    transaction1 = Transaction()
                }
            }

            And("A transaction fron another thread")

            var transaction2 : Transaction = null
            var th2 = new Thread() {
                override def run() = {
                    transaction2 = Transaction()
                }
            }


            Then("The two transactions are different")
            th1.start();
            th2.start();
            List(th1,th2).foreach(_.join)

            assert(transaction1.hashCode != transaction2.hashCode)

         }

        scenario(" Transaction Data Unit Holding") {

            Given("A Transactional Buffer chain")
            //--------------------

            var baseBuffer = new LongBuffer()
            baseBuffer - new TransactionBuffer()

            When("Setting Value on base long buffer")
            //------------
            baseBuffer.set(42)

            Then(" there is transaction in TransactionBuffer, with one registered partitial function")
            //---------------
            var transaction = Transaction()
            assert(transaction!=null,"Transaction for current Thread must not be null")

            transaction.actions.size should equal (1)

        }

    }

    feature("Transaction Commit") {


        scenario("Single Value set commit ") {

            Given("A Transactional Buffer chain")
            //--------------------
            var resultDataUnit : DataUnit = null
            var baseBuffer = new LongBuffer()
            baseBuffer - new TransactionBuffer()
            baseBuffer - new BaseBuffer() {

                override def pushRight(du:DataUnit) = {
                    resultDataUnit = du
                }

            }

            When("Setting Value on base start buffer, ")
            //------------------------------
            baseBuffer.set(42)

            Then("the receiving buffer should't get any results")
            //-----------------------------
            expectResult(null)(resultDataUnit)

            When("commiting the transaction")
            //---------------------------------
            Transaction().commit

            Then("The holded data unit gets received by the buffer after transaction buffer")
            //---------------------
            assert(resultDataUnit!=null)


        }

        scenario("Multiple Buffer commit") (pending)


    }

    feature("Transaction Cancel") {

        scenario("Single Cancel") {

            Given("A Transactional Buffer chain")
            //--------------------
            var resultDataUnit : DataUnit = null
            var baseBuffer = new LongBuffer()
            baseBuffer - new TransactionBuffer()
            baseBuffer - new BaseBuffer() {

                override def pushRight(du:DataUnit) = {
                    resultDataUnit = du
                }

            }

            When("Setting Value on base start buffer, ")
            //------------------------------
            baseBuffer.set(42)


            And("Cancelling the transaction")
            //---------------------------------
            var transaction = Transaction()
            Transaction().cancel

            Then("No result should be received")
            //-----------------------
            assert(resultDataUnit==null)

            And("Transaction has been discarded")
            //----------------
            var transactionAfterCancel = Transaction()
            assert(transaction.hashCode != transactionAfterCancel.hashCode)
            expect(Transaction.State.Pending)(transactionAfterCancel.state)


        }
    }


}

