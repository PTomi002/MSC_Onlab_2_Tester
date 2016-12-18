package hu.bme.msc.onlab.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.TestNGException;
import org.testng.xml.Parser;
import org.testng.xml.XmlSuite;

import com.beust.jcommander.internal.Lists;

import hu.bme.msc.onlab.framework.configuration.PropertyKey;
import hu.bme.msc.onlab.framework.configuration.TestConfiguration;
import hu.bme.msc.onlab.framework.entity.SUT;
import hu.bme.msc.onlab.framework.util.Closer;
import hu.bme.msc.onlab.framework.util.SUTHolder;

public class Main {
	private static final String MAIN_CLASS_DELIMETER = "<====================================>";
	private static final int EXIT_CODE = 1;
	private static final int INDEX_2 = 2;
	private static final int INDEX_1 = 1;
	private static final int INDEX_0 = 0;
	private static final int ARGS_NUMBER_EXPECTED = 3;
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private Main() {
		// Hiding constructor of Main
	}

	public static void main(String[] args) {
		try {
			LOGGER.info("Checking arguments");
			checkArgs(args, ARGS_NUMBER_EXPECTED);

			LOGGER.info("Reading arguments");
			final String testxmlFilePath = parseConfiguratonFile(args, INDEX_0);
			final String testConfigFilePath = parseConfiguratonFile(args, INDEX_1);

			LOGGER.info("Get test configuration files");
			InputStream testXmlFile = fetchTestConfigurationFile(testxmlFilePath);
			InputStream testConfigFile = fetchTestConfigurationFile(testConfigFilePath);

			LOGGER.info("Initialize test configuration");
			initTestConfiguration(testConfigFile);

			LOGGER.info("Initialize System Under Test");
			final TestConfiguration config = TestConfiguration.getInstance();
			SUT standalone = initSystemUnderTest(config);

			LOGGER.info("Storing SUT");
			SUTHolder.getInstance(standalone);

			LOGGER.info("Adding event listeners");
			initEventListeners(config);

			LOGGER.info("Parsing Test XML");
			List<XmlSuite> tests = parseTestXml(testXmlFile);
			Closer.close(Arrays.asList(testXmlFile, testConfigFile));

			LOGGER.info("Creating test output dir, if not exists");
			final String outputDir = parseTestOutputDir(args, INDEX_2);
			TestConfiguration.getInstance().put(PropertyKey.LOGDIR, outputDir);

			LOGGER.info("Configuring TestNG framework");
			TestNG testng = new TestNG();
			testng.setXmlSuites(tests);
			testng.setOutputDirectory(outputDir);
			testng.addListener(new FailureListener());

			LOGGER.info("Redirecting System out/err to the Loggers");
			redirectSystemOutput();

			LOGGER.info(MAIN_CLASS_DELIMETER);
			LOGGER.info("        Running Test Framework");
			LOGGER.info(MAIN_CLASS_DELIMETER);
			runTestEngine(testng);

			LOGGER.info(MAIN_CLASS_DELIMETER);
			LOGGER.info("             Shutting down");
			LOGGER.info(MAIN_CLASS_DELIMETER);

			if (testng.hasFailure()) {
				// Jenkins runs comands qith #!/bin/sh -ex
				// where x = means to print every command executed
				// and e = means to exit with failure if any of the commands in
				// the script failed
				System.exit(EXIT_CODE);
			}
		} catch (Exception e) {
			exitFromApplication("Unhandled failure case, development error!", e, EXIT_CODE);
		}
	}

	private static void runTestEngine(TestNG testng) {
		try {
			testng.run();
		} catch (TestNGException e) {
			LOGGER.error("TestNG exception happened!", e);
		}
	}

	private static void initEventListeners(final TestConfiguration config) {
		config.entrySet().stream().filter(e -> String.valueOf(e.getKey()).startsWith(PropertyKey.EVENT_LISTENER_PREFIX))
				.forEach(e -> {
					String listenerKey = (String) e.getKey();
					String listenerClass = (String) e.getValue();
					LOGGER.info("Adding event listeners (key: " + listenerKey + "): " + listenerClass);
				});
	}

	private static void redirectSystemOutput() {
		// Redirecting
		// org.testng.Reporter.log(String, boolean)
		System.setOut(new SystemOutputPrintStream(System.out));
		System.setErr(new SystemErrorPrintStream(System.err));
	}

