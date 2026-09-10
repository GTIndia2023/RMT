package com.qa.rmt.utils;

import RMT.Factory.DriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public final class AllureAttachmentHelper {

    private static final String FAILURE_ATTACHMENT_MARKER = "ALLURE_FAILURE_ATTACHMENTS_DONE";
    private static final String ALLURE_TEST_CASE_UUID = "ALLURE_TEST_CASE_UUID";
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Path ALLURE_RESULTS_DIR = Paths.get("allure-results");

    private AllureAttachmentHelper() {
    }

    public static void attachFailureArtifacts(ITestResult result, WebDriver preferredDriver) {
        if (result == null || Boolean.TRUE.equals(result.getAttribute(FAILURE_ATTACHMENT_MARKER))) {
            return;
        }

        WebDriver driver = preferredDriver != null ? preferredDriver : DriverManager.getDriver();
        if (driver == null) {
            System.out.println("[AllureAttachmentHelper] No active driver available for failure artifacts.");
            return;
        }

        String testName = safeTestMethodName(result);
        String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP);
        Path artifactDir = Paths.get("test_Logs", "failure-artifacts");
        boolean attachmentAdded = false;

        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            attachmentAdded = attachToAllure(result, "Failure screenshot - " + testName, "image/png", ".png", screenshot)
                    || attachmentAdded;
            writeArtifact(artifactDir.resolve(sanitizeForFileName(testName + "_" + timestamp) + ".png"), screenshot);
        } catch (Exception screenshotException) {
            System.out.println("[AllureAttachmentHelper] Unable to capture screenshot for " + testName
                    + " because: " + screenshotException.getMessage());
        }

        try {
            String pageSource = driver.getPageSource();
            byte[] html = pageSource.getBytes(StandardCharsets.UTF_8);
            attachmentAdded = attachToAllure(result, "Failure page source - " + testName, "text/html", ".html", html)
                    || attachmentAdded;
            writeArtifact(artifactDir.resolve(sanitizeForFileName(testName + "_" + timestamp) + ".html"), html);
        } catch (Exception pageSourceException) {
            System.out.println("[AllureAttachmentHelper] Unable to capture page source for " + testName
                    + " because: " + pageSourceException.getMessage());
        }

        if (attachmentAdded) {
            result.setAttribute(FAILURE_ATTACHMENT_MARKER, Boolean.TRUE);
        }
    }

    private static boolean attachToAllure(ITestResult result, String name, String type, String extension, byte[] content) {
        if (content == null || content.length == 0) {
            return false;
        }

        try {
            String storedUuid = result == null ? null : safeTrim((String) result.getAttribute(ALLURE_TEST_CASE_UUID));
            if (!storedUuid.isEmpty()) {
                Files.createDirectories(ALLURE_RESULTS_DIR);
                String normalizedExtension = extension == null ? "" : extension.trim();
                if (!normalizedExtension.isEmpty() && !normalizedExtension.startsWith(".")) {
                    normalizedExtension = "." + normalizedExtension;
                }
                String source = UUID.randomUUID() + "-attachment" + normalizedExtension;
                Files.write(ALLURE_RESULTS_DIR.resolve(source), content);

                Attachment attachment = new Attachment();
                attachment.setName(name);
                attachment.setType(type);
                attachment.setSource(source);

                Allure.getLifecycle().updateTestCase(storedUuid, testResult -> testResult.getAttachments().add(attachment));
                System.out.println("[AllureAttachmentHelper] Attached directly to Allure test uuid=" + storedUuid + ": " + name);
                return true;
            }

            Optional<String> currentUuid = Allure.getLifecycle().getCurrentTestCase();
            if (currentUuid.isPresent()) {
                Files.createDirectories(ALLURE_RESULTS_DIR);
                String normalizedExtension = extension == null ? "" : extension.trim();
                if (!normalizedExtension.isEmpty() && !normalizedExtension.startsWith(".")) {
                    normalizedExtension = "." + normalizedExtension;
                }
                String source = UUID.randomUUID() + "-attachment" + normalizedExtension;
                Files.write(ALLURE_RESULTS_DIR.resolve(source), content);

                Attachment attachment = new Attachment();
                attachment.setName(name);
                attachment.setType(type);
                attachment.setSource(source);

                Allure.getLifecycle().updateTestCase(currentUuid.get(), testResult -> testResult.getAttachments().add(attachment));
                System.out.println("[AllureAttachmentHelper] Attached directly to current Allure test uuid=" + currentUuid.get() + ": " + name);
                return true;
            }

            Allure.addAttachment(name, type, new ByteArrayInputStream(content),
                    extension == null ? "" : extension.replaceFirst("^\\.", ""));
            System.out.println("[AllureAttachmentHelper] Attached to current Allure fixture/context: " + name);
            return true;
        } catch (Exception e) {
            System.out.println("[AllureAttachmentHelper] Failed to attach to Allure for " + name + ": " + e.getMessage());
            return false;
        }
    }

    private static void writeArtifact(Path target, byte[] content) {
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content);
            System.out.println("[AllureAttachmentHelper] Wrote failure artifact: " + target.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("[AllureAttachmentHelper] Failed to write artifact " + target + ": " + e.getMessage());
        }
    }

    private static String safeTestMethodName(ITestResult result) {
        if (result == null || result.getMethod() == null || result.getMethod().getMethodName() == null) {
            return "UnknownTest";
        }
        String methodName = result.getMethod().getMethodName().trim();
        return methodName.isEmpty() ? "UnknownTest" : methodName;
    }

    private static String sanitizeForFileName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "UnknownTest";
        }
        return value.trim().replaceAll("[\\\\/:*?\"<>|]+", "_").replaceAll("\\s+", " ");
    }

    private static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
