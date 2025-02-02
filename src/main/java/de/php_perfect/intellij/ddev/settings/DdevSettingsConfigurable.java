package de.php_perfect.intellij.ddev.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import de.php_perfect.intellij.ddev.DdevIntegrationBundle;
import de.php_perfect.intellij.ddev.notification.DdevNotifier;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class DdevSettingsConfigurable implements Configurable {
    private DdevSettingsComponent ddevSettingsComponent;

    private final @NotNull Project project;

    public DdevSettingsConfigurable(@NotNull Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return DdevIntegrationBundle.message("settings.title");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return this.ddevSettingsComponent.getPreferredFocusedComponent();
    }

    @Override
    public JComponent createComponent() {
        this.ddevSettingsComponent = new DdevSettingsComponent();

        return this.ddevSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        DdevSettingsState settings = DdevSettingsState.getInstance(this.project);
        boolean modified = this.ddevSettingsComponent.getCheckForUpdatedStatus() != settings.checkForUpdates;
        modified |= this.ddevSettingsComponent.getWatchDdevCheckboxStatus() != settings.watchDdev;
        modified |= this.ddevSettingsComponent.getAutoConfigureDataSource() != settings.autoConfigureDataSource;
        modified |= this.ddevSettingsComponent.getAutoConfigurePhpInterpreter() != settings.autoConfigurePhpInterpreter;

        return modified;
    }

    @Override
    public void apply() {
        DdevSettingsState settings = DdevSettingsState.getInstance(this.project);
        settings.checkForUpdates = this.ddevSettingsComponent.getCheckForUpdatedStatus();
        settings.watchDdev = this.ddevSettingsComponent.getWatchDdevCheckboxStatus();
        settings.autoConfigureDataSource = this.ddevSettingsComponent.getAutoConfigureDataSource();
        settings.autoConfigurePhpInterpreter = this.ddevSettingsComponent.getAutoConfigurePhpInterpreter();

        DdevNotifier.getInstance(this.project).asyncNotifyRestartAfterSettingsChange();
    }

    @Override
    public void reset() {
        DdevSettingsState settings = DdevSettingsState.getInstance(this.project);
        this.ddevSettingsComponent.setCheckForUpdatesStatus(settings.checkForUpdates);
        this.ddevSettingsComponent.setWatchDdevCheckboxStatus(settings.watchDdev);
        this.ddevSettingsComponent.setAutoConfigureDataSource(settings.autoConfigureDataSource);
        this.ddevSettingsComponent.setAutoConfigurePhpInterpreter(settings.autoConfigurePhpInterpreter);
    }

    @Override
    public void disposeUIResources() {
        this.ddevSettingsComponent = null;
    }
}
