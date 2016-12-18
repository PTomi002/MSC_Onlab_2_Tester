package hu.bme.msc.onlab.framework;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

import hu.bme.msc.onlab.framework.util.EventListenerHolder;

public abstract class FrameworkTestCase {

	// ~ Fields
	// ================================================================================================
	protected final Logger testLogger = LoggerFactory.getLogger(getClass());

	private static final Assertion HARD = new Assertion();

	private static final SoftAssert SOFT = new SoftAssert();

	private final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String LINE_SEPARATOR = "<br/>";

	// ~ Methods
	// ================================================================================================
	@BeforeMethod(alwaysRun = true)
	protected void frameWorkBeforeMethodSetUp(final Method testMethod) {
		setTestInfo("<--------------- START TEST ---------------->");
		setTestInfo("TEST NAME:            " + testMethod.getName());
		setTestInfo("<------------------------------------------->");
	}

	@AfterMethod(alwaysRun = true)
	protected void frameWorkAfterMethodTearDown(final ITestResult testResult) {
		setTestInfo("<--------------- TEST RESULT --------------->");
		setTestInfo("TEST FUNCTIONAL AREAS: " + Arrays.asList(testResult.getMethod().getGroups()));
		setTestInfo("TEST RESULT:           " + testResult.isSuccess());
		setTestInfo("<------------------------------------------->");
	}
	
	@BeforeSuite(alwaysRun = true)
	protected void frameWorkBeforeSuiteSetUp(){
		setTestInfo("Starting event listener(s)...");
		EventListenerHolder.getInstance().runAll();
	}
	
	@AfterSuite(alwaysRun = true)
	protected void frameWorkAfterSuiteSetUp(){
		setTestInfo("Stopping event listener(s)...");
		EventListenerHolder.getInstance().stopAll();
	}

	// ~ Assert Methods
	// ================================================================================================
	protected static void fail(String message) {
		saveFail(message, null);
	}

	protected static void fail(String message, Throwable t) {
		Optional<String> opt = Optional.ofNullable(message);
		Optional<Throwable> optExc = Optional.ofNullable(t);

		if (opt.isPresent() && optExc.isPresent()) {
			HARD.fail(opt.get(), optExc.get());
		} else if (opt.isPresent()) {
			HARD.fail(opt.get());
		} else {
			HARD.fail();
		}
	}

	protected static void assertTrue(String message, boolean condition) {
		Optional<String> opt = Optional.ofNullable(message);
		if (opt.isPresent()) {
			HARD.assertTrue(condition, message);
		} else {
			HARD.assertTrue(condition);
		}
	}

	protected static void saveFail(String message) {
		saveFail(message, null);
	}

	protected static void saveFail(String message, Throwable t) {
		Optional<String> opt = Optional.ofNullable(message);
		Optional<Throwable> optExc = Optional.ofNullable(t);

		if (opt.isPresent() && optExc.isPresent()) {
			SOFT.fail(opt.get(), optExc.get());
		} else if (opt.isPresent()) {
			SOFT.fail(opt.get());
		} else {
			SOFT.fail();
		}
	}

	protected static void saveAssertTrue(String message, boolean condition) {
		Optional<String> opt = Optional.ofNullable(message);
		if (opt.isPresent()) {
			SOFT.assertTrue(condition, message);
		} else {
			SOFT.assertTrue(condition);
		}
	}

	protected static void saveAssertEquals(int actual, int expected) {
		saveAssertEquals(null, actual, expected);
	}
	
	protected static void saveAssertEquals(String message, int actual, int expected) {
		Optional<String> opt = Optional.ofNullable(message);
		if (opt.isPresent()) {
			SOFT.assertEquals(actual, expected, message);
		} else {
			SOFT.assertEquals(actual, expected);
		}
	}
	
	protected static void saveAssertNotNull(Object object) {
		saveAssertNotNull(null, object);
	}
	
	protected static void saveAssertNotNull(String message, Object object) {
		Optional<String> opt = Optional.ofNullable(message);
		if (opt.isPresent()) {
			SOFT.assertNotNull(object, message);
		} else {
			SOFT.assertNotNull(object);
		}
	}
	
	protected static void assertNotNull(Object object) {
		assertNotNull(null ,object);
	}
	
	protected static void assertNotNull(String message, Object object) {
		Optional<String> opt = Optional.ofNullable(message);
		if (opt.isPresent()) {
			HARD.assertNotNull(object, message);
		} else {
			HARD.assertNotNull(object);
		}
	}
	
	// ~ Logging
	// ================================================================================================
	protected void setTestInfo(String msg) {
		Optional.ofNullable(msg).ifPresent(m -> {
			testLogger.info(msg);
			logToReport(msg, LogLevel.INFO);
		});
	}

	protected void setTestWarning(String msg) {
		Optional.ofNullable(msg).ifPresent(m -> {
			testLogger.warn(msg);
			logToReport(msg, LogLevel.WARNING);
		});
	}

	protected void setTestError(String message) {
		setTestError(message, null);
	}

	protected void setTestError(String msg, Throwable t) {
		Optional.ofNullable(msg).ifPresent(m -> {
			Optional<Throwable> opt = Optional.ofNullable(t);
			if (opt.isPresent()) {
				testLogger.error(m, opt.get());
			} else {
				testLogger.error(m);
			}
			logToReport(m, LogLevel.ERROR);
		});
	}

	private void logToReport(String msg, LogLevel logLevel) {
		Reporter.log(FORMAT.format(new Date()) + " " + logLevel + " " + msg + LINE_SEPARATOR);
	}
}
