package de.wuespace.telestion.application.launcher;

/**
 * This launcher combines the functionality of a {@link ConfigLauncher} and a {@link DeploymentLauncher}.
 *
 * @author Ludwig Richter (@fussel178)
 */
public interface ConfigDeploymentLauncher<T> extends ConfigLauncher<T>, DeploymentLauncher {
}
