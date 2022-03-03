package com.anthaathi.engine.jpa_graphql_mapper.filter.generator

import au.com.origin.snapshots.junit5.SnapshotExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import au.com.origin.snapshots.Expect;

@ExtendWith(SnapshotExtension::class)
internal class StringFilterGeneratorTest {
    private lateinit var expect: Expect

    @Test
    fun createType() {
        assertThat(StringFilterGenerator().createType()).isNotNull
        expect.toMatchSnapshot(StringFilterGenerator().createType())
    }
}
