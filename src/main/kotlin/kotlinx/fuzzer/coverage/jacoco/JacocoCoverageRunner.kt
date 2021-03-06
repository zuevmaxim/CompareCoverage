package kotlinx.fuzzer.coverage.jacoco

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.coverage.jacoco.classload.Loader
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.analysis.IClassCoverage
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.SessionInfoStore
import org.jacoco.core.runtime.LoggerRuntime
import org.jacoco.core.runtime.RuntimeData

/**
 * Code coverage using Jacoco library.
 * Uses class loader for bytecode transformation.
 */
internal class JacocoCoverageRunner(classpath: List<String>, packages: Collection<String>) : CoverageRunner {
    private val loader = Loader(classpath, packages)
    private val runtime = LoggerRuntime()
    private val classes = loader.load(runtime)
    private val data = RuntimeData()

    init {
        runtime.startup(data)
    }

    /** Run function [f] with coverage of predefined packages. */
    override fun runWithCoverage(f: () -> Unit): Int {
        f()

        val executionData = ExecutionDataStore()
        val sessionInfos = SessionInfoStore()
        data.collect(executionData, sessionInfos, false)

        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(executionData, coverageBuilder)
        for (bytes in classes) {
            analyzer.analyzeClass(bytes, "JacocoCoverageRunner")
        }

        data.reset()
        return coverageBuilder.classes
            .map { it.toInt() }
            .sum()
    }

    override fun loadClass(name: String): Class<*>? = loader.classLoader.loadClass(name)
}

private fun IClassCoverage.toInt() = (methodCounter.totalCount - methodCounter.missedCount) +
        (lineCounter.totalCount - lineCounter.missedCount) +
        (branchCounter.totalCount - branchCounter.missedCount)
