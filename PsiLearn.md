# Intellij Psi系统 学习  
  Psi是Program Structure Interface的缩写，即程序结构接口。在Intellij平台中，
  Psi负责解析文件并支持平台各种功能的语法和语义的代码模型。  
  [PSI官方文档](http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi.html)
  
 # 1、PSI File（PSI文件）  
一个PSI文件是表示文件内容的结构的根，作为特定编程语言中的元素层次的结构。  

类[PsiFile](https://upsource.jetbrains.com/idea-ce/file/idea-ce-e97504227f5f68c58cd623c8f317a134b6d440b5/platform/core-api/src/com/intellij/psi/PsiFile.java)
是所有PSI File的公共基类，特定语言的file通常由其子类表示。
比如 
类[PyFile](https://upsource.jetbrains.com/idea-ce/file/idea-ce-e97504227f5f68c58cd623c8f317a134b6d440b5/python/psi-api/src/com/jetbrains/python/psi/PyFile.java)
代表一个python文件，
类[PsiJava](https://upsource.jetbrains.com/idea-ce/file/idea-ce-e97504227f5f68c58cd623c8f317a134b6d440b5/java/java-psi-api/src/com/intellij/psi/PsiJavaFile.java)
代表一个Java文件，
类[XmlFile](https://upsource.jetbrains.com/idea-ce/file/idea-ce-e97504227f5f68c58cd623c8f317a134b6d440b5/xml/xml-psi-api/src/com/intellij/psi/xml/XmlFile.java)
代表一个XML文件。  

与[VirtualFIle](https://upsource.jetbrains.com/idea-ce/file/idea-ce-e97504227f5f68c58cd623c8f317a134b6d440b5/platform/core-api/src/com/intellij/openapi/vfs/VirtualFile.java)
和[Document](https://upsource.jetbrains.com/idea-ce/file/idea-ce-e97504227f5f68c58cd623c8f317a134b6d440b5/platform/core-api/src/com/intellij/openapi/editor/Document.java)
拥有application级的范围(即使代开了多个项目，每个文件还是有同一个VirtualFIle实例表示)不同，
PSI的范围是Project级的，即使文件属于同时打开的多个项目，则该文件由多个PsiFile实例表示。

# 2、如何获取一个PSI File实例  
- 从一个action获取：e.getData(LangDataKeys.PSI_FILE) 在较新的版本中用e.getData(CommonDataKeys.PSI_FILE)， 例子:  
``` Java
class GetPsiFromActionTestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
    }
}
```
- 从一个VirtualFile中获取：PsiManager.getInstance(project).findFile(VirtualFile), 例子：  
``` Java
class GetPsiFromVirtualTestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // TODO: insert action logic here
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        assert project != null;
        PsiManager.getInstance(project).findFile(Objects.requireNonNull(project.getProjectFile()));
    }
}
```
 - 从一个Document中获取：PsiDocumentManager.getInstance(project).getPsiFile(Document), 例子：  
 ``` Java
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
```
 - 从文件内的元素中获取：psiElement.getContainingFile(), 例子：  
 ``` Java
psiElement.getContainingFile()
```


