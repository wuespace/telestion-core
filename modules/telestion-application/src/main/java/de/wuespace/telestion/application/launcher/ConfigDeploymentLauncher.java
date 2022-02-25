package de.wuespace.telestion.application.launcher;

/**
 * This launcher type combines the functionality of a {@link ConfigLauncher} type
 * and a {@link DeploymentLauncher} type.
 *
 * @author Ludwig Richter (@fussel178)
 */
public interface ConfigDeploymentLauncher<T> extends ConfigLauncher<T>, DeploymentLauncher {
}
