package nodomain.aditya1875more.stashly

import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.runners.JUnit4
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.anchor", appContext.packageName)
    }
}

@RunWith(JUnit4::class)
class AutomateScreenshotsTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java)
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
    }

    @Test
    fun captureScreen() {
        // Delay 500 millis for app launch to main screen
        Thread.sleep(500)
        Screengrab.screenshot("main_screen")
    }
}