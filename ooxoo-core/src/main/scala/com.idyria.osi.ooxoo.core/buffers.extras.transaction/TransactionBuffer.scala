package com.idyria.osi.ooxoo.core.buffers.extras.transaction

import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.tea.logging._
import scala.language.implicitConversions
import scala.collection.mutable.Stack

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

    // Only Cache value if Transaction is enabled
    //--------------------------
    Transaction() match {

      // Stopped -> D nothing
      //-----------------------
      case Transaction.Stopped(transaction) ⇒

        this.pullDataUnit = null
        super.pull(du)

      // Cache, but no value already
      case _ if (pullDataUnit == null) ⇒

      	logFine("Pulling and caching value")
      
        // Pull, cache and return
        // Register to Transaction if not already
        Transaction()(transactionAction)
        this.pullDataUnit = super.pull(du)
        this.pullDataUnit

      // Return Cached value
      case _ =>

        logFine("Returning cached value")
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
   * Come back to previous state after because multiple commits per transaction can be issued
   */
  def commit() = {

    // Change State
    //-------------
    var oldState = this.state
    this.state = Transaction.State.Commit

    // Execute The actions
    //  - Catch Errors and call roll back if needed
    //----------------------
    actions.foreach(_(this))

    // Come back to previous state
    //-------------
    this.state = oldState

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

  /**
   * Current Transactions Map, with stacked transactions for incremental transactions
   */
  var currentTransactions = Map[Thread, Stack[Transaction]]()

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

      //-- Already a transaction
      case Some(transactions) ⇒

        transactions.head

      case None ⇒

        logFine("-- Creating transaction for Thread --")

        var transaction = new Transaction
        transaction.initiator = initiator
        currentTransactions += (thread -> Stack(transaction))
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
   * Create a transaction, and stack it for the current thread
   */
  def begin(implicit initiator: AnyRef = null): Transaction = {

    // Take current Thread
    //-------------
    var thread = Thread.currentThread

    // Create or return Transaction
    //---------------
    currentTransactions.get(thread) match {

      //-- Already have some transactions
      case Some(transactions) ⇒

        // Create
        var transaction = new Transaction
        transaction.initiator = initiator

        // Stack
        transactions.push(transaction)

        transaction

      case None ⇒

        logFine("-- Creating transaction for Thread --")

        var transaction = new Transaction
        transaction.initiator = initiator
        currentTransactions += (thread -> Stack(transaction))
        return transaction
    }

  }

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

    currentTransactions.find { case (th, tr) ⇒ tr.head == transaction } match {
      case Some((th, tr)) ⇒

        // println("Found Transaction to discard for current Thread")
        tr.head.discard
        tr.pop

        // If no more transactions for thread, clean
        if (tr.size == 0)
          currentTransactions -= th

      case None ⇒
      // FIXME : Return an error trying to discard a non registered transaction ?
    }

  }

  /**
   * Discard All the transactions, and cancels not closed one
   */
  def discardAll = {

    currentTransactions.foreach { case (th, tr) ⇒ tr.foreach(discard(_)) }

  }

}
