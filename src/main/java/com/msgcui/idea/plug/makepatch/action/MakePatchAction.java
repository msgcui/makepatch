package com.msgcui.idea.plug.makepatch.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.msgcui.idea.plug.makepatch.component.MakePatchState;
import com.msgcui.idea.plug.makepatch.core.MakePatchService;
import com.msgcui.idea.plug.makepatch.core.impl.DefaultMakePatchService;

public class MakePatchAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        //获取当前在操作的工程上下文
        Project project = event.getData(PlatformDataKeys.PROJECT);

        MakePatchService service = new DefaultMakePatchService();
        try {
            String result = service.makePatch(MakePatchState.getInstance(), event);
            //显示对话框
            Messages.showMessageDialog(project, result, "补丁抽取结果", Messages.getInformationIcon());
        }catch(Exception e){
            //显示对话框
            Messages.showMessageDialog(project, "补丁抽取失败："+e.getMessage(), "补丁抽取结果", Messages.getInformationIcon());
        }
    }
}
