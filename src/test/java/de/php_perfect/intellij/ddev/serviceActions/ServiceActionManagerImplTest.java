package de.php_perfect.intellij.ddev.serviceActions;

import com.intellij.ide.x.a.U;
import com.intellij.openapi.actionSystem.ActionManager;
import de.php_perfect.intellij.ddev.actions.OpenServiceAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceActionManagerImplTest {
    @Test
    @DisplayName("should update actions")
    public void testUpdateActionsByDescription() throws MalformedURLException {
        var serviceActionManager = new ServiceActionManagerImpl(() -> mock(ActionManager.class));

    //    serviceActionManager.updateActionsByDescription();

    //    assertThat(serviceActionManager.getServiceActions()).containsExactly()

    }

    @Test
    @DisplayName("should update actions registering / unregistering")
    public void testUpdateActionsByDescriptionCallsRegisterUnregister() {

    }
}
