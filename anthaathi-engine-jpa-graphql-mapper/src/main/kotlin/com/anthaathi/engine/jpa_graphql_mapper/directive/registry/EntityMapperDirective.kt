package com.anthaathi.engine.jpa_graphql_mapper.directive.registry

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsTypeDefinitionRegistry
import graphql.introspection.Introspection
import graphql.language.*
import graphql.schema.idl.TypeDefinitionRegistry

// Ref: https://github.com/Netflix/dgs-framework/blob/fbff648ad954e0768b9f454afb317c0a812cce42/graphql-dgs-pagination/src/main/kotlin/com/netflix/graphql/dgs/pagination/DgsPaginationTypeDefinitionRegistry.kt

@DgsComponent
class EntityMapperDirective {
    @DgsTypeDefinitionRegistry
    fun registry(schemaRegistry: TypeDefinitionRegistry): TypeDefinitionRegistry {
        val definitions = schemaRegistry.types()
        val connectionTypes = parseConnectionDirective(definitions.values.toMutableList())

        val typeDefinitionRegistry = TypeDefinitionRegistry()
        typeDefinitionRegistry.addAll(connectionTypes)
        if (!schemaRegistry.directiveDefinitions.contains("connection")) {
            val directive = DirectiveDefinition.newDirectiveDefinition().name("connection")
                .directiveLocation(
                    DirectiveLocation.newDirectiveLocation().name(
                        Introspection.DirectiveLocation.OBJECT.name
                    ).build()
                ).build()
            typeDefinitionRegistry.add(directive)
        }

        getNodeInterface(definitions)

        val query = ObjectTypeExtensionDefinition.newObjectTypeExtensionDefinition().name("Query")

        typeDefinitionRegistry.add(query.build())

        return typeDefinitionRegistry
    }

    private fun getNodeInterface(
        definitions: MutableMap<String, TypeDefinition<TypeDefinition<*>>>,
    ): InterfaceTypeDefinition {
        return if (definitions.containsKey("Node")) {
            val nodeDef = definitions["Node"] as InterfaceTypeDefinition

            if (nodeDef.fieldDefinitions.find {
                    it.name == "id" && it.type is NonNullType
                } == null
            ) {
                throw IllegalArgumentException("Invalid interface Node which was registered before")
            }

            nodeDef
        } else {
            InterfaceTypeDefinition.newInterfaceTypeDefinition()
                .name("Node")
                .definition(FieldDefinition("ID", TypeName("ID")))
                .build()
        }
    }

    private fun parseConnectionDirective(types: MutableList<TypeDefinition<*>>): List<TypeDefinition<*>> {
        val definitions = mutableListOf<TypeDefinition<*>>()
        types.filter { it is ObjectTypeDefinition || it is InterfaceTypeDefinition }
            .filter { it.hasDirective("connection") }
            .forEach {
                definitions.add(createInputs(it))
                definitions.add(createConnection(it.name))
                definitions.add(createEdge(it.name))
            }

        if (types.any { it.hasDirective("connection") } && !types.any { it.name == "PageInfo" }) {
            definitions.add(createPageInfo())
        }

        return definitions
    }

    private fun createInputs(type: TypeDefinition<*>): TypeDefinition<*> {
        val filterInputName = type.name + "FilterInput"

        val result = InputObjectTypeDefinition.newInputObjectDefinition().name(filterInputName)

        result.inputValueDefinition(InputValueDefinition.newInputValueDefinition().name("name").type(TypeName("String")).build())

        result.inputValueDefinition(InputValueDefinition.newInputValueDefinition().name("_and").type(TypeName(filterInputName)).build())
        result.inputValueDefinition(InputValueDefinition.newInputValueDefinition().name("_or").type(TypeName(filterInputName)).build())

        return result.build()
    }

    private fun createConnection(type: String): ObjectTypeDefinition {
        return ObjectTypeDefinition.newObjectTypeDefinition()
            .name(type + "Connection")
            .fieldDefinition(FieldDefinition("edges", ListType(TypeName(type + "Edge"))))
            .fieldDefinition(FieldDefinition("pageInfo", TypeName("PageInfo")))
            .build()
    }

    private fun createEdge(type: String): ObjectTypeDefinition {
        return ObjectTypeDefinition.newObjectTypeDefinition()
            .name(type + "Edge")
            .fieldDefinition(FieldDefinition("cursor", TypeName("String")))
            .fieldDefinition(FieldDefinition("node", TypeName(type)))
            .build()
    }

    private fun createPageInfo(): ObjectTypeDefinition {
        return ObjectTypeDefinition.newObjectTypeDefinition()
            .name("PageInfo")
            .fieldDefinition(FieldDefinition("hasPreviousPage", NonNullType(TypeName("Boolean"))))
            .fieldDefinition(FieldDefinition("hasNextPage", NonNullType(TypeName("Boolean"))))
            .fieldDefinition(FieldDefinition("startCursor", TypeName("String")))
            .fieldDefinition(FieldDefinition("endCursor", TypeName("String")))
            .build()
    }
}
