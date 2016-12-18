package hu.bme.msc.onlab.trhandlertool.test;

import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import hu.bme.msc.onlab.framework.FrameworkTestCase;
import hu.bme.msc.onlab.framework.configuration.TestConfiguration;
import hu.bme.msc.onlab.framework.entity.SUT;
import hu.bme.msc.onlab.framework.util.SUTHolder;
import hu.bme.msc.onlab.trhandlertool.interf.SupplierWithException;
import hu.bme.msc.onlab.trhandlertool.util.PropertyKey;
import hu.bme.msc.onlab.trhandlertool.util.ResponseDto;

public abstract class AbstractTestCase extends FrameworkTestCase {

	protected static final String HTTP = TestConfiguration.getInstance().getProperty(PropertyKey.HTTP_KEY);

	protected final SUT STANDALONE = SUTHolder.getIstance().getSUT();

	private static final String EXCEPTION_MESSAGE = "Exception happened during operation!";

	protected ResponseDto<URL> getTargetedURL(SUT sut, String page) {
		final String host = sut.getHost().getHostAddress();
		final int port = sut.getPort();
		final String project = sut.getProject();
		return tryThis(() -> {
			return ResponseDto.ok(new URL(HTTP + host + ":" + port + "/" + project + "/" + page));
		});
	}

	protected boolean checkPageTitle(HtmlPage page, String title) {
		setTestInfo("Checking page: " + page.getUrl().toString() + " title: " + title);
		return title.equals(page.getTitleText());
	}
	
	protected List<? extends Object> getPageElementsByXPath(HtmlPage page, String xPath) {
		setTestInfo("Getting page: " + page.getUrl().toString() + " ,elements by xPath: " + xPath);
		return page.getByXPath(xPath);
	}
	
	@BeforeMethod(alwaysRun = true)
	protected void abstractSetUp() {
	}
	
	@AfterMethod(alwaysRun = true)
	protected void abstractTearDown() {
	}
	
	protected void saveAssertTrue(ResponseDto<?> response) {
		saveAssertTrue(StringUtils.EMPTY, response);
	}

	protected void saveAssertTrue(String message, ResponseDto<?> response) {
		if (!response.isSuccess()) {
			saveFail(String.valueOf(message) + " [" + response.getReason() + "]", response.getException());
		}
	}

	protected void assertTrue(ResponseDto<?> response) {
		assertTrue(StringUtils.EMPTY, response);
	}

	protected void assertTrue(String message, ResponseDto<?> response) {
		if (!response.isSuccess()) {
			fail(String.valueOf(message) + " [" + response.getReason() + "]", response.getException());
		}
	}

	protected static <T, R> ResponseDto<R> tryThis(Supplier<Function<T, ResponseDto<R>>> supplier, T value) {
		final Function<T, ResponseDto<R>> operation = supplier.get();
		return tryThis(operation, value);
	}

	protected static <T, R> ResponseDto<R> tryThis(Function<T, ResponseDto<R>> operation, T value) {
		try {
			return operation.apply(value);
		} catch (Exception e) {
			return ResponseDto.fail(EXCEPTION_MESSAGE, e);
		}
	}

	protected static <T> ResponseDto<T> tryThis(SupplierWithException<ResponseDto<T>> supplier) {
		try {
			return supplier.get();
		} catch (Exception e) {
			return ResponseDto.fail(EXCEPTION_MESSAGE, e);
		}
	}
}
