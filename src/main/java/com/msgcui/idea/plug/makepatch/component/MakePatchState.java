package com.msgcui.idea.plug.makepatch.component;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jdom.Element;


/**
 * 配置信息持久化
 */
@State(name = "makePatch", storages = {@com.intellij.openapi.components.Storage("$APP_CONFIG$/makePatch.xml")})
public class MakePatchState implements PersistentStateComponent<Element> {

    /**
     * 补丁输出目录
     */
    private String outputPath;


    public static MakePatchState getInstance(){
        return ServiceManager.getService(MakePatchState.class);
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("MakePatch");
        element.setAttribute("outputPath", getOutputPath());
        return element;
    }

    @Override
    public void loadState(@NotNull Element element) {
        setOutputPath(element.getAttributeValue("outputPath"));
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
