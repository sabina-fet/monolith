package com.example.sabina.api.models.neo4j

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

/**
 * @author Sabina Muhic
 */
@Node("Account")
data class Account(
    @Id val id: String,

    @Relationship(type = "SEND")
    val sends: MutableList<Send> = mutableListOf()
)