package kotlinx.fuzzer.fuzzing

import kotlinx.fuzzer.coverage.InstanceCreator
import java.lang.reflect.Method

class TargetMethod(private val targetClass: Class<*>, methodName: String) {
    private val method = targetClass.declaredMethods
        .filter { it.name == methodName }
        .singleOrNull { isApplicableMethodSignature(it) }
        ?: throw IllegalArgumentException("Single method $methodName with correct signature not found!")

    fun execute(input: ByteArray) {
        val targetInstance = InstanceCreator.constructDefault(targetClass)
        method.invoke(targetInstance, input)
    }

    internal companion object {
        internal fun isApplicableMethodSignature(method: Method): Boolean {
            if (method.returnType != Int::class.java) return false
            if (method.parameterCount != 1) return false
            if (method.parameterTypes[0] != ByteArray::class.java) return false
            return true
        }
    }
}