	private static String parseTestOutputDir(String[] args, int index) {
		String path = StringUtils.EMPTY;
		try {
			LOGGER.info("Command line argument(" + index + "): " + args[index]);
			File outputDir = new File(args[index]);
			if (!outputDir.exists() && !outputDir.mkdirs()) {
				exitFromApplication("Could not create test resuts output dir!", EXIT_CODE);
			} else {
				LOGGER.info("Output dir exists, using it");
			}
			path = outputDir.getAbsolutePath();
		} catch (ArrayIndexOutOfBoundsException e) {
			exitFromApplication("Did not found command line argument at position: " + index, e, EXIT_CODE);
		}
		return path;
	}

	private static String parseConfiguratonFile(String[] args, int index) {
		String path = StringUtils.EMPTY;
		try {
			LOGGER.info("Command line argument(" + index + "): " + args[index]);
			File file = new File(args[index]);
			if (!file.exists()) {
				exitFromApplication("Test configuration file does not exist: " + args[index], EXIT_CODE);
			}
			path = file.getAbsolutePath();
		} catch (ArrayIndexOutOfBoundsException e) {
			exitFromApplication("Did not found command line argument at position: " + index, e, EXIT_CODE);
		}
		return path;
	}

	private static void checkArgs(String[] args, int expectedArgsNumber) {
		if (args.length != expectedArgsNumber) {
			exitFromApplication("Command line aruments are not matching, needed: " + expectedArgsNumber
					+ " ,but found: " + args.length, EXIT_CODE);
		}

		if (Arrays.asList(args).stream().anyMatch(arg -> {
			LOGGER.info("Command line argument: " + arg);
			return StringUtils.isEmpty(arg);
		})) {
			exitFromApplication("Found a null or empty command line argument!", EXIT_CODE);
		}
	}

	private static List<XmlSuite> parseTestXml(InputStream testXmlFile) {
		List<XmlSuite> result = Lists.newArrayList();
		try {
			result.addAll(new Parser(testXmlFile).parseToList());
		} catch (Exception e) {
			exitFromApplication("Could not parse Test.xml file!", e, EXIT_CODE);
		}
		return result;
	}

	private static InputStream fetchTestConfigurationFile(String filePath) {
		InputStream file = null;
		try {
			LOGGER.info("Get an input stream for file: " + filePath);
			file = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			exitFromApplication("Could not find file on path: " + filePath, e, EXIT_CODE);
		}
		return file;
	}

	private static SUT initSystemUnderTest(final TestConfiguration config) {
		try {
			final String host = config.getProperty(PropertyKey.HOST_DOMAIN);
			final Integer port = config.getIntegerProperty(PropertyKey.HOST_PORT);
			final String project = config.getProperty(PropertyKey.PROJECT_NAME);

			LOGGER.info("Checking host: " + host);
			InetAddress inetAddress = InetAddress.getByName(host);

			LOGGER.info("Checking port: " + port);
			new InetSocketAddress(inetAddress, port);

			LOGGER.info("Creating SUT...");
			final SUT sut = new SUT(inetAddress).setPort(port);

			LOGGER.info("Adding project: " + project);
			sut.setProject(project);

			LOGGER.info("Adding service URLs");
			TestConfiguration.getInstance().entrySet().stream()
					.filter(e -> String.valueOf(e.getKey()).startsWith(PropertyKey.URL_PREFIX)).forEach(e -> {
						String urlKey = (String) e.getKey();
						String url = (String) e.getValue();
						LOGGER.info("Adding SUT service URL (key: " + urlKey + "): " + url);
						sut.addSutUrl(urlKey, url);
					});
			return sut;
		} catch (UnknownHostException | IllegalArgumentException e) {
			exitFromApplication("Could not initialize System Under Test!", e, EXIT_CODE);
			return null;
		}
	}

	private static void initTestConfiguration(InputStream testConfigFile) {
		try {
			LOGGER.info("Loading test configuration");
			TestConfiguration.getInstance().load(testConfigFile);
		} catch (IOException | IllegalArgumentException e) {
			exitFromApplication("Could not get properties, bailing out...", e, EXIT_CODE);
		}
	}

	private static void exitFromApplication(String message, int exitCode) {
		exitFromApplication(message, null, exitCode);
	}

	private static void exitFromApplication(String message, Throwable exc, int exitCode) {
		Optional<Throwable> optional = Optional.ofNullable(exc);
		if (optional.isPresent()) {
			LOGGER.error(message, optional.get());
		} else {
			LOGGER.error(message);
		}
		System.exit(exitCode);
	}
}
