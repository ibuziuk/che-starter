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
package io.fabric8.che.starter.model.workspace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkspaceEnvironment {

    private WorkspaceRecipe recipe;
    private Map<String, WorkspaceMachine> machines;

    public WorkspaceRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(WorkspaceRecipe recipe) {
        this.recipe = recipe;
    }

    public Map<String, WorkspaceMachine> getMachines() {
        return machines;
    }

    public void setMachines(Map<String, WorkspaceMachine> machines) {
        this.machines = machines;
    }

}
