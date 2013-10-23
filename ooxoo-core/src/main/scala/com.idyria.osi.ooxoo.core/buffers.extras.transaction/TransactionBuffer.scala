package com.idyria.osi.ooxoo.core.buffers.extras.transaction

import com.idyria.osi.ooxoo.core.buffers.structural._

import com.idyria.osi.tea.logging._

import scala.language.implicitConversions

/**
 * This Buffer can be used to block propagations base on a transaction context,
 * so that values get propagated
 *
 */
class TransactionBuffer extends BaseBufferTrait with TLogSource {

  /**
   * The DataUnit that is currently on hold before transaction commit
   */
  var pushDataUnit: DataUnit = null

  var pullDataUnit: DataUnit = null

  var transactionAction: PartialFunction[Transaction, Unit] = {

    case Transaction.Commit(transaction) ⇒

      //    println("Transaction Commit Called")

      if (pushDataUnit != null)
        TransactionBuffer.this.doPushRight(pushDataUnit)

      this.pushDataUnit = null
      this.pullDataUnit = null

    case Transaction.Discard(transaction) ⇒

      //println("Discarding")

      this.pushDataUnit = null
      this.pullDataUnit = null

    case Transaction.Cancel(transaction) ⇒

      this.pushDataUnit = null
      this.pullDataUnit = null

    case _ ⇒

      this.pushDataUnit = null
      this.pullDataUnit = null

  }

  // Get
  //----------

  /**
   * Returned Transaction Cached read value, or propagate pull
   */
  override def pull(du: DataUnit): DataUnit = {

    // Return cached value if available, otherwise delegate
    if (pullDataUnit != null) {
      logFine("Returning cached value")
      this.pullDataUnit
    } else {

      // Register to Transaction if not already
      Transaction()(transactionAction)

      // Pull, cache and return
      this.pullDataUnit = super.pull(du)
      this.pullDataUnit
    }
  }

  // Put
  //------------

  /**
   * Holds the dataunit for propagation until the transaction is commited
   *
   *
   * If The transaction is stopped, don't retain values
   *
   */
  override def pushRight(du: DataUnit) = {

    Transaction() match {

      // Let through
      case Transaction.Stopped(transaction) ⇒
      
        this.pushDataUnit = null
        this.pullDataUnit = null
        super.pushRight(du)

      // Retain
      case _ ⇒

        this.pushDataUnit = du
        this.pullDataUnit = du
        Transaction()(transactionAction)

    }

    //println("In Propagate right")

  }

  protected def doPushRight(du: DataUnit) = super.pushRight(du)

}

/**
 * The TransactionBuffer Singleton manages transaction state
 *
 * Running Transaction Management:
 *
 * - Every calling thread has a TransactionContext, in which buffers store their DataUnits to propagate
 *
 */
object TransactionBuffer {

}

/**
 * A Transaction is an object holding for a transaction, the actions that should happen when commiting,
 * and also status/error informations in case of error
 */
class Transaction {

  /// State of the transaction
  var state = Transaction.State.Stopped

  /// The actions to be executed by the transaction
  var actions = List[PartialFunction[Transaction, Unit]]()

  /// The object that initiated this transaction, if provided at creation
  var initiator: AnyRef = null

  // Setup
  //------------

  /**
   * The passed action closure will be executed on commit, with a reference to the current transaction passed
   */
  def apply(action: PartialFunction[Transaction, Unit]) = {

    // println("Recording transaction action: "+action.hashCode)
    if (!actions.contains(action))
      actions = action :: actions
  }

  

  // Lifecycle
  //----------------

  /**
   * Change state to pending
   */
  def begin() = {
     this.state = Transaction.State.Pending
  }
  
  /**
   * Commits all the registered action closures
   */
  def commit() = {

    // Change State
    //-------------
    this.state = Transaction.State.Commit

    // Execute The actions
    //  - Catch Errors and call roll back if needed
    //----------------------
    actions.foreach(_(this))

  }

  /**
   *
   * Cancel all the actions, and discard from Transaction/Tread mapping
   *
   * @return This Transaction if needs to be saved
   */
  def cancel(): Transaction = {

    // Change State
    //-------------
    this.state = Transaction.State.Cancel

    // Cancel
    //----------------------
    try {
      actions.foreach(_(this))

    } // Discard
    //--------------
    finally {

      Transaction.discard(this)

    }
    return this

  }

  /**
   * Discard Transaction
   * Discard is called at the end of a transaction to cleanup
   */
  def discard() = {

    //println("Discarding transaction with: "+actions.size+" actions")

    // Change State
    //-------------
    this.state = Transaction.State.Discard

    // Discard
    //----------------------
    actions.foreach(_(this))

  }
}

object Transaction extends TLogSource {

  /**
   * Transaction State
   */
  object State extends Enumeration {
    type State = Value
    val Stopped, Pending, Commit, Cancel, Rollback, Discard = Value
  }

  object Stopped {

    def unapply(transaction: Transaction): Option[Transaction] = {
      if (transaction.state == Transaction.State.Stopped)
        return Option(transaction)
      else
        return None
    }

  }

  object Commit {

    def unapply(transaction: Transaction): Option[Transaction] = {
      if (transaction.state == Transaction.State.Commit)
        return Option(transaction)
      else
        return None
    }
  }

  object Cancel {

    def unapply(transaction: Transaction): Option[Transaction] = {
      if (transaction.state == Transaction.State.Cancel)
        return Option(transaction)
      else
        return None
    }
  }

  object Discard {

    def unapply(transaction: Transaction): Option[Transaction] = {
      if (transaction.state == Transaction.State.Discard)
        return Option(transaction)
      else
        return None
    }
  }

  var currentTransactions = Map[Thread, Transaction]()

  implicit val defaultInitiator: AnyRef = null

  /**
   *
   * @return The Transaction of the calling Buffer for the current Thread
   */
  def apply(implicit initiator: AnyRef = null): Transaction = {

    // Take current Thread
    //-------------
    var thread = Thread.currentThread

    // Create or return Transaction
    //---------------
    currentTransactions.get(thread) match {

      case Some(transaction) ⇒ transaction
      case None ⇒

        logFine("-- Creating transaction for Thread --")

        var transaction = new Transaction
        transaction.initiator = initiator
        currentTransactions += (thread -> transaction)
        return transaction
    }

  }
  /*def apply : Transaction =  apply(null)

    def apply( action : PartialFunction[Transaction,Unit]) = {

        this(null)(action)
    }*/

  // Execution utils
  //------------------

  /**
   * Executes the closure on the transaction, issuing a commit at the end of join group
   */
  def join(cl: ⇒ Any): Unit = {

    //-- Make the transaction pending
    Transaction().begin
    
    //-- Execute
    cl

    //-- Commit
    Transaction().commit

  }
  
  /**
   * Remove current Transaction from transaction mapping
   */
  def discard(): Unit = discard(Transaction())

  /**
   * Remove a transaction from Thread Mapping
   */
  def discard(transaction: Transaction): Unit = {

    //println("Searching for Transaction to discard for current Thread")

    currentTransactions.find { case (th, tr) ⇒ tr == transaction } match {
      case Some((th, tr)) ⇒

        // println("Found Transaction to discard for current Thread")
        tr.discard
        currentTransactions -= th
      case None ⇒
      // FIXME : Return an error trying to discard a non registered transaction ?
    }

  }

  /**
   * Discard All the transactions, and cancels not closed one
   */
  def discardAll = {

    currentTransactions.foreach { case (th, tr) ⇒ discard(tr) }

  }

}
