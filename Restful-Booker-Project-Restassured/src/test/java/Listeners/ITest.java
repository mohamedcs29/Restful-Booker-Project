package Listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;
import com.example.apitesting.utils.LogUtility;

public class ITest implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        LogUtility.info("Test Case " + result.getName() + " started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogUtility.info("Test Case " + result.getName() + " passed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogUtility.info("Test Case " + result.getName() + " skipped");
    }


}
