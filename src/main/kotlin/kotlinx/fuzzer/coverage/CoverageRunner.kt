package kotlinx.fuzzer.coverage

import kotlinx.fuzzer.coverage.jacoco.JacocoCoverageRunner

/** Defines the way of running test with coverage. In particular, resolves class loading. */
interface CoverageRunner {
    fun runWithCoverage(f: () -> Unit): Int
    fun loadClass(name: String): Class<*>?
}

fun createJacocoCoverageRunner(): CoverageRunner {
    return JacocoCoverageRunner(emptyList(), listOf("org.apache.commons.compress", "kotlinx.fuzzer.tests.apache.zip"))
}
