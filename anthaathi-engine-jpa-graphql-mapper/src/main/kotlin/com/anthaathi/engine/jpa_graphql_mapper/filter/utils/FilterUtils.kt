package com.anthaathi.engine.jpa_graphql_mapper.filter.utils

import graphql.language.NonNullType
import graphql.language.Type

fun checkWithOrWithoutNullable(type1: Type<*>, type2: Type<*>): Boolean {
    if (type1 !is NonNullType && type2 !is NonNullType) {
        return type1.isEqualTo(type2)
    }

    return true
}
