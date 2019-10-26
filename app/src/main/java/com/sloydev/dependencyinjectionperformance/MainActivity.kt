package com.sloydev.dependencyinjectionperformance

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.plusAssign
import androidx.core.view.updatePadding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            runTests()
        }

        runTests()
    }

    private fun runTests() {
        val results = InjectionTest().runTests()
        reportOnLogcat(results)
        reportOnScreen(results)
    }

    @Suppress("RemoveSingleExpressionStringTemplate")
    private fun reportOnLogcat(results: List<LibraryResult>) {
        log("Done!")
        log("${Build.BRAND} ${Build.DEVICE} with Android ${Build.VERSION.RELEASE}")
        log(" ")
        log("Library | Setup Java | Setup Kotlin | Setup Kotlin Constructor | Inject Java | Inject Kotlin | Inject Kotlin Constructor")
        log("--- | ---:| ---:| ---:| ---:| ---:| ---:")
        results.forEach { result ->
            log(
                "**${result.injectorName}** | " +
                        "${result[Variant.JAVA]?.startupTime?.median().format()} | " +
                        "${result[Variant.KOTLIN]?.startupTime?.median().format()} | " +
                        "${result[Variant.CONSTRUCTOR_KOTLIN]?.startupTime?.median().format()} | " +
                        "${result[Variant.JAVA]?.injectionTime?.median().format()} | " +
                        "${result[Variant.KOTLIN]?.injectionTime?.median().format()} |" +
                        "${result[Variant.CONSTRUCTOR_KOTLIN]?.injectionTime?.median().format()}"
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun reportOnScreen(results: List<LibraryResult>) {
        deviceInfo.text = """
                ${Build.BRAND} · ${Build.DEVICE}
                Android ${Build.VERSION.RELEASE} (sdk ${Build.VERSION.SDK_INT})
            """.trimIndent()

        results.forEach { result ->
            table += row(
                nameCell(result.injectorName),
                timeCell(result[Variant.JAVA]?.startupTime?.median()),
                timeCell(result[Variant.KOTLIN]?.startupTime?.median()),
                timeCell(result[Variant.JAVA]?.injectionTime?.median()),
                timeCell(result[Variant.KOTLIN]?.injectionTime?.median())
            )
        }

        table.addView(
            separator(),
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.dp)
        )
    }

    private fun timeCell(time: Double?): TextView {
        return time?.let {
            TextView(this).apply {
                text = time.format()
                gravity = GravityCompat.END
            }
        } ?: TextView(this).apply {
            text = " - "
        }
    }

    private fun nameCell(name: String): TextView {
        return TextView(this).apply {
            text = name
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun row(vararg cells: View): TableRow {
        return TableRow(this).also { row ->
            cells.forEach { cell ->
                row += cell
            }
            row.updatePadding(
                top = 4.dp,
                bottom = 4.dp
            )
        }
    }

    private fun separator(): View {
        return View(this).apply {
            setBackgroundColor(resources.getColor(R.color.colorAccent))
            updatePadding(
                top = 4.dp,
                bottom = 4.dp
            )
        }
    }

}
