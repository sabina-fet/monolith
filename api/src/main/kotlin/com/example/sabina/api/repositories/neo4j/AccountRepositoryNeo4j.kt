package com.example.sabina.api.repositories.neo4j

import com.example.sabina.api.models.neo4j.Account
import org.springframework.data.neo4j.repository.Neo4jRepository

/**
 * @author Sabina Muhic
 */
interface AccountRepositoryNeo4j : Neo4jRepository<Account, String> {

}