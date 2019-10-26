package com.sloydev.dependencyinjectionperformance

import com.sloydev.dependencyinjectionperformance.dagger2.*
import com.sloydev.dependencyinjectionperformance.koin.koinConstructorKotlinModule
import com.sloydev.dependencyinjectionperformance.koin.koinJavaModule
import com.sloydev.dependencyinjectionperformance.koin.koinKotlinModule
import com.sloydev.dependencyinjectionperformance.toothpick.toothpickJavaModule
import com.sloydev.dependencyinjectionperformance.toothpick.toothpickKotlinModule
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.instance
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import toothpick.Scope
import toothpick.Toothpick
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module
import javax.inject.Inject

class InjectionTest : KoinComponent {

    private val kotlinDaggerTest = KotlinDaggerTest()
    private val kotlinConstructorDaggerTest = KotlinConstructorDaggerTest()
    private val javaDaggerTest = JavaDaggerTest()

    private val rounds = 100

    fun runTests(): List<LibraryResult> =
        listOf(
            koinTest(),
            kodeinTest(),
            toothpickTest(),
            daggerTest()
        )

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
                test = { get<FibonacciJava.Fib18>() },
                teardown = { stopKoin() }
            ),
            Variant.KOTLIN to runTest(
                setup = {
                    startKoin {
                        modules(koinKotlinModule)
                    }
                },
                test = { get<Fib18>() },
                teardown = { stopKoin() }
            ),
            Variant.CONSTRUCTOR_KOTLIN to runTest(
                setup = {
                    startKoin {
                        modules(koinConstructorKotlinModule)
                    }
                },
                test = { get<Fib18>() },
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
                test = { kodein.direct.instance<Fib18>() }
            ),
            Variant.KOTLIN to runTest(
                setup = { kodein = Kodein { import(kodeinKotlinModule) } },
                test = { kodein.direct.instance<Fib18>() }
            )
        ))
    }

    private fun daggerTest(): LibraryResult {
        log("Running Dagger...")
        lateinit var kotlinComponent: KotlinDaggerComponent
        lateinit var kotlinConstructorComponent: KotlinConstructorDaggerComponent
        lateinit var javaComponent: JavaDaggerComponent
        return LibraryResult("Dagger", mapOf(
            Variant.JAVA to runTest(
                setup = { javaComponent = DaggerJavaDaggerComponent.create() },
                test = { javaComponent.inject(javaDaggerTest) }
            ),
            Variant.KOTLIN to runTest(
                setup = { kotlinComponent = DaggerKotlinDaggerComponent.create() },
                test = { kotlinComponent.inject(kotlinDaggerTest) }
            ),
            Variant.CONSTRUCTOR_KOTLIN to runTest(
                setup = { kotlinConstructorComponent = DaggerKotlinConstructorDaggerComponent.create() },
                test = { kotlinConstructorComponent.inject(kotlinConstructorDaggerTest) }
            )
        ))
    }

    private fun toothpickTest(): LibraryResult {
        log("Running Toothpick...")
        lateinit var scope: Scope
        return LibraryResult("Toothpick", mapOf(
            Variant.JAVA to runTest(
                setup = {
                    scope = Toothpick.openScope("java").apply {
                        installModules(toothpickJavaModule)
                    }
                    Toothpick.inject(this, scope)
                },
                test = { scope.getInstance(FibonacciJavaTP.Fib18::class.java) },
                teardown = { Toothpick.closeScope("java") }
            ),
            Variant.KOTLIN to runTest(
                setup = {
                    scope = Toothpick.openScope("kotlin").apply {
                        installModules(toothpickKotlinModule)
                    }
                    Toothpick.inject(this, scope)
                },
                test = { scope.getInstance(Fib18::class.java) },
                teardown = { Toothpick.closeScope("kotlin") }
            ),
            Variant.CONSTRUCTOR_KOTLIN to runTest(
                setup = {
                    scope = Toothpick.openScope("constructor").apply {
                        installModules(module {
                            bind<KotlinConstructorToothpickTest>()
                        })
                    }
                    Toothpick.inject(this, scope)
                },
                test = { scope.getInstance(KotlinConstructorToothpickTest::class.java) }
            )
        ))
    }

    class KotlinDaggerTest {
        @Inject
        lateinit var daggerFib18: Fib18
    }

    class JavaDaggerTest {
        @Inject
        lateinit var daggerFib18: FibonacciJava.Fib18
    }

    class KotlinConstructorDaggerTest {
        @Inject
        lateinit var daggerFib18: Fib18
    }

    class KotlinConstructorToothpickTest {
        @Inject
        lateinit var daggerFib18: Fib18
    }
}
