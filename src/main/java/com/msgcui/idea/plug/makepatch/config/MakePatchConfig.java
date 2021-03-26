package com.msgcui.idea.plug.makepatch.config;


import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.NlsContexts;
import com.msgcui.idea.plug.makepatch.component.MakePatchComponent;
import com.msgcui.idea.plug.makepatch.component.MakePatchState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


/**
 * 打包配置
 */
public class MakePatchConfig implements SearchableConfigurable {

    private MakePatchComponent makePatchComponent;

    private MakePatchState state = MakePatchState.getInstance();


    @Override
    public @NotNull String getId() {
        return "com.msgcui.idea.plug.makepatch.config.MakePatchConfig";
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "make patch config";
    }

    @Override
    public @Nullable JComponent createComponent() {
        if(this.makePatchComponent == null) {
            this.makePatchComponent = new MakePatchComponent();
        }
        this.makePatchComponent.outputDir.setText(state.getOutputPath());
        return this.makePatchComponent.mainPanel;
    }

    /**
     * 用户在点击ok或apply按钮之前由IDEA调用该方法来判断当前内容是否已经被修改来控制面板中的OK或apply按钮是否可用
     * @return
     */
    @Override
    public boolean isModified() {
        String outputDir = makePatchComponent.outputDir.getText();
        String stateOutputDir = state.getOutputPath();
        if(!outputDir.equals(stateOutputDir)){
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        String str = makePatchComponent.outputDir.getText();
        state.setOutputPath(str);
    }
}
