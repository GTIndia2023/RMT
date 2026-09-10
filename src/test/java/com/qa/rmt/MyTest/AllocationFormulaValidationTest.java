package com.qa.rmt.MyTest;

import RMT.Utils.AllocationCapacityCalculator;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Covers pure formula validation scenarios for availability, half-day logic, and balance-capacity rules.
 */
public class AllocationFormulaValidationTest {

    @DataProvider(name = "availabilityScenarios")
    /**
     * Supplies representative availability-calculation scenarios for the pure formula test coverage.
     */
    public Object[][] availabilityScenarios() {
        return new Object[][]{
                {"Base only (8h), no leave, no holiday, no allocation", 8, 0, 0, 0, 0, 8},
                {"Base+Appended (8+4), allocation 9", 8, 4, 0, 9, 0, 3},
                {"Base+Appended (8+4), overallocated 15 -> non-negative", 8, 4, 0, 15, 0, 0},
                {"With leave and holiday", 8, 4, 2, 5, 1, 4},
                {"Full day leave consumes whole base day", 8, 0, 8, 0, 0, 0}
        };
    }

    @Test(dataProvider = "availabilityScenarios")
    /**
     * Verifies that the availability formula returns the expected value for the supplied scenario.
     */
    public void validateAvailabilityFormula(String scenario,
                                            double baseCapacity,
                                            double appendedCapacity,
                                            double leaveHours,
                                            double allocationHours,
                                            double holidayHours,
                                            double expectedAvailability) {
        double actualAvailability = AllocationCapacityCalculator.calculateAvailableHours(
                baseCapacity,
                appendedCapacity,
                leaveHours,
                allocationHours,
                holidayHours
        );

        Assert.assertEquals(actualAvailability, expectedAvailability, 0.01, "Scenario failed: " + scenario);
    }

    @Test
    /**
     * Verifies the half-day availability calculation rules used by the allocation capacity helper.
     */
    public void validateHalfDayLeaveRules() {
        Assert.assertEquals(AllocationCapacityCalculator.calculateHalfDayAvailability(0), 4.0, 0.01);
        Assert.assertEquals(AllocationCapacityCalculator.calculateHalfDayAvailability(2), 2.0, 0.01);
        Assert.assertEquals(AllocationCapacityCalculator.calculateHalfDayAvailability(4), 0.0, 0.01);
        Assert.assertEquals(AllocationCapacityCalculator.calculateHalfDayAvailability(5), 0.0, 0.01);
    }

    @Test
    /**
     * Verifies that extended utilization is triggered when requested effort exceeds base availability.
     */
    public void validateExtendedUtilizationTrigger() {
        boolean triggered = AllocationCapacityCalculator.isExtendedUtilizationTriggered(10, 8, true);
        Assert.assertTrue(triggered, "Extended utilization should trigger when requested hours exceed base available hours.");
    }

    @Test
    /**
     * Verifies that requests within balance capacity are allowed and over-capacity requests are blocked.
     */
    public void validateRequestMustNotExceedBalanceCapacity() {
        Assert.assertTrue(AllocationCapacityCalculator.canAllocateRequestedHours(11, 12),
                "Request within balance capacity should be allowed.");

        Assert.assertFalse(AllocationCapacityCalculator.canAllocateRequestedHours(13, 12),
                "Request above balance capacity should be blocked.");
    }
}
