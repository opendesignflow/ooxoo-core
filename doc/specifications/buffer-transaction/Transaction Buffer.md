Transaction Buffer
==========================


Transaction based data operation is very standard in Databases Technology.

It allows maintaining data coherency during a process involving data manipulation,
that is to say, ensure that all the data manipulations an algorithm is reponsible of, only gets really applied if the algorithm successes.

This functionality is usually provided by a framework, that makes data manipulation transparent to the user, and takes care of applying or reverting changes.
Those actions are called:

- Commit: This is when the transaction tries to apply all the changes
- Cancel/Rollback: When an error occured during the transaction or while committing, rollbacks all modifications to the state before the transaction 
  FIXME: Is cancel different in operation than rollback

# Overview


The Transaction Buffer provides Transactional functionality when working with the OOXOO library.
In the following picture, you can see the various calls and components provided by the transaction buffer

- Transaction Buffer is to be inserted in a chain
- Push:
	- When Receiving push calls, the DataUnit gets cached, and is not passed to the next buffer
	- A transaction action is registered to the current Thread Transaction
- Pull:
	- When a pull is seen, the normal pull operation is called, but the resulting Data Unit gets cached
	- Subsequent pulls are not propagated and return the cached data unit
- Transaction commit:
	- Perform the normal push: Push the Cached push data unit to the next buffer
- Transaction rollback:
	- Forget about cached push data unit
	- Pull the cached pull data unit to the previous buffer, so that it gets resetted to the value before the last received push

The package also provides a Transaction Class and singleton to manage the living Transactions per Threads

# Example

- Commit:
	
	import com.idyria.osi.ooxoo.core.buffers.datatypes._
	import com.idyria.osi.ooxoo.core.buffers.extras.transaction._

	// Create a LongBuffer to work with a long
	var longBuffer = Buffer(10)

	// Add a transaction buffer
	longBuffer - new TransactionBuffer

	// Change value
	longBufer.set(89)

	// Print (89)
	println("Long: "+longBuffer.data)

	// Commit and print same result (89), but that's normal
	Transaction().commit
	println("Long: "+longBuffer.data)


- Cancel/Rollback

	import com.idyria.osi.ooxoo.core.buffers.datatypes._
	import com.idyria.osi.ooxoo.core.buffers.extras.transaction._

	// Create a LongBuffer to work with a long
	var longBuffer = Buffer(10)

	// Add a transaction buffer
	longBuffer - new TransactionBuffer

	// Change value
	longBufer.set(89)

	// Print (89)
	println("Long: "+longBuffer.data)

	// Cancel and print back original value
	Transaction().cancel

	// (10)
	println("Long: "+longBuffer.data)