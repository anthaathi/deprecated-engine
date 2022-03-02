package com.anthaathi.engine.jpa_graphql_mapper.configuration

import com.anthaathi.engine.jpa_graphql_mapper.directive.evaluator.EntityMapperDirectiveWiring
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsRuntimeWiring
import graphql.schema.idl.RuntimeWiring


@DgsComponent
class SecuredDirectiveRegistration(private val entityMapperDirectiveWiring: EntityMapperDirectiveWiring) {
    /**
     * Registers schema directive wiring for `@entity` directive.
     *
     * @param builder
     * @return RuntimeWiring.Builder
     */
    @DgsRuntimeWiring
    fun addSecuredDirective(builder: RuntimeWiring.Builder): RuntimeWiring.Builder {
        return builder.directive(
            EntityMapperDirectiveWiring.ENTITY_DIRECTIVE,
            entityMapperDirectiveWiring
        )
    }
}
