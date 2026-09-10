package com.qa.rmt.MyTest;

import RMT.Pages.AllocationModuleFlowPage;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Covers the end-to-end allocation lifecycle by delegating the workflow to the page-layer orchestration class.
 */
public class AllocationModulePOMTest extends BaseTest {

    @Test(priority = 1)
    @Description("Executes full allocation lifecycle: allocate from ResourceReq2, approve from Resource5 task, reopen allocation, and release resource.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    /**
     * Verifies that the allocation lifecycle completes successfully from requestor allocation through release.
     */
    public void verifyAllocateEmployeeEndToEndFlowTest() {
        AllocationModuleFlowPage.AllocationLifecycleResult result =
                new AllocationModuleFlowPage(driver, prop).executeEndToEndFlow();

        Assert.assertTrue(result.isRequestorAllocationValidated(), result.getAllocationSummary());
        Assert.assertTrue(result.isTaskApprovalValidated(), result.getTaskSummary());
        Assert.assertTrue(result.isReleaseValidated(), result.getReleaseSummary());
        Assert.assertTrue(result.isFlowSuccessful(), result.getFailureSummary());
    }
}
