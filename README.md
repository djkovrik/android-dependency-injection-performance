# Android Injection Performance

This project aims to measure the performance of several Dependency Injection frameworks (or Service Locators) in different devices.

## Libraries tested
- [Koin](https://insert-koin.io/) - 2.0.1
- [Kodein](http://kodein.org/Kodein-DI/) - 6.4.1
- [Toothpick](https://github.com/stephanenicolas/toothpick) - 3.1.0
- [Dagger 2](https://google.github.io/dagger/) - 2.25.2


## The test
The test data are classes with dependencies in a structure similar to Fibonacci sequence, to simulate multiple levels of transitive dependencies.
For each library there is a test with Kotlin classes and one with Java classes, because some libraries seem to be affected by this difference.

Each test injects one of this dependencies 100 times and prints the maximum time, the minimum and the average.

The project contains an Android application that run the tests on its onCreate and prints the result to the Logcat.

The actual test is implemented in the class [InjectionTest.kt](https://github.com/Sloy/android-dependency-injection-performance/blob/master/app/src/main/java/com/sloydev/dependencyinjectionperformance/InjectionTest.kt)

c-tor means constructor injection.

## Results

### Meizu M6 Note with Android 7.1.2

#### Fib8

Library | Setup Java | Setup Kotlin | Setup Kotlin c-tor | Inject Java | Inject Kotlin | Inject Kotlin c-tor
--- | ---:| ---:| ---:| ---:| ---:| ---:
**Koin** | 1.33 ms | 1.16 ms | 1.15 ms | 0.12 ms | 0.12 ms |0.45 ms
**Kodein** | 11.63 ms | 10.91 ms |  -  | 0.30 ms | 0.19 ms | - 
**Toothpick** | 2.34 ms | 2.34 ms | 0.84 ms | 0.11 ms | 0.07 ms |0.10 ms
**Dagger** | 0.01 ms | 0.01 ms | 0.01 ms | 0.09 ms | 0.07 ms |0.05 ms

#### Fib16

Library | Setup Java | Setup Kotlin | Setup Kotlin c-tor | Inject Java | Inject Kotlin | Inject Kotlin c-tor
--- | ---:| ---:| ---:| ---:| ---:| ---:
**Koin** | 1.21 ms | 1.23 ms | 1.10 ms | 4.44 ms | 4.56 ms |19.70 ms
**Kodein** | 11.09 ms | 10.70 ms |  -  | 10.05 ms | 9.59 ms | - 
**Toothpick** | 2.37 ms | 2.33 ms | 0.84 ms | 2.05 ms | 2.21 ms |3.96 ms
**Dagger** | 0.01 ms | 0.01 ms | 0.01 ms | 0.38 ms | 0.48 ms |0.25 ms

#### Fib18
Library | Setup Java | Setup Kotlin | Setup Kotlin c-tor | Inject Java | Inject Kotlin | Inject Kotlin c-tor
--- | ---:| ---:| ---:| ---:| ---:| ---:
**Koin** | 1.30 ms | 1.14 ms | 1.13 ms | 12.59 ms | 12.40 ms |53.28 ms
**Kodein** | 11.07 ms | 10.81 ms |  -  | 27.28 ms | 27.43 ms | - 
**Toothpick** | 2.39 ms | 2.36 ms | 0.83 ms | 5.16 ms | 5.60 ms |10.08 ms
**Dagger** | 0.01 ms | 0.01 ms | 0.01 ms | 0.78 ms | 1.00 ms |0.65 ms


### Google Pixel 3 with Android 10

#### Fib8

Library | Setup Java | Setup Kotlin | Setup Kotlin c-tor | Inject Java | Inject Kotlin | Inject Kotlin c-tor
--- | ---:| ---:| ---:| ---:| ---:| ---:
**Koin** | 0.35 ms | 0.33 ms | 0.33 ms | 0.10 ms | 0.09 ms |0.11 ms
**Kodein** | 3.97 ms | 3.94 ms |  -  | 0.17 ms | 0.08 ms | - 
**Toothpick** | 0.41 ms | 0.37 ms | 0.21 ms | 0.08 ms | 0.06 ms |0.05 ms
**Dagger** | 0.00 ms | 0.00 ms | 0.00 ms | 0.03 ms | 0.03 ms |0.01 ms

#### Fib16

Library | Setup Java | Setup Kotlin | Setup Kotlin c-tor | Inject Java | Inject Kotlin | Inject Kotlin c-tor
--- | ---:| ---:| ---:| ---:| ---:| ---:
**Koin** | 0.32 ms | 0.31 ms | 0.31 ms | 1.00 ms | 1.02 ms |3.16 ms
**Kodein** | 3.74 ms | 3.77 ms |  -  | 3.50 ms | 3.40 ms | - 
**Toothpick** | 0.41 ms | 0.37 ms | 0.21 ms | 0.43 ms | 0.44 ms |0.73 ms
**Dagger** | 0.00 ms | 0.00 ms | 0.00 ms | 0.11 ms | 0.14 ms |0.07 ms

#### Fib18
Library | Setup Java | Setup Kotlin | Setup Kotlin c-tor | Inject Java | Inject Kotlin | Inject Kotlin c-tor
--- | ---:| ---:| ---:| ---:| ---:| ---:
**Koin** | 0.32 ms | 0.31 ms | 0.32 ms | 2.57 ms | 2.63 ms |9.65 ms
**Kodein** | 3.90 ms | 3.74 ms |  -  | 9.80 ms | 9.68 ms | - 
**Toothpick** | 0.40 ms | 0.38 ms | 0.22 ms | 1.04 ms | 1.10 ms |1.89 ms
**Dagger** | 0.00 ms | 0.00 ms | 0.00 ms | 0.26 ms | 0.29 ms |0.15 ms
