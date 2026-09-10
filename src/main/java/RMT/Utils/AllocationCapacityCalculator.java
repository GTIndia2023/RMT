package RMT.Utils;

/**
 * Utility methods for allocation availability calculations derived from BRD/FRD rules.
 */
public final class AllocationCapacityCalculator {

    private static final double HALF_DAY_BASE_HOURS = 4.0;

    private AllocationCapacityCalculator() {
    }

    public static double calculateTotalCapacity(double baseCapacityHours, double appendedCapacityHours) {
        validateNonNegative(baseCapacityHours, "baseCapacityHours");
        validateNonNegative(appendedCapacityHours, "appendedCapacityHours");
        return roundToTwoDecimals(baseCapacityHours + appendedCapacityHours);
    }

    public static double calculateAvailableHours(double baseCapacityHours,
                                                 double appendedCapacityHours,
                                                 double leaveHours,
                                                 double allocationHours,
                                                 double holidayHours) {
        validateNonNegative(leaveHours, "leaveHours");
        validateNonNegative(allocationHours, "allocationHours");
        validateNonNegative(holidayHours, "holidayHours");

        double totalCapacity = calculateTotalCapacity(baseCapacityHours, appendedCapacityHours);
        double rawAvailability = totalCapacity - (leaveHours + allocationHours + holidayHours);
        return clampToZero(rawAvailability);
    }

    public static double calculateBaseAvailability(double baseCapacityHours,
                                                   double leaveHours,
                                                   double allocationHours,
                                                   double holidayHours) {
        validateNonNegative(baseCapacityHours, "baseCapacityHours");
        validateNonNegative(leaveHours, "leaveHours");
        validateNonNegative(allocationHours, "allocationHours");
        validateNonNegative(holidayHours, "holidayHours");

        double rawAvailability = baseCapacityHours - (leaveHours + allocationHours + holidayHours);
        return clampToZero(rawAvailability);
    }

    public static double calculateHalfDayAvailability(double allocationHours) {
        validateNonNegative(allocationHours, "allocationHours");
        return clampToZero(HALF_DAY_BASE_HOURS - allocationHours);
    }

    public static boolean canAllocateRequestedHours(double requestedHours, double balanceCapacityHours) {
        validateNonNegative(requestedHours, "requestedHours");
        validateNonNegative(balanceCapacityHours, "balanceCapacityHours");
        return requestedHours <= balanceCapacityHours;
    }

    public static boolean isExtendedUtilizationTriggered(double requestedHours,
                                                         double baseAvailableHours,
                                                         boolean appendedCapacityEnabled) {
        validateNonNegative(requestedHours, "requestedHours");
        validateNonNegative(baseAvailableHours, "baseAvailableHours");
        return appendedCapacityEnabled && requestedHours > baseAvailableHours;
    }

    private static double clampToZero(double value) {
        return roundToTwoDecimals(Math.max(0.0, value));
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative. Actual value: " + value);
        }
    }
}
