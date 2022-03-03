package com.anthaathi.engine.jpa_graphql_mapper.directive.registry

import com.anthaathi.engine.jpa_graphql_mapper.annotation.DgsJPAFilter
import com.anthaathi.engine.jpa_graphql_mapper.annotation.DgsJPASchemaWiring
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsTypeDefinitionRegistry
import graphql.introspection.Introspection
import graphql.language.*
import graphql.schema.idl.TypeDefinitionRegistry
import org.springframework.context.ApplicationContext
import java.lang.reflect.Method
import kotlin.reflect.full.findAnnotation

// Ref: https://github.com/Netflix/dgs-framework/blob/fbff648ad954e0768b9f454afb317c0a812cce42/graphql-dgs-pagination/src/main/kotlin/com/netflix/graphql/dgs/pagination/DgsPaginationTypeDefinitionRegistry.kt

@DgsComponent
class EntityMapperDirective(
    private val context: ApplicationContext? = null
) {
    private var providers: List<Sequence<Method>>? = null

    init {
        this.providers = getSchemaWiringProviders();
    }

    /**
     * Get all the methods annotated with DgsJPASchemaWiring and filter out the ones that have a parameter of type
     * TypeDefinitionRegistry
     *
     * @return A sequence of methods that have the @DgsJPASchemaWiring annotation.
     */
    private final fun getSchemaWiringProviders(): List<Sequence<Method>>? {
        return context?.getBeansWithAnnotation(DgsJPAFilter::class.java)?.map { (_, bean) ->
            bean.javaClass.methods.asSequence()
                .filter { it.isAnnotationPresent(DgsJPASchemaWiring::class.java) }
                .filter { it.parameterCount == 1 && it.parameterTypes[0] == TypeDefinitionRegistry::class.java }
        }
    }

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

        val interfaceNodeTypeDefinition = getNodeInterface(definitions)

        if (interfaceNodeTypeDefinition != null) {
            typeDefinitionRegistry.add(interfaceNodeTypeDefinition)
        }

        val query = ObjectTypeExtensionDefinition.newObjectTypeExtensionDefinition().name("Query")

        typeDefinitionRegistry.add(query.build())

        return typeDefinitionRegistry
    }

    /**
     * It creates an interface type definition for the `Node` type if it doesn't already exist
     *
     * @param definitions A map of all the type definitions that have been created so far.
     * @return The `InterfaceTypeDefinition` object.
     */
    private fun getNodeInterface(
        definitions: MutableMap<String, TypeDefinition<TypeDefinition<*>>>,
    ): InterfaceTypeDefinition? {
        return if (!definitions.containsKey("Node")) {
            InterfaceTypeDefinition.newInterfaceTypeDefinition()
                .name("Node")
                .definition(FieldDefinition("ID", TypeName("ID")))
                .build()
        } else {
            null
        }
    }

    /**
     * If the type has a connection directive, create the inputs, connection, and edge types
     *
     * @param types The list of types that are being parsed.
     * @return A list of type definitions.
     */
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

    fun createInputs(type: TypeDefinition<*>): TypeDefinition<*> {
        val filterInputName = type.name + "FilterInput"

        val result = InputObjectTypeDefinition.newInputObjectDefinition().name(filterInputName)

        result.inputValueDefinition(
            InputValueDefinition.newInputValueDefinition().name("_and").type(TypeName(filterInputName)).build()
        )
        result.inputValueDefinition(
            InputValueDefinition.newInputValueDefinition().name("_or").type(TypeName(filterInputName)).build()
        )
        result.inputValueDefinition(
            InputValueDefinition.newInputValueDefinition().name("_not").type(TypeName(filterInputName)).build()
        )
        type.children.filterIsInstance<FieldDefinition>().forEach {
            println(it.type)
            println(NonNullType(TypeName("String")))

            println(it.type.isEqualTo(NonNullType(TypeName("String"))))
        }

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
