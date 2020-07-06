package kotlinx.fuzzer.coverage.jmh

import jwp.fuzz.Fuzzer
import jwp.fuzz.Invoker
import jwp.fuzz.ParamProvider
import kotlinx.fuzzer.coverage.createJacocoCoverageRunner
import kotlinx.fuzzer.fuzzing.TargetMethod
import kotlinx.fuzzer.tests.apache.zip.ApacheZipTest
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
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
        state.coverageRunner.runWithCoverage {
            state.targetMethod.execute(zipState.zip)
        }
    }

    @State(Scope.Thread)
    open class JwpState {
        private val config: Fuzzer.Config = Fuzzer.Config.builder()
            .method(ApacheZipTest::class.java.getMethod("test", ByteArray::class.java))
            .params(ParamProvider.suggested(ByteArray::class.java))
            .build()
        val invokerConfig = Invoker.Config(config.tracer, config.method)
        val targetMethod = TargetMethod(ApacheZipTest::class.java, "test")
    }

    @Benchmark
    @Fork(jvmArgs = ["-javaagent:/Users/Maksim.Zuev/CompareCoverage/agent/build/libs/agent.jar"])
    fun jwpTest(state: JwpState, zipState: ZipState) {
        val thread = Thread.currentThread()
        state.invokerConfig.tracer.startTrace(thread)
        state.targetMethod.execute(zipState.zip)
        state.invokerConfig.tracer.stopTrace(thread)
    }
}
