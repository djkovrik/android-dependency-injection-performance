package com.sloydev.dependencyinjectionperformance

import android.os.Build
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerJavaDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.DaggerKotlinDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.JavaDaggerComponent
import com.sloydev.dependencyinjectionperformance.dagger2.KotlinDaggerComponent
import com.sloydev.dependencyinjectionperformance.koin.koinJavaModule
import com.sloydev.dependencyinjectionperformance.koin.koinKotlinModule
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.instance
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import javax.inject.Inject

class InjectionTest : KoinComponent {

    private val kotlinDaggerTest = KotlinDaggerTest()
    private val javaDaggerTest = JavaDaggerTest()

    private val rounds = 100

    fun runTests(): List<LibraryResult> {
        val results = listOf(
            koinTest(),
            kodeinTest(),
            daggerTest()
        )
        reportMarkdown(results)
        return results
    }

    private fun reportMarkdown(results: List<LibraryResult>) {
        log("Done!")
        log(" ")
        log("${Build.BRAND} ${Build.DEVICE} with Android ${Build.VERSION.RELEASE}")
        log(" ")
        log("Library | Setup Java | Setup Kotlin | Inject Java | Inject Kotlin")
        log("--- | ---:| ---:| ---:| ---:")
        results.forEach { result ->
            log("**${result.injectorName}** | ${result[Variant.JAVA]?.startupTime?.median().format()} | ${result[Variant.KOTLIN]?.startupTime?.median().format()}  | ${result[Variant.JAVA]?.injectionTime?.median().format()} | ${result[Variant.KOTLIN]?.injectionTime?.median().format()}")
        }
    }

    private fun runTest(
        setup: () -> Unit,
        test: () -> Unit,
        teardown: () -> Unit = {}
    ): TestResult {
        val startup = (1..rounds).map { measureTime { setup() }.also { teardown() } }
        setup()

        val testDurations = (1..rounds).map { measureTime { test() } }
        teardown()
        return TestResult(startup, testDurations)
    }

    private fun koinTest(): LibraryResult {
        log("Running Koin...")
        return LibraryResult("Koin", mapOf(
            Variant.JAVA to runTest(
                setup = {
                    startKoin {
                        modules(koinJavaModule)
                    }
                },
                test = { get<FibonacciJava.Fib8>() },
                teardown = { stopKoin() }
            ),
            Variant.KOTLIN to runTest(
                setup = {
                    startKoin {
                        modules(koinKotlinModule)
                    }
                },
                test = { get<Fib8>() },
                teardown = { stopKoin() }
            )
        ))
    }

    private fun kodeinTest(): LibraryResult {
        log("Running Kodein...")
        lateinit var kodein: Kodein
        return LibraryResult("Kodein", mapOf(
            Variant.JAVA to runTest(
                setup = { kodein = Kodein { import(kodeinKotlinModule) } },
                test = { kodein.direct.instance<Fib8>() }
            ),
            Variant.KOTLIN to runTest(
                setup = { kodein = Kodein { import(kodeinKotlinModule) } },
                test = { kodein.direct.instance<Fib8>() }
            )
        ))
    }

    private fun daggerTest(): LibraryResult {
        log("Running Dagger...")
        lateinit var kotlinComponent: KotlinDaggerComponent
        lateinit var javaComponent: JavaDaggerComponent
        return LibraryResult("Dagger", mapOf(
            Variant.JAVA to runTest(
                setup = { javaComponent = DaggerJavaDaggerComponent.create() },
                test = { javaComponent.inject(javaDaggerTest) }
            ),
            Variant.KOTLIN to runTest(
                setup = { kotlinComponent = DaggerKotlinDaggerComponent.create() },
                test = { kotlinComponent.inject(kotlinDaggerTest) }
            )
        ))
    }

    class KotlinDaggerTest {
        @Inject
        lateinit var daggerFib8: Fib8
    }

    class JavaDaggerTest {
        @Inject
        lateinit var daggerFib8: FibonacciJava.Fib8
    }
}
