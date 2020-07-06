package kotlinx.fuzzer.coverage.jmh

import kotlinx.fuzzer.coverage.CoverageRunner
import kotlinx.fuzzer.coverage.createJacocoCoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.io.File

open class CoverageTest {

    @State(Scope.Benchmark)
    open class ZipState {
        val zip = File("/Users/Maksim.Zuev/CompareCoverage/test.zip").readBytes()
    }

    @State(Scope.Thread)
    open class JacocoState {
        val coverageRunner = createJacocoCoverageRunner()
        val targetMethod =
            TargetMethod(
                coverageRunner.loadClass("kotlinx.fuzzer.tests.apache.zip.ApacheZipTest")!!,
                "test"
            )
    }

    @Benchmark
    fun jacocoTest(state: JacocoState, zipState: ZipState) {
        test(state.coverageRunner, state.targetMethod, zipState.zip)
    }

    private fun test(
        runner: CoverageRunner,
        targetMethod: TargetMethod,
        zip: ByteArray
    ) {
        runner.runWithCoverage {
            targetMethod.execute(zip)
        }
    }
}
