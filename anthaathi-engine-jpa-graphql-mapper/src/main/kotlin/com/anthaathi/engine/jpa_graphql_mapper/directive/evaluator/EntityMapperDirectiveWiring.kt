package com.anthaathi.engine.jpa_graphql_mapper.directive.evaluator

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsTypeDefinitionRegistry
import graphql.language.FieldDefinition
import graphql.language.ObjectTypeExtensionDefinition
import graphql.language.TypeName
import graphql.schema.GraphQLObjectType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.TypeDefinitionRegistry
import org.springframework.stereotype.Component


@Component
class EntityMapperDirectiveWiring : SchemaDirectiveWiring {
    companion object {
        const val ENTITY_DIRECTIVE = "entity"
    }

    override fun onObject(environment: SchemaDirectiveWiringEnvironment<GraphQLObjectType>?): GraphQLObjectType {
        println("onObject")
        println(environment!!.directive.name)

        val query = ObjectTypeExtensionDefinition.newObjectTypeExtensionDefinition().name("Query").fieldDefinition(
            FieldDefinition.newFieldDefinition().name("randomNumber").type(TypeName("Int")).build()
        ).build()

        environment.registry.add(query)

        return super.onObject(environment)
    }
}
