package com.example.sabina.api.service

import com.example.sabina.api.controllers.CreateTransactionRequest
import com.example.sabina.api.models.Transaction
import com.example.sabina.api.models.TransactionStatus
import com.example.sabina.api.models.neo4j.Account
import com.example.sabina.api.models.neo4j.Send
import com.example.sabina.api.repositories.AccountRepository
import com.example.sabina.api.repositories.TransactionRepository
import com.example.sabina.api.utils.unwrap
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val accountRepositoryNeo4j: com.example.sabina.api.repositories.neo4j.AccountRepositoryNeo4j
) {

    @Transactional
    fun executeTransaction(request: CreateTransactionRequest): ResponseEntity<String> =
        accountRepository.findById(request.src).unwrap()?.let { account ->
            when (account.balance < request.amount) {
                true -> ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient funds")
                else -> {
                    account.balance -= request.amount;
                    accountRepository.save(account)
                    val transaction = Transaction(
                        id = null,
                        datetime = Instant.now().toEpochMilli(),
                        amount = -request.amount,
                        status = TransactionStatus.PENDING,
                        source = account,
                        destination = request.dest
                    )
                    transactionRepository.save(transaction)

                    val destinationAccount = accountRepository.findAccountByNumber(request.dest)
                    if (destinationAccount != null) {
                        destinationAccount.balance += request.amount
                        accountRepository.save(destinationAccount)
                    }

                    val source = accountRepositoryNeo4j
                        .findById(transaction.source!!.accountNumber!!)
                        .orElseGet {
                            accountRepositoryNeo4j
                                .save(
                                    Account(transaction.source!!.accountNumber!!)
                                )
                        }

                    // Neo4j relationship
                    val destination = accountRepositoryNeo4j.findById(transaction.destination!!)
                        .orElseGet { accountRepositoryNeo4j.save(Account(transaction.destination!!)) }

                    val send = Send(amount = transaction.amount!!, time = transaction.datetime!!, toAccount = destination)

                    source.sends.add(send)
                    accountRepositoryNeo4j.save(source)

                    ResponseEntity(HttpStatus.CREATED)
                }
            }
        } ?: ResponseEntity(HttpStatus.OK)
}