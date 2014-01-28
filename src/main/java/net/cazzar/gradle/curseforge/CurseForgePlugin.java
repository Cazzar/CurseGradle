package net.cazzar.gradle.curseforge;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class CurseForgePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        Task myTask = project.getTasks().create("uploadToCurseForge", CurseUpload.class);
        for (Task task : project.getTasksByName("reobf", false)) {
            myTask.dependsOn(task);
        }
    }
}
