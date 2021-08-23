package com.kubeworks.watcher.ecosystem.kubernetes.dto.crd;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import io.kubernetes.client.openapi.models.V1Container;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class V1ContainerExtends extends V1Container {

    public V1ContainerExtends(V1Container v1Container) {
        super();
        super.setArgs(v1Container.getArgs());
        super.setCommand(v1Container.getCommand());
        super.setEnv(v1Container.getEnv());
        super.setEnvFrom(v1Container.getEnvFrom());
        super.setImage(v1Container.getImage());
        super.setImagePullPolicy(v1Container.getImagePullPolicy());
        super.setLifecycle(v1Container.getLifecycle());
        super.setLivenessProbe(v1Container.getLivenessProbe());
        super.setName(v1Container.getName());
        super.setPorts(v1Container.getPorts());
        super.setReadinessProbe(v1Container.getReadinessProbe());
        super.setResources(v1Container.getResources());
        super.setSecurityContext(v1Container.getSecurityContext());
        super.setStartupProbe(v1Container.getStartupProbe());
        super.setStdin(v1Container.getStdin());
        super.setStdinOnce(v1Container.getStdinOnce());
        super.setTerminationMessagePath(v1Container.getTerminationMessagePath());
        super.setTerminationMessagePolicy(v1Container.getTerminationMessagePolicy());
        super.setTty(v1Container.getTty());
        super.setVolumeDevices(v1Container.getVolumeDevices());
        super.setVolumeMounts(v1Container.getVolumeMounts());
        super.setWorkingDir(v1Container.getWorkingDir());
    }

    String status = ExternalConstants.NONE;


    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }

        if (this.getClass() != o.getClass()) {
            return false;
        }

        V1ContainerExtends v1Container = (V1ContainerExtends) o;
        return super.equals(o) && Objects.equals(this.status, v1Container.status);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hash(
            super.getArgs(),
            super.getCommand(),
            super.getEnv(),
            super.getEnvFrom(),
            super.getImage(),
            super.getImagePullPolicy(),
            super.getLifecycle(),
            super.getLivenessProbe(),
            super.getName(),
            super.getPorts(),
            super.getReadinessProbe(),
            super.getResources(),
            super.getSecurityContext(),
            super.getStartupProbe(),
            super.getStdin(),
            super.getStdinOnce(),
            super.getTerminationMessagePath(),
            super.getTerminationMessagePolicy(),
            super.getTty(),
            super.getVolumeDevices(),
            super.getVolumeMounts(),
            super.getWorkingDir(),
            status
        );
    }
}
