package com.msgcui.idea.plug.makepatch.core;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import com.msgcui.idea.plug.makepatch.component.MakePatchState;

/**
 * 补丁抽取接口
 */
public interface MakePatchService {

    /**
     * 制作补丁
     * @param state
     * @param event
     * @return
     */
    public String makePatch(MakePatchState state, AnActionEvent event);


}
