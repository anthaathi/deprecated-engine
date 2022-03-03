package com.anthaathi.engine.jpa_graphql_mapper.directive.registry

import com.anthaathi.engine.jpa_graphql_mapper.annotation.DgsJPAFilter
import graphql.language.FieldDefinition
import graphql.language.NonNullType
import graphql.language.ObjectTypeDefinition
import graphql.language.TypeName
import graphql.schema.idl.SchemaParser
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationContext

internal class EntityMapperDirectiveTest {
    @MockK
    lateinit var applicationContextMock: ApplicationContext

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun registry() {
        // language=GraphQL
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

        val expectedRegistry = EntityMapperDirective().registry(typeRegistry)

        assertThat(expectedRegistry.types()["MovieConnection"]).isNotNull
        assertThat(expectedRegistry.types()["J"]).isNotNull

        println(expectedRegistry)

        assertThat(expectedRegistry.types()["Query"]).isNotNull
    }

    @Test
    fun getAllTypesWithEntity() {
    }

    @Test
    fun createInputs() {
        EntityMapperDirective().createInputs(
            ObjectTypeDefinition.newObjectTypeDefinition().name("")
                .fieldDefinition(
                    FieldDefinition
                        .newFieldDefinition()
                        .name("Test")
                        .type(NonNullType(TypeName("String")))
                        .build(),
                )
                .build()
        );
    }
}
