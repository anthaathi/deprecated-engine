package com.anthaathi.engine.jpa_graphql_mapper.filter.schema

import graphql.language.InputObjectTypeDefinition
import graphql.language.ObjectTypeDefinition
import graphql.language.TypeDefinition
import graphql.schema.idl.TypeDefinitionRegistry

interface FilterGenerator {
    fun onSchemaGenerate(
        schemaRegistry: TypeDefinitionRegistry,
        typeDefinition: TypeDefinition<InputObjectTypeDefinition>,
    ): TypeDefinitionRegistry
}
