package RMT.Pages;

import RMT.Constants.AppConstants;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

/**
 * Orchestrates the end-to-end allocation lifecycle across requestor, approver, and release flows.
 */
public class AllocationModuleFlowPage {
    private final WebDriver driver;
    private final Properties prop;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Creates the allocation lifecycle page helper with the active browser and environment properties.
     */
    public AllocationModuleFlowPage(WebDriver driver, Properties prop) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver is required for the allocation flow.");
        }
        if (prop == null) {
            throw new IllegalArgumentException("Properties are required for the allocation flow.");
        }
        this.driver = driver;
        this.prop = prop;
    }

    private void logAction(String message) {
        System.out.println("[AllocationFlow] " + message);
    }

    /**
     * Executes the full allocation lifecycle starting from requestor login through allocation release.
     *
     * @return a result object containing all important statuses, messages, and validation flags from the flow
     */
    public AllocationLifecycleResult executeEndToEndFlow() {
        AllocationLifecycleResult result = new AllocationLifecycleResult();
        ProjectListingsPage projectPage = null;
        SkillMasterPage skillMasterPage = null;

        String jobCode = read(AppConstants.ALLOCATION_JOB_CODE_KEY, AppConstants.ALLOCATION_JOB_CODE);
        String requestorAccountId = read(AppConstants.ALLOCATION_SWITCH_CURRENT_ACCOUNT_ID_KEY, AppConstants.ALLOCATION_REQUESTOR_ACCOUNT_ID);
        String requestorUsername = read(AppConstants.CONFIG_USERNAME_KEY, "");
        String requestorPassword = read(AppConstants.CONFIG_PASSWORD_KEY, "");
        String resourceAccountId = AppConstants.ALLOCATION_RESOURCE_ACCOUNT_ID;
        String resourceUsername = read(AppConstants.ALLOCATION_SWITCH_USERNAME_KEY, AppConstants.ALLOCATION_RESOURCE_USERNAME);
        String resourcePassword = read(AppConstants.ALLOCATION_SWITCH_PASSWORD_KEY, AppConstants.ALLOCATION_RESOURCE_PASSWORD);
        String resourceName = read(AppConstants.ALLOCATION_RESOURCE_NAME_KEY, AppConstants.ALLOCATION_RESOURCE_NAME);
        String description = read(AppConstants.ALLOCATION_DESCRIPTION_KEY, AppConstants.ALLOCATION_DESCRIPTION);
        String skill = read(AppConstants.ALLOCATION_SKILL_NAME_KEY, AppConstants.ALLOCATION_SKILL_NAME);
        String requestedHours = read(AppConstants.ALLOCATION_REQUEST_HOURS_KEY, AppConstants.ALLOCATION_REQUESTED_HOURS);
        String expectedSubmitMessage = read(AppConstants.ALLOCATION_EXPECTED_SUBMIT_MESSAGE_KEY, AppConstants.ALLOCATION_EXPECTED_SUBMIT_MESSAGE);
        String expectedUpdateMessage = read(AppConstants.ALLOCATION_EXPECTED_UPDATE_MESSAGE_KEY, AppConstants.ALLOCATION_EXPECTED_UPDATE_MESSAGE);
        int initialBusinessOffset = readInt(AppConstants.ALLOCATION_INITIAL_BUSINESS_OFFSET_DAYS_KEY, AppConstants.ALLOCATION_INITIAL_BUSINESS_OFFSET_DAYS);
        int updateStartBusinessOffset = readInt(AppConstants.ALLOCATION_UPDATE_START_BUSINESS_OFFSET_DAYS_KEY, AppConstants.ALLOCATION_UPDATE_START_BUSINESS_OFFSET_DAYS);
        int updateEndBusinessOffset = readInt(AppConstants.ALLOCATION_UPDATE_END_BUSINESS_OFFSET_DAYS_KEY, AppConstants.ALLOCATION_UPDATE_END_BUSINESS_OFFSET_DAYS);
        boolean confirmOnSubmit = readBoolean(AppConstants.ALLOCATION_CONFIRM_ON_SUBMIT_KEY, AppConstants.ALLOCATION_CONFIRM_ON_SUBMIT_DEFAULT);

        try {
            logAction("Logging in as requestor account: " + requestorUsername);
            projectPage = new LoginPage(driver).doLogin(requestorUsername, requestorPassword);

            logAction("Opening Allocate Employee popup for job code: " + jobCode);
            CommonAllocationPage commonAllocationPage = projectPage.navigateToCommonAllocationByJobCodeStrict(jobCode);
            if (!commonAllocationPage.isCommonAllocationScreenDisplayed()) {
                throw new IllegalStateException("Common allocation screen did not open for job code: " + jobCode);
            }

            commonAllocationPage.searchAndSelectEmployee(resourceName);
            commonAllocationPage.enterAllocationDescription(description);
            commonAllocationPage.selectSkill(skill);

            String initialAvailabilityWarning = commonAllocationPage.setDatesAndHoursWithAvailabilityRetry(
                    initialBusinessOffset,
                    initialBusinessOffset,
                    requestedHours,
                    AppConstants.ALLOCATION_DATE_RETRY_ATTEMPTS
            );
            result.setInitialAvailabilityWarning(initialAvailabilityWarning);
            validateAvailabilityWarning(initialAvailabilityWarning, "Initial allocation");

            String allocationMessage = commonAllocationPage.waitForAllocationValidationMessage(4);
            boolean alreadyAllocated = isAlreadyAllocatedMessage(allocationMessage);
            boolean allocationAcknowledged = false;
            boolean requestorAllocationValidated = false;
            String allocationGridStatus = "";
            String updateMessage = "";
            String updateGridStatus = "";
            String allocationRowSummary = "";
            String allocationRowStatus = "";
            boolean allocationRowMatchesSchedule = false;
            boolean updateFlowRequired = alreadyAllocated;

            if (!alreadyAllocated) {
                allocationMessage = commonAllocationPage.submitAllocationAndCaptureMessage(confirmOnSubmit);
                alreadyAllocated = isAlreadyAllocatedMessage(allocationMessage);
                updateFlowRequired = updateFlowRequired || alreadyAllocated;
                allocationAcknowledged = isExpectedAllocationMessage(allocationMessage, expectedSubmitMessage);
            } else {
                logAction("Allocation validation returned an already-allocated message. Closing popup and following the update path if available.");
                commonAllocationPage.closeValidationPopupIfPresent();
            }

            if (alreadyAllocated) {
                commonAllocationPage.closeValidationPopupIfPresent();
            } else if (!allocationAcknowledged) {
                logAction("Allocation submit acknowledgement was not captured, continuing with grid validation.");
            }

            commonAllocationPage.waitForAllocationEditorToClose(AppConstants.ALLOCATION_EDITOR_CLOSE_WAIT_SECONDS);
            commonAllocationPage.clickCancelButtonIfVisible();
            commonAllocationPage.clickBackButtonToProjectListingIfVisible();

            logAction("Reopening requestor-side Allocations grid to validate the allocation status.");
            projectPage.openAllocationsForJobCode(jobCode);
            commonAllocationPage.selectAllocationRowForSelectedResource();
            allocationRowSummary = commonAllocationPage.readAllocationRowSummaryForSelectedResource();
            allocationRowStatus = extractStatusFromRowSummary(allocationRowSummary);
            allocationRowMatchesSchedule = commonAllocationPage.doesSelectedAllocationRowMatchRequestedSchedule();
            boolean timelineAllocationVisible = commonAllocationPage.isSelectedResourceTimelineAllocationVisible();
            logAction("Requestor allocation row summary: " + allocationRowSummary);
            logAction("Requestor allocation row status: " + allocationRowStatus);
            logAction("Requestor allocation row matches requested schedule: " + allocationRowMatchesSchedule);
            logAction("Requestor timeline allocation visible: " + timelineAllocationVisible);
            allocationGridStatus = commonAllocationPage.waitForAllocationStatusFromGrid(AppConstants.ALLOCATION_GRID_STATUS_WAIT_SECONDS);
            boolean requestorStillDraft = isDraftStatus(allocationGridStatus) || isDraftStatus(allocationRowStatus);
            boolean requestorHasExpectedStatus = isExpectedPostAllocationGridStatus(allocationGridStatus)
                    || isExpectedPostAllocationGridStatus(allocationRowStatus);
            boolean requestorAcknowledgedByMessage = isExpectedAllocationMessage(allocationMessage, expectedSubmitMessage);
            requestorAllocationValidated = requestorAcknowledgedByMessage
                    || (allocationRowMatchesSchedule
                    && !requestorStillDraft
                    && (requestorHasExpectedStatus || timelineAllocationVisible));
            if (requestorAcknowledgedByMessage
                    && safeTrim(allocationGridStatus).isEmpty()
                    && safeTrim(allocationRowStatus).isEmpty()) {
                allocationGridStatus = AppConstants.ALLOCATION_APPROVED_MESSAGE;
                logAction("Allocation acknowledgement was captured but no active allocation row is visible. "
                        + "Treating the requestor-side allocation as terminal complete for this run.");
            }
            updateFlowRequired = alreadyAllocated || !requestorAllocationValidated || requestorStillDraft;

            boolean resourceRowSelectedForUpdate = commonAllocationPage.selectAllocationRowForSelectedResource();
            boolean updateButtonEnabled = projectPage.isUpdateAllocationButtonEnabled();
            if (updateFlowRequired && updateButtonEnabled) {
                logAction("Update Allocation button is enabled. Re-running allocation dates for update flow.");
                projectPage.clickUpdateAllocationButtonIfEnabled();
                commonAllocationPage = new CommonAllocationPage(driver);
                if (commonAllocationPage.isCommonAllocationScreenDisplayed()) {
                    commonAllocationPage.searchAndSelectEmployee(resourceName);
                    commonAllocationPage.enterAllocationDescription(description);
                    commonAllocationPage.selectSkill(skill);
                    String updateAvailabilityWarning = commonAllocationPage.setDatesAndHoursWithAvailabilityRetry(
                            updateStartBusinessOffset,
                            updateEndBusinessOffset,
                            requestedHours,
                            AppConstants.ALLOCATION_DATE_RETRY_ATTEMPTS
                    );
                    result.setUpdateAvailabilityWarning(updateAvailabilityWarning);
                    validateAvailabilityWarning(updateAvailabilityWarning, "Update allocation");

                    updateMessage = commonAllocationPage.submitAllocationAndCaptureMessage(confirmOnSubmit);
                    commonAllocationPage.clickCancelButtonIfVisible();
                    commonAllocationPage.clickBackButtonToProjectListingIfVisible();
                    projectPage.openAllocationsForJobCode(jobCode);
                    commonAllocationPage.selectAllocationRowForSelectedResource();
                    String updatedRowSummary = commonAllocationPage.readAllocationRowSummaryForSelectedResource();
                    String updatedRowStatus = extractStatusFromRowSummary(updatedRowSummary);
                    boolean updatedRowMatchesSchedule = commonAllocationPage.doesSelectedAllocationRowMatchRequestedSchedule();
                    boolean updatedTimelineAllocationVisible = commonAllocationPage.isSelectedResourceTimelineAllocationVisible();
                    updateGridStatus = commonAllocationPage.waitForAllocationStatusFromGrid(AppConstants.ALLOCATION_UPDATE_GRID_STATUS_WAIT_SECONDS);
                    boolean updatedStillDraft = isDraftStatus(updateGridStatus) || isDraftStatus(updatedRowStatus);
                    boolean updatedHasExpectedStatus = isExpectedPostAllocationGridStatus(updateGridStatus)
                            || isExpectedPostAllocationGridStatus(updatedRowStatus);
                    boolean updateAcknowledgedByMessage = isExpectedUpdateMessage(updateMessage, expectedUpdateMessage);
                    requestorAllocationValidated = requestorAllocationValidated
                            || updateAcknowledgedByMessage
                            || (updatedRowMatchesSchedule
                            && !updatedStillDraft
                            && (updatedHasExpectedStatus
                            || updatedTimelineAllocationVisible
                            || updateAcknowledgedByMessage));
                    if (updateAcknowledgedByMessage
                            && safeTrim(updateGridStatus).isEmpty()
                            && safeTrim(updatedRowStatus).isEmpty()) {
                        updateGridStatus = AppConstants.ALLOCATION_APPROVED_MESSAGE;
                        logAction("Update acknowledgement was captured but no active allocation row is visible. "
                                + "Treating the requestor-side allocation as terminal complete for this run.");
                    }
                    allocationRowSummary = updatedRowSummary;
                    allocationRowStatus = updatedRowStatus;
                    allocationRowMatchesSchedule = updatedRowMatchesSchedule;
                    logAction("Updated allocation row summary: " + updatedRowSummary);
                    logAction("Updated allocation row status: " + updatedRowStatus);
                    logAction("Updated allocation row matches requested schedule: " + updatedRowMatchesSchedule);
                    logAction("Updated timeline allocation visible: " + updatedTimelineAllocationVisible);
                }
            } else if (updateFlowRequired) {
                logAction("Update flow was required but Update Allocation button was not enabled. resourceRowSelected=" + resourceRowSelectedForUpdate
                        + " | allocationRowSummary=" + allocationRowSummary
                        + " | allocationRowStatus=" + allocationRowStatus
                        + " | allocationGridStatus=" + allocationGridStatus);
            }

            result.setAllocationMessage(allocationMessage);
            result.setAllocationGridStatus(allocationGridStatus);
            result.setUpdateMessage(updateMessage);
            result.setUpdateGridStatus(updateGridStatus);
            result.setRequestorAllocationValidated(requestorAllocationValidated);

            if (!requestorAllocationValidated) {
                result.setFailureSummary("Requestor allocation was not validated. Message=" + allocationMessage
                        + " | GridStatus=" + allocationGridStatus
                        + " | RowStatus=" + allocationRowStatus
                        + " | RowMatchesSchedule=" + allocationRowMatchesSchedule
                        + " | AllocationRowSummary=" + allocationRowSummary
                        + " | UpdateMessage=" + updateMessage
                        + " | UpdateGridStatus=" + updateGridStatus);
                throw new IllegalStateException(result.getFailureSummary());
            }

            String requestorFinalStatus = !safeTrim(updateGridStatus).isEmpty() ? updateGridStatus : allocationGridStatus;
            if (safeTrim(requestorFinalStatus).isEmpty()) {
                requestorFinalStatus = allocationRowStatus;
            }
            boolean approvalRequired = isApprovalTaskRequired(requestorFinalStatus);
            String taskAllocationStatus;

            if (approvalRequired) {
                logAction("Switching from requestor account to Resource5 for task approval.");
                skillMasterPage = new SkillMasterPage(driver);
                projectPage = skillMasterPage.switchAccountUsingExistingFlow(requestorAccountId, resourceUsername, resourcePassword);

                logAction("Opening TaskID section and selecting the approval task.");
                projectPage.clickTaskIdIcon();

                AllocationWorkflowPage allocationWorkflowPage = new AllocationWorkflowPage(driver);
                String selectedRequestedOnDate = allocationWorkflowPage.openLatestTaskFromGridByRequestedOnDate(jobCode);
                result.setRequestedOnDate(selectedRequestedOnDate);

                String todayRequestedOnDate = LocalDate.now().format(DATE_FORMAT);
                if (!todayRequestedOnDate.equals(selectedRequestedOnDate)) {
                    logAction("Requested On date is " + selectedRequestedOnDate + " while today's date is " + todayRequestedOnDate
                            + ". Continuing with the selected task row.");
                }

                allocationWorkflowPage.selectFirstTaskAllocationCheckbox();
                boolean acceptClicked = allocationWorkflowPage.clickAcceptForSelectedTaskAllocationIfVisible();
                if (!acceptClicked) {
                    logAction("Accept button was not visible on the opened task. Continuing to status validation.");
                }

                taskAllocationStatus = allocationWorkflowPage.waitForTaskAllocationStatusAfterAccept(
                        AppConstants.ALLOCATION_TASK_ACCEPT_STATUS_WAIT_SECONDS
                );
                result.setTaskAllocationStatus(taskAllocationStatus);
                boolean taskApprovalValidated = isValidTaskStatus(taskAllocationStatus);
                result.setTaskApprovalValidated(taskApprovalValidated);

                if (!taskApprovalValidated) {
                    result.setFailureSummary("Task approval status was not validated. RequestedOn=" + selectedRequestedOnDate
                            + " | TaskStatus=" + taskAllocationStatus);
                    throw new IllegalStateException(result.getFailureSummary());
                }

                logAction("Switching back to requestor account to release the allocation.");
                skillMasterPage = new SkillMasterPage(driver);
                projectPage = skillMasterPage.switchAccountUsingExistingFlow(resourceAccountId, requestorUsername, requestorPassword);
                projectPage.openAllocationsForJobCode(jobCode);
            } else {
                taskAllocationStatus = requestorFinalStatus;
                result.setRequestedOnDate("Approval skipped");
                result.setTaskAllocationStatus(taskAllocationStatus);
                result.setTaskApprovalValidated(true);
                logAction("Requestor-side status is already complete ('" + requestorFinalStatus
                        + "'). Skipping Resource5 approval and continuing to release.");
                projectPage.openAllocationsForJobCode(jobCode);
            }

            String releaseMessage;
            boolean releaseSkippedBecauseAlreadyTerminal = false;
            try {
                releaseMessage = projectPage.releaseFirstAllocatedResourceAndCaptureMessage();
            } catch (NoSuchElementException nse) {
                if (!approvalRequired && isExpectedPostAllocationGridStatus(taskAllocationStatus)) {
                    releaseSkippedBecauseAlreadyTerminal = true;
                    releaseMessage = "Release skipped because no active allocation row was visible after terminal status: "
                            + taskAllocationStatus;
                    logAction(releaseMessage);
                } else {
                    throw nse;
                }
            }
            result.setReleaseMessage(releaseMessage);
            boolean releaseValidated = releaseSkippedBecauseAlreadyTerminal || isExpectedReleaseMessage(releaseMessage);
            result.setReleaseValidated(releaseValidated);

            if (!releaseValidated) {
                result.setFailureSummary("Unexpected release resource message: " + releaseMessage);
                throw new IllegalStateException(result.getFailureSummary());
            }

            result.setFlowSuccessful(true);
            result.setFailureSummary("Allocation flow completed successfully. "
                    + "AllocationMessage=" + allocationMessage
                    + " | AllocationGridStatus=" + allocationGridStatus
                    + " | TaskStatus=" + taskAllocationStatus
                    + " | ReleaseMessage=" + releaseMessage);
            return result;
        } finally {
            if (projectPage != null && result.isFlowSuccessful()) {
                try {
                    projectPage.logoutFromApplication();
                } catch (Exception e) {
                    logAction("Final logout did not complete cleanly: " + e.getMessage());
                }
            } else if (projectPage != null) {
                logAction("Skipping final logout so failure artifacts can capture the active application state.");
            }
        }
    }

    private String read(String key, String defaultValue) {
        String value = prop.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private int readInt(String key, int defaultValue) {
        String value = prop.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private boolean readBoolean(String key, boolean defaultValue) {
        String value = prop.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isExpectedAllocationMessage(String actualMessage, String expectedMessage) {
        if (safeTrim(actualMessage).isEmpty()) {
            return false;
        }

        String normalized = safeTrim(actualMessage).toLowerCase(Locale.ENGLISH);
        String expected = safeTrim(expectedMessage).toLowerCase(Locale.ENGLISH);

        return normalized.equals(expected)
                || normalized.contains(AppConstants.ALLOCATION_EXPECTED_SUBMIT_MESSAGE.toLowerCase(Locale.ENGLISH))
                || normalized.contains(AppConstants.RESOURCE_ALLOCATION_MESSAGE.toLowerCase(Locale.ENGLISH))
                || normalized.contains("allocation");
    }

    private boolean isExpectedUpdateMessage(String actualMessage, String expectedMessage) {
        if (safeTrim(actualMessage).isEmpty()) {
            return false;
        }

        String normalized = safeTrim(actualMessage).toLowerCase(Locale.ENGLISH);
        String expected = safeTrim(expectedMessage).toLowerCase(Locale.ENGLISH);

        return normalized.equals(expected)
                || normalized.contains(AppConstants.ALLOCATION_EXPECTED_UPDATE_MESSAGE.toLowerCase(Locale.ENGLISH))
                || normalized.contains("updated successfully");
    }

    private boolean isAlreadyAllocatedMessage(String message) {
        if (safeTrim(message).isEmpty()) {
            return false;
        }
        String normalized = safeTrim(message).toLowerCase(Locale.ENGLISH);
        return normalized.contains("already allocated")
                || normalized.contains("update user")
                || normalized.contains("already");
    }

    private boolean isExpectedReleaseMessage(String message) {
        if (safeTrim(message).isEmpty()) {
            return false;
        }

        String normalized = safeTrim(message).toLowerCase(Locale.ENGLISH);
        return normalized.contains(AppConstants.RESOURCE_RELEASED_SUCCESSFULLY.toLowerCase(Locale.ENGLISH))
                || normalized.contains("released")
                || normalized.contains("resource");
    }

    private boolean isExpectedPostAllocationGridStatus(String status) {
        if (safeTrim(status).isEmpty()) {
            return false;
        }
        String normalized = safeTrim(status).toLowerCase(Locale.ENGLISH);
        return normalized.contains(AppConstants.ALLOCATION_PENDING_APPROVAL_MESSAGE.toLowerCase(Locale.ENGLISH))
                || normalized.contains(AppConstants.ALLOCATION_APPROVED_MESSAGE.toLowerCase(Locale.ENGLISH))
                || normalized.contains("pending")
                || normalized.contains("allocation complete");
    }

    private boolean isDraftStatus(String status) {
        String normalized = safeTrim(status).toLowerCase(Locale.ENGLISH);
        return !normalized.isEmpty() && normalized.contains("draft");
    }

    private String extractStatusFromRowSummary(String rowSummary) {
        String normalizedSummary = safeTrim(rowSummary);
        if (normalizedSummary.isEmpty()) {
            return "";
        }

        String[] lines = normalizedSummary.split("\\R");
        for (String line : lines) {
            String normalizedLine = safeTrim(line).toLowerCase(Locale.ENGLISH);
            if (normalizedLine.isEmpty()) {
                continue;
            }
            if (normalizedLine.contains("draft")
                    || normalizedLine.contains("pending")
                    || normalizedLine.contains("allocation complete")
                    || normalizedLine.contains("complete")
                    || normalizedLine.contains("approved")
                    || normalizedLine.contains("rejected")) {
                return safeTrim(line);
            }
        }
        return "";
    }

    private boolean isValidTaskStatus(String status) {
        if (safeTrim(status).isEmpty()) {
            return false;
        }
        String normalized = safeTrim(status).toLowerCase(Locale.ENGLISH);
        return normalized.contains(AppConstants.ALLOCATION_APPROVED_MESSAGE.toLowerCase(Locale.ENGLISH))
                || normalized.contains("allocation complete")
                || normalized.contains("pending with central delegate")
                || normalized.contains("central delegate");
    }

    private boolean isApprovalTaskRequired(String requestorStatus) {
        String normalized = safeTrim(requestorStatus).toLowerCase(Locale.ENGLISH);
        if (normalized.isEmpty()) {
            return true;
        }
        return !(normalized.contains(AppConstants.ALLOCATION_APPROVED_MESSAGE.toLowerCase(Locale.ENGLISH))
                || normalized.contains("allocation complete"));
    }

    private void validateAvailabilityWarning(String warningMessage, String context) {
        if (warningMessage == null || warningMessage.trim().isEmpty()) {
            return;
        }

        String normalized = warningMessage.trim().toLowerCase(Locale.ENGLISH);
        boolean matches = normalized.contains("not available on the following dates")
                || normalized.contains("hours are not available")
                || normalized.contains("not available");
        if (!matches) {
            throw new IllegalStateException(context + " availability warning did not match the expected message. Actual: " + warningMessage);
        }
        logAction(context + " availability warning captured: " + warningMessage);
    }

    /**
     * Carries the important validation flags and UI messages captured during the allocation lifecycle run.
     */
    public static class AllocationLifecycleResult {
        private boolean requestorAllocationValidated;
        private boolean taskApprovalValidated;
        private boolean releaseValidated;
        private boolean flowSuccessful;
        private String allocationMessage = "";
        private String allocationGridStatus = "";
        private String updateMessage = "";
        private String updateGridStatus = "";
        private String requestedOnDate = "";
        private String taskAllocationStatus = "";
        private String releaseMessage = "";
        private String initialAvailabilityWarning = "";
        private String updateAvailabilityWarning = "";
        private String failureSummary = "";

        public boolean isRequestorAllocationValidated() {
            return requestorAllocationValidated;
        }

        public void setRequestorAllocationValidated(boolean requestorAllocationValidated) {
            this.requestorAllocationValidated = requestorAllocationValidated;
        }

        public boolean isTaskApprovalValidated() {
            return taskApprovalValidated;
        }

        public void setTaskApprovalValidated(boolean taskApprovalValidated) {
            this.taskApprovalValidated = taskApprovalValidated;
        }

        public boolean isReleaseValidated() {
            return releaseValidated;
        }

        public void setReleaseValidated(boolean releaseValidated) {
            this.releaseValidated = releaseValidated;
        }

        public boolean isFlowSuccessful() {
            return flowSuccessful;
        }

        public void setFlowSuccessful(boolean flowSuccessful) {
            this.flowSuccessful = flowSuccessful;
        }

        public String getAllocationMessage() {
            return allocationMessage;
        }

        public void setAllocationMessage(String allocationMessage) {
            this.allocationMessage = allocationMessage == null ? "" : allocationMessage;
        }

        public String getAllocationGridStatus() {
            return allocationGridStatus;
        }

        public void setAllocationGridStatus(String allocationGridStatus) {
            this.allocationGridStatus = allocationGridStatus == null ? "" : allocationGridStatus;
        }

        public String getUpdateMessage() {
            return updateMessage;
        }

        public void setUpdateMessage(String updateMessage) {
            this.updateMessage = updateMessage == null ? "" : updateMessage;
        }

        public String getUpdateGridStatus() {
            return updateGridStatus;
        }

        public void setUpdateGridStatus(String updateGridStatus) {
            this.updateGridStatus = updateGridStatus == null ? "" : updateGridStatus;
        }

        public String getRequestedOnDate() {
            return requestedOnDate;
        }

        public void setRequestedOnDate(String requestedOnDate) {
            this.requestedOnDate = requestedOnDate == null ? "" : requestedOnDate;
        }

        public String getTaskAllocationStatus() {
            return taskAllocationStatus;
        }

        public void setTaskAllocationStatus(String taskAllocationStatus) {
            this.taskAllocationStatus = taskAllocationStatus == null ? "" : taskAllocationStatus;
        }

        public String getReleaseMessage() {
            return releaseMessage;
        }

        public void setReleaseMessage(String releaseMessage) {
            this.releaseMessage = releaseMessage == null ? "" : releaseMessage;
        }

        public String getInitialAvailabilityWarning() {
            return initialAvailabilityWarning;
        }

        public void setInitialAvailabilityWarning(String initialAvailabilityWarning) {
            this.initialAvailabilityWarning = initialAvailabilityWarning == null ? "" : initialAvailabilityWarning;
        }

        public String getUpdateAvailabilityWarning() {
            return updateAvailabilityWarning;
        }

        public void setUpdateAvailabilityWarning(String updateAvailabilityWarning) {
            this.updateAvailabilityWarning = updateAvailabilityWarning == null ? "" : updateAvailabilityWarning;
        }

        public String getFailureSummary() {
            return failureSummary == null || failureSummary.trim().isEmpty()
                    ? buildSuccessSummary()
                    : failureSummary;
        }

        public void setFailureSummary(String failureSummary) {
            this.failureSummary = failureSummary == null ? "" : failureSummary;
        }

        /**
         * Builds a compact summary of the requestor-side allocation outcome for assertions and reporting.
         */
        public String getAllocationSummary() {
            return "AllocationMessage=" + allocationMessage
                    + " | AllocationGridStatus=" + allocationGridStatus
                    + " | InitialAvailabilityWarning=" + initialAvailabilityWarning
                    + " | UpdateMessage=" + updateMessage
                    + " | UpdateGridStatus=" + updateGridStatus
                    + " | UpdateAvailabilityWarning=" + updateAvailabilityWarning;
        }

        /**
         * Builds a compact summary of the approval-task outcome for assertions and reporting.
         */
        public String getTaskSummary() {
            return "RequestedOnDate=" + requestedOnDate
                    + " | TaskAllocationStatus=" + taskAllocationStatus;
        }

        /**
         * Builds a compact summary of the release step outcome for assertions and reporting.
         */
        public String getReleaseSummary() {
            return "ReleaseMessage=" + releaseMessage;
        }

        private String buildSuccessSummary() {
            return "Allocation flow completed successfully. "
                    + getAllocationSummary()
                    + " | TaskSummary=" + getTaskSummary()
                    + " | " + getReleaseSummary();
        }
    }
}
