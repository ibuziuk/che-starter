/*-
 * #%L
 * che-starter
 * %%
 * Copyright (C) 2017 Red Hat, Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package io.fabric8.che.starter.util;

import io.fabric8.che.starter.model.workspace.*;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkspaceLegacyFormatAdapter {

    private static final String RUNTIME = "/runtime";
    private static final String SNAPSHOT = "/snapshot";

    /**
     * Takes the new format and returns the legacy format with all the necessary
     * data and links fabricated from what is returned by new che server
     * @param workspaceV6 Che 6 compatible Workspace response model
     * @return Che 5 compatible Workspace response model
     */
    public static Workspace getWorkspaceLegacyFormat(final WorkspaceV6 workspaceV6) {
        Workspace response = new Workspace();
        response.setConfig(convertConfigToLegacy(workspaceV6.getConfig()));
        response.setId(workspaceV6.getId());
        response.setRuntime(workspaceV6.getRuntime());
        response.setStatus(workspaceV6.getStatus());
        response.setLinks(convertLinksToLegacy(workspaceV6));
        return response;
    }

    /**
     * Takes <self>, <ide> and <channel> links, adding or subtracting necessary constants to get
     * all the links used by Che 5 server
     * As new links are returned as a <Map><String, String></Map>, this function iterates through
     * the keys and fabricates all the legacy data from sources that are present
     * @param workspaceV6 Che 6 Workspace response model
     * @return <List><WorkspaceLink></List> Arraylist of legacy formated links
     */
    private static List<WorkspaceLink> convertLinksToLegacy(WorkspaceV6 workspaceV6) {
        Map<String, String> links = workspaceV6.getLinks();
        List<WorkspaceLink> response = new ArrayList<>();
        links.forEach((key, value) -> {
            switch (key) {
                case "self":
                    WorkspaceLink self = new WorkspaceLink();
                    WorkspaceLink start = new WorkspaceLink();
                    WorkspaceLink remove = new WorkspaceLink();
                    WorkspaceLink getAllWorkspaces = new WorkspaceLink();
                    WorkspaceLink getSnapshot = new WorkspaceLink();

                    self.setHref(value);
                    self.setRel("self link");
                    self.setMethod(HttpMethod.GET.name().toUpperCase());

                    start.setHref(value + RUNTIME);
                    start.setRel("start workspace");
                    start.setMethod(HttpMethod.POST.name().toUpperCase());

                    remove.setHref(value);
                    remove.setRel("remove workspace");
                    remove.setMethod(HttpMethod.DELETE.name().toUpperCase());

                    getAllWorkspaces.setHref(value.substring(0, value.length() - (workspaceV6.getId().length() + 1)));
                    getAllWorkspaces.setRel("get all user workspaces");
                    getAllWorkspaces.setMethod(HttpMethod.GET.name().toUpperCase());

                    getSnapshot.setHref(value + SNAPSHOT);
                    getSnapshot.setRel("get workspace snapshot");
                    getSnapshot.setMethod(HttpMethod.GET.name().toUpperCase());

                    response.add(self);
                    response.add(start);
                    response.add(remove);
                    response.add(getAllWorkspaces);
                    response.add(getSnapshot);
                    break;
                case "ide":
                    WorkspaceLink ide = new WorkspaceLink();
                    ide.setHref(value);
                    ide.setRel("ide url");
                    ide.setMethod(HttpMethod.GET.name().toUpperCase());
                    response.add(ide);
                    break;
                case "environment/outputChannel":
                    WorkspaceLink outputChannel = new WorkspaceLink();
                    WorkspaceLink getWorkspaceEvents = new WorkspaceLink();

                    outputChannel.setHref(value);
                    outputChannel.setRel("environment.output_channel");
                    outputChannel.setMethod(HttpMethod.GET.name().toUpperCase());

                    getWorkspaceEvents.setHref(value);
                    getWorkspaceEvents.setRel("get workspace events channel");
                    getWorkspaceEvents.setMethod(HttpMethod.GET.name().toUpperCase());

                    response.add(outputChannel);
                    response.add(getWorkspaceEvents);
                    break;
                case "environment/statusChannel":
                    WorkspaceLink statusChannel = new WorkspaceLink();
                    statusChannel.setHref(value);
                    statusChannel.setRel("environment.status_channel");
                    statusChannel.setMethod(HttpMethod.GET.name().toUpperCase());
                    response.add(statusChannel);
                    break;
                default:
                    break;
            }
        });
        return response;
    }

    /**
     * Layout of config has changed in V6, this method takes new config and maps it to the legacy format
     * @param configV6 New config from response
     * @return legacy config format
     */
    private static WorkspaceConfig convertConfigToLegacy(WorkspaceConfigV6 configV6) {
        WorkspaceConfig response = new WorkspaceConfig();
        response.setName(configV6.getName());
        response.setDescription(configV6.getDescription());
        response.setDefaultEnv(configV6.getDefaultEnv());
        response.setLinks(configV6.getLinks());
        response.setCommands(configV6.getCommands());
        response.setProjects(configV6.getProjects());

        Map<String, WorkspaceEnvironmentV6> configV6Environments = configV6.getEnvironments();
        if (configV6Environments != null) {
            Map<String, WorkspaceEnvironment> environments = new HashMap<>();
            WorkspaceEnvironmentV6 configV6DefaultEnvironment = configV6Environments.get(configV6.getDefaultEnv());
            if (configV6DefaultEnvironment != null) {
                WorkspaceEnvironment defaultEnv = new WorkspaceEnvironment();
                Map<String, WorkspaceMachineV6> configV6Machines = configV6DefaultEnvironment.getMachines();
                if (configV6Machines != null) {
                    Map<String, WorkspaceMachine> machines = new HashMap<>();
                    configV6Machines.forEach((name, machine) -> {
                        WorkspaceMachine tmp = new WorkspaceMachine();
                        tmp.setAttributes(machine.getAttributes());
                        tmp.setAgents(machine.getInstallers());
                        machines.put(name, tmp);
                    });
                    defaultEnv.setMachines(machines);
                }
                defaultEnv.setRecipe(configV6DefaultEnvironment.getRecipe());
                environments.put(configV6.getDefaultEnv(),defaultEnv);
            }
            response.setEnvironments(environments);
        }

        return response;
    }

}
