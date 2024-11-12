package com.example.sabina.api.models.neo4j

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.LocalDateTime

/**
 * @author Sabina Muhic
 */
@RelationshipProperties
data class Send(
    @Id @GeneratedValue val id: Long? = null,
    val amount: Double,
    val time: Long,
    @TargetNode val toAccount: Account
)