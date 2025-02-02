package de.php_perfect.intellij.ddev.state;

import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import de.php_perfect.intellij.ddev.cmd.Description;
import de.php_perfect.intellij.ddev.cmd.MockProcessExecutor;
import de.php_perfect.intellij.ddev.cmd.ProcessExecutor;
import de.php_perfect.intellij.ddev.cmd.Versions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

final class DdevStateManagerTest extends BasePlatformTestCase {
    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    void initialize() {
        String expectedWhich = "which";
        if (SystemInfo.isWindows) {
            expectedWhich = "where";
        }

        final Project project = this.getProject();
        final MockDdevConfigLoader ddevConfigLoader = (MockDdevConfigLoader) DdevConfigLoader.getInstance(project);
        final MockProcessExecutor mockProcessExecutor = (MockProcessExecutor) ApplicationManager.getApplication().getService(ProcessExecutor.class);
        mockProcessExecutor.addProcessOutput(expectedWhich + " ddev", new ProcessOutput("/foo/bar/bin/ddev", "", 0, false, false));

        ddevConfigLoader.setExists(true);
        this.prepareCommand("ddev version --json-output", "src/test/resources/ddev_version.json");
        this.prepareCommand("ddev describe --json-output", "src/test/resources/ddev_describe.json");

        DdevStateManager ddevStateManager = DdevStateManager.getInstance(project);
        AtomicBoolean runnableExecuted = new AtomicBoolean(false);
        ddevStateManager.initialize(() -> runnableExecuted.set(true));

        StateImpl expectedState = new StateImpl();
        expectedState.setDdevBinary("/foo/bar/bin/ddev");
        expectedState.setConfigured(true);
        expectedState.setVersions(new Versions("v1.19.0"));
        expectedState.setDescription(new Description("acol", "8.1", Description.Status.STOPPED, null, null, new HashMap<>(), null));

        Assertions.assertEquals(expectedState, ddevStateManager.getState());
        Assertions.assertTrue(runnableExecuted.get());
    }

    @Test
    void updateDescription() {
        String expectedWhich = "which";
        if (SystemInfo.isWindows) {
            expectedWhich = "where";
        }

        final Project project = this.getProject();
        final MockDdevConfigLoader ddevConfigLoader = (MockDdevConfigLoader) DdevConfigLoader.getInstance(project);
        MockProcessExecutor mockProcessExecutor = (MockProcessExecutor) ApplicationManager.getApplication().getService(ProcessExecutor.class);
        mockProcessExecutor.addProcessOutput(expectedWhich + " ddev", new ProcessOutput("/foo/bar/bin/ddev", "", 0, false, false));

        ddevConfigLoader.setExists(true);
        this.prepareCommand("ddev version --json-output", "src/test/resources/ddev_version.json");
        this.prepareCommand("ddev describe --json-output", "src/test/resources/ddev_describe.json");

        DdevStateManager ddevStateManager = DdevStateManager.getInstance(this.getProject());
        ddevStateManager.initialize(null);

        StateImpl expectedState = new StateImpl();
        expectedState.setDdevBinary("/foo/bar/bin/ddev");
        expectedState.setConfigured(true);
        expectedState.setVersions(new Versions("v1.19.0"));
        expectedState.setDescription(new Description("acol", "8.1", Description.Status.STOPPED, null, null, new HashMap<>(), null));

        Assertions.assertEquals(expectedState, ddevStateManager.getState());

        this.prepareCommand("ddev describe --json-output", "src/test/resources/ddev_describe2.json");

        ddevStateManager.updateDescription();

        expectedState.setDescription(new Description("acol", "7.4", Description.Status.STOPPED, null, null, new HashMap<>(), null));

        Assertions.assertEquals(expectedState, ddevStateManager.getState());
    }

    private void prepareCommand(String command, String file) {
        MockProcessExecutor mockProcessExecutor = (MockProcessExecutor) ApplicationManager.getApplication().getService(ProcessExecutor.class);

        ProcessOutput processOutput = null;
        try {
            processOutput = new ProcessOutput(Files.readString(Path.of(file)), "", 0, false, false);
        } catch (IOException e) {
            Assertions.fail(e);
        }

        mockProcessExecutor.addProcessOutput(command, processOutput);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        final MockDdevConfigLoader ddevConfigLoader = (MockDdevConfigLoader) DdevConfigLoader.getInstance(this.getProject());
        ddevConfigLoader.setExists(false);

        DdevStateManager.getInstance(this.getProject()).resetState();

        super.tearDown();
    }
}
