package com.anthaathi.engine.jpa_graphql_mapper.directive.registry

import graphql.schema.idl.SchemaParser
import org.junit.jupiter.api.Test

internal class EntityMapperDirectiveTest {

    @Test
    fun registry() {
        // language=graphql
        val schema = """
            interface Node {
                id: ID!
            }
            
            type Movie implements Node @entity(
                className: "com.anthaathi.entity.SomeEntity",
            ) @connection(connectionName: "movies") {
                movieID: ID @column(name: "id")
                title: String
            }

        """.trimIndent()

        val typeRegistry = SchemaParser().parse(schema)

        EntityMapperDirective().registry(typeRegistry)
    }

    @Test
    fun getAllTypesWithEntity() {
    }
}
