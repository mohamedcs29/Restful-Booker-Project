package Listeners;

import io.qameta.allure.Allure;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import com.example.apitesting.utils.LogUtility;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class IInvokedMethodListenerImpl implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        LogUtility.info("Starting test method: " + method.getTestMethod().getMethodName());
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult, ITestContext context) {
        if (testResult.getStatus() == ITestResult.FAILURE) {
            LogUtility.error("Test Case " + testResult.getName() + " Failed");

            String failureLog = "Test Failed: " + testResult.getName() +
                    "\nReason: " + testResult.getThrowable();
            Allure.addAttachment("Failure Log", new ByteArrayInputStream(failureLog.getBytes(StandardCharsets.UTF_8)));
        }
    }
}
