package com.maff.codingstatisticmanager;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.EmptyModuleManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiEditorUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;
import java.util.Objects;

public class LOCStatistic extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());

        assert project != null;
//        CodingCounter.printProjectInfo(project);
//        printVisualFileInfo(e);
        LOCStatistic.selectedFilesInfo(project);
        LOCStatistic.psiElementsInfo(e);
    }

    private void printVisualFileInfo(@NotNull AnActionEvent e) {
        VirtualFile[] data = ((VirtualFile[]) e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE_ARRAY.getName()));
        assert data != null;
        VfsUtilCore.visitChildrenRecursively(data[0], new VirtualFileVisitor() {
            public boolean visitFile(@NotNull VirtualFile file) {

                String fileExtension = file.getExtension();
                System.out.println("文件扩展名： " + fileExtension + " 名称：" + file.getName() + " " + file.isDirectory()
                        + " file type：" + file.getFileType() + " " + file);
                if (!file.isDirectory()) {
                    LOCStatistic.this.getFileLines(file);
                }
                return file.isDirectory();
            }
        });
    }

    private void getFileLines(@NotNull VirtualFile file) {
        try {
            InputStream fileInputStream = file.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(new BufferedInputStream(fileInputStream));
            LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader);

            while (lineNumberReader.ready()) {
                String line;
                if ((line = lineNumberReader.readLine()) != null && line.trim().length() != 0) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printProjectInfo(@NotNull Project project) {
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
                return true;
            }
        });
        ProjectUtil.guessProjectDir(project);
        System.out.println("  file iterate content result: " + content);

        System.out.println("****************************************");
    }

    private static void psiElementsInfo(@NotNull AnActionEvent e) {
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        Project projectData = e.getData(CommonDataKeys.PROJECT);
        Project project1 = e.getProject();
        VirtualFile directoryData = PlatformDataKeys.PROJECT_FILE_DIRECTORY.getData(e.getDataContext());

        assert project != null;
        assert psiElement != null;

        System.out.println("project ==? " + (project == projectData));
        System.out.println("project ==? " + (project1 == projectData));
        PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());

        psiDirectory.accept(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
                System.out.println("element1: " + element);
            }
        });
        System.out.println("---------------------------------------");

        psiDirectory.acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
                System.out.println("element2: " + element);
            }
        });

        System.out.println("---------------------------------------");

        psiDirectory.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
                if(element instanceof PsiFile || element instanceof PsiDirectory){
                    System.out.println("element3: " + element + "  " + element.getLanguage());
                }
            }
        });

        System.out.println("---------------------------------------");
        psiElement.accept(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                super.visitElement(element);
                if(element instanceof PsiFile || element instanceof PsiDirectory){
                    System.out.println("current element4: " + element + "  " + element.getLanguage());
                }
            }

            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);
                System.out.println("current file : " + file);
            }
        });
        System.out.println("---------------------------------------");

        List<PyClass> childrenOfTypeAsList = PsiTreeUtil.getChildrenOfTypeAsList(psiElement, PyClass.class);

        System.out.println("psi element contain file: " + psiElement.getContainingFile());
        for (PyClass element : childrenOfTypeAsList) {
            System.out.println(element.getName());
        }
    }

    private static void selectedFilesInfo(@NotNull Project project) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile[] selectedFiles = fileEditorManager.getSelectedFiles();
        FileEditor[] selectedEditors = fileEditorManager.getSelectedEditors();

        System.out.println("----------------------------------------");
        System.out.println("selected files: ");
        for (VirtualFile selectedFile : selectedFiles) {
            System.out.println("selected file: " + selectedFile);
        }
        System.out.println("----------------------------------------");

        System.out.println("selected editor: ");
        for (FileEditor selectedEditor : selectedEditors) {
            System.out.println("selected editor: " + selectedEditor);
        }
        System.out.println("----------------------------------------");
    }

    private void printVirtualFile(VirtualFile virtualFile) {

    }
}

class GetPsiFromActionTestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        PsiFile psiFile = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        assert psiFile != null;
        FileViewProvider viewProvider = psiFile.getViewProvider();
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
        PsiFile[] psiFiles = FilenameIndex.getFilesByName(project, "test", GlobalSearchScope.everythingScope(project));
        FileViewProvider viewProvider = psiFiles[0].getViewProvider();
//        viewProvider.getPsi(StdLanguages.)
        System.out.println(psiFiles[0].getFileType());
        System.out.println(psiFiles[0].getLanguage());
        psiFiles[0].accept(new PsiElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                System.out.println(element.getText());
                super.visitElement(element);
            }
        });
    }

}

