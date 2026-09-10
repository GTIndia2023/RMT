package RMT.Models;

public class AllocationValidationRow {
    private final String recordKey;
    private final double baseCapacityHours;
    private final double appendedCapacityHours;
    private final double leaveHours;
    private final double allocationHours;
    private final double holidayHours;
    private final double uiAvailableHours;

    public AllocationValidationRow(String recordKey,
                                   double baseCapacityHours,
                                   double appendedCapacityHours,
                                   double leaveHours,
                                   double allocationHours,
                                   double holidayHours,
                                   double uiAvailableHours) {
        this.recordKey = recordKey;
        this.baseCapacityHours = baseCapacityHours;
        this.appendedCapacityHours = appendedCapacityHours;
        this.leaveHours = leaveHours;
        this.allocationHours = allocationHours;
        this.holidayHours = holidayHours;
        this.uiAvailableHours = uiAvailableHours;
    }

    public String getRecordKey() {
        return recordKey;
    }

    public double getBaseCapacityHours() {
        return baseCapacityHours;
    }

    public double getAppendedCapacityHours() {
        return appendedCapacityHours;
    }

    public double getLeaveHours() {
        return leaveHours;
    }

    public double getAllocationHours() {
        return allocationHours;
    }

    public double getHolidayHours() {
        return holidayHours;
    }

    public double getUiAvailableHours() {
        return uiAvailableHours;
    }
}
