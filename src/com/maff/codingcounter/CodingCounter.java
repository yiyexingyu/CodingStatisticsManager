package com.maff.codingcounter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.EmptyModuleManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.Calendar;
import java.util.Objects;

public class CodingCounter extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
//        DatabaseStatsRepository databaseStatsRepository = DatabaseStatsRepository.getInstance();
//        System.out.println(databaseStatsRepository.getConnectMessage());
//
//        databaseStatsRepository.selectLastPeriodStats(Period.今天);
//
//        databaseStatsRepository.disconnectDatabase();
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        CodingCounter.printProjectInfo(project);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        System.out.println("moth: " + instance.get(Calendar.MONTH));
        System.out.println("week of year: " + instance.get(Calendar.WEEK_OF_YEAR));
        System.out.println("week of moth: " + instance.get(Calendar.WEEK_OF_MONTH));
    }

    public static void printProjectInfo(Project project){
        System.out.println("****************project************************");
        System.out.println("project name: " + project.getName());
        System.out.println("BasePath: " + project.getBasePath());
        System.out.println("ProjectFilePath: " + project.getProjectFilePath());
        System.out.println("WorkspaceFile: " + project.getWorkspaceFile());
        System.out.println("LocationHash: " + project.getLocationHash());
        System.out.println("PresentableUrl: " + project.getPresentableUrl());

        // 输出project的modules
        Module[] modules = EmptyModuleManager.getInstance(project).getModules();
        System.out.println("******************modules**********************");
        for (Module module : modules) {
            System.out.println("ModuleFilePath" + module.getModuleFilePath());
            System.out.println("ModuleTypeName" + module.getModuleTypeName());
            System.out.println("Module name" + module.getName());
        }

        System.out.println("  ******************files********************");
        // 输出project的文件夹和文件

        boolean content = ProjectFileIndex.SERVICE.getInstance(project).iterateContent(new ContentIterator() {
            @Override
            public boolean processFile(VirtualFile virtualFile) {
                System.out.println("     ************************************");
                System.out.println("     file name: " + virtualFile.getName());
                System.out.println("     file Path: " + virtualFile.getPath());
                System.out.println("     file isDirectory: " + virtualFile.isDirectory());
                System.out.println("     file Url: " + virtualFile.getUrl());
                if(virtualFile.isDirectory()){
                    return true;
                }
                return false;
            }
        });
        ProjectUtil.guessProjectDir(project);
        System.out.println("  file iterate content result: " + content);

        System.out.println("****************************************");
    }
}

class GetPsiFromActionTestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
    }
}
class PsiGetFromVirtualTestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        assert project != null;
        PsiManager.getInstance(project).findFile(Objects.requireNonNull(project.getProjectFile()));
    }

}
class PsiGetFromDocumentTestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        Editor editorData = CommonDataKeys.EDITOR.getData(e.getDataContext());
        assert project != null;
        assert editorData != null;
        PsiDocumentManager.getInstance(project).getPsiFile(editorData.getDocument());
    }

}

