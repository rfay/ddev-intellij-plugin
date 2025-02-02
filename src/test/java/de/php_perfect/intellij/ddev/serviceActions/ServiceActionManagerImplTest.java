package de.php_perfect.intellij.ddev.serviceActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.array;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import de.php_perfect.intellij.ddev.actions.OpenServiceAction;
import de.php_perfect.intellij.ddev.cmd.DatabaseInfo;
import de.php_perfect.intellij.ddev.cmd.DatabaseInfo.Type;
import de.php_perfect.intellij.ddev.cmd.Description;
import de.php_perfect.intellij.ddev.cmd.Description.Status;
import de.php_perfect.intellij.ddev.cmd.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServiceActionManagerImplTest {

    @Test
    @DisplayName("should update actions from given description")
    void testUpdateActionsByDescription() {
        var serviceActionManager = new ServiceActionManagerImpl(
            () -> mock(ActionManager.class),
            new HashMap<>(Map.of(
                "existingAction",
                anAction("Open existing action", "https://www.existing.com", "existing action")
            )));

        serviceActionManager.updateActionsByDescription(aDescription("test", Status.RUNNING));

        assertThat(serviceActionManager.getServiceActions()).isEqualTo(array(
            aMailhogAction(),
            anAction("Open test", "https://www.test.com", "Open test service in your browser")
        ));
    }

    @Test
    @DisplayName("should update actions registering / unregistering")
    void testUpdateActionsByDescriptionCallsRegisterUnregister() {
        var actionManager = mock(ActionManager.class);
        var serviceActionManager = new ServiceActionManagerImpl(
            () -> actionManager,
            new HashMap<>(Map.of(
                "existingAction",
                anAction("Open existing action", "https://www.existing.com", "existing action")
            )));

        serviceActionManager.updateActionsByDescription(aDescription("test", Status.RUNNING));

        verify(actionManager).unregisterAction("existingAction");
        verify(actionManager).registerAction(eq("DdevIntegration.Services.ddev-config-test-mailhog"), any(), any());
        verify(actionManager).registerAction(eq("DdevIntegration.Services.test"), any(), any());
    }

    private AnAction anAction(String displayText, String url, String description) {
        try {
            return new OpenServiceAction(new URL(url), displayText, description, AllIcons.General.Web);
        } catch (MalformedURLException urlException) {
            throw new RuntimeException(urlException);
        }
    }

    private AnAction aMailhogAction() {
        return anAction("Open Mailhog", "https://www.test.com",
            "Open ddev-config-test-mailhog service in your browser");
    }

    private Description aDescription(String name, Description.Status status) {
        var dataBaseInfo = new DatabaseInfo(Type.MYSQL, "5.7", 2133, "db", "localhost",
            "root", "root", 2133);

        var httpUrl = String.format("http://www.%s.com", name);
        var httpsUrl = String.format("https://www.%s.com", name);

        return new Description(name, "7.4", status, httpsUrl, httpUrl,
            Map.of(
                name, new Service(name, httpsUrl, httpUrl)
            ), dataBaseInfo);
    }
}
