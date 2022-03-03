package com.anthaathi.engine.jpa_graphql_mapper.filter.generator

import com.anthaathi.engine.jpa_graphql_mapper.annotation.DgsJPAFilter
import com.anthaathi.engine.jpa_graphql_mapper.annotation.DgsJPASchemaWiring
import com.anthaathi.engine.jpa_graphql_mapper.filter.schema.FilterGenerator
import graphql.language.*
import graphql.schema.idl.TypeDefinitionRegistry
import org.springframework.stereotype.Component

@Component
@DgsJPAFilter
class StringFilterGenerator : FilterGenerator {
    companion object {
        const val TYPE_NAME = "StringComparisonExp"
    }

    @DgsJPASchemaWiring
    override fun onSchemaGenerate(
        schemaRegistry: TypeDefinitionRegistry,
        typeDefinition: TypeDefinition<InputObjectTypeDefinition>
    ): TypeDefinitionRegistry {
        if (!schemaRegistry.hasType(TypeName(TYPE_NAME))) {
            schemaRegistry.add(createType())
        }

        typeDefinition

        return schemaRegistry
    }

    fun createType(): SDLDefinition<*> {
        return InputObjectTypeDefinition.newInputObjectDefinition()
            .name(TYPE_NAME)
            .inputValueDefinitions(
                listOf(
                    InputValueDefinition.newInputValueDefinition()
                        .name("_eq")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_gt")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_gte")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_ilike")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_in")
                        .type(ListType(NonNullType(TypeName("String"))))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_iregex")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_is_null")
                        .type(TypeName("Boolean"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_like")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_lt")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_lte")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_neq")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_nilike")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_nin")
                        .type(ListType(NonNullType(TypeName("String"))))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_niregex")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_nlike")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_nregex")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_nsimilar")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_regex")
                        .type(TypeName("String"))
                        .build(),
                    InputValueDefinition.newInputValueDefinition()
                        .name("_similar")
                        .type(TypeName("String"))
                        .build(),
                )
            )
            .build()
    }
}
