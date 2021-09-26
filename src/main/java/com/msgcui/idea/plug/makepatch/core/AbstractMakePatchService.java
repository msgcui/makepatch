package com.msgcui.idea.plug.makepatch.core;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.msgcui.idea.plug.makepatch.component.MakePatchState;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 补丁抽取服务（模板）
 */
public abstract class AbstractMakePatchService implements MakePatchService{

    /**
     * 制作补丁
     * @param state
     * @param event
     * @return
     */
    public String makePatch(MakePatchState state, AnActionEvent event){
        // step 1. 获取选中的文件
        VirtualFile[] virtualFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        StringBuffer srcFiles = new StringBuffer();
        StringBuffer buildFiles = new StringBuffer();
        if(virtualFiles != null && virtualFiles.length > 0)
        {
            // step 2. 创建文件目录
            String dirName = createDirName();
            // step 3. 循环每一项
            for(VirtualFile file : virtualFiles){
                String filePath = file.getPath();
                String porjectName = getProjectName(filePath);
            }
        }

        // step x. 构造readme信息

        return null;
    }

    /**
     * 创建文件夹名称
     * 默认实现：yyyyMMddHHmmss 外围实现可重写本方法
     * @return
     */
    public String createDirName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    /**
     * 获取项目名称
     * @param filePath
     * @return
     */
    public String getProjectName(String filePath) {
        int srcIndex = filePath.indexOf("/src/");
        String srcTemp = filePath.substring(0,srcIndex);
        int lastIndex = srcTemp.lastIndexOf("/");
        return srcTemp.substring(lastIndex,srcTemp.length());
    }


}
