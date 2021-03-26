package com.msgcui.idea.plug.makepatch.core.impl;

import com.android.aapt.Resources;
import com.google.common.io.Files;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.msgcui.idea.plug.makepatch.component.MakePatchState;
import com.msgcui.idea.plug.makepatch.core.MakePatchService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 制作补丁（默认实现类）
 */
public class DefaultMakePatchService implements MakePatchService {

    private static final String SRC_PATH = "/src/main/java/";

    private static final String RESOURCE_PATH = "/src/main/resources/";

    private static final String JSP_PATH = "/src/main/webapp/";

    private static final String TARGET_CLASS_PATH = "/WEB-INF/classes/";

    private static final String TARGET_RESOURCE_PATH = "/WEB-INF/classes/";

    private static final String TARGET_JSP_PATH = "/";

    @Override
    public String makePatch(MakePatchState state, AnActionEvent event) {

        // step 1. 获取到选择的文件
        VirtualFile[] virtualFiles = event.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY);
        StringBuffer srcFiles = new StringBuffer();
        StringBuffer buildFiles = new StringBuffer();
        if(virtualFiles != null && virtualFiles.length > 0)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String dirName = sdf.format(new Date());
            for(VirtualFile file : virtualFiles){
                String filePath = file.getPath();
                // 获取项目名称
                String projectName = getProjectName(filePath);
                String outputDir = getOutputPath(state, dirName, projectName);
                // step 2. 获取到build目录下的对应文件信息
                String buildFilePath = getBuildFilePath(filePath);
                // setp 3. 获取目标目录
                String dirPath = buildFilePath.substring(0, buildFilePath.lastIndexOf("/"));
                String fileName = buildFilePath.substring(buildFilePath.lastIndexOf("/")+1, buildFilePath.lastIndexOf("."));
                File dirFile = new File(dirPath);
                // 获取该目录下所有的问题件
                File[] files = dirFile.listFiles();
                if(files != null && files.length > 0){
                    for(File tempFile : files){
                        String tempFileName = tempFile.getName();
                        if(tempFileName.startsWith(fileName) || tempFileName.startsWith(fileName + "$")){
                            String targetFilePath = (outputDir.endsWith("/") ? outputDir.substring(0,outputDir.lastIndexOf("/")) : outputDir) + getTargetFilePath(filePath,tempFileName);
                            srcFiles.append(filePath + "\r\n");
                            buildFiles.append(targetFilePath + "\r\n");
                            FileChannel inputChannel = null;
                            FileChannel outputChannel = null;
                            try {
                                inputChannel = new FileInputStream(new File(buildFilePath)).getChannel();
                                File targetFile = new File(targetFilePath);
                                if(!targetFile.exists()){
                                    // 构造目录
                                    File dir = new File(targetFilePath.substring(0,targetFilePath.lastIndexOf("/")));
                                    if(!dir.exists()){
                                        dir.mkdirs();
                                    }
                                    targetFile.createNewFile();
                                }
                                outputChannel = new FileOutputStream(targetFile).getChannel();
                                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

                            } catch(Exception e){
                                throw new RuntimeException("copy文件失败,"+e.getMessage()+"\r\n buildFilePath:["+buildFilePath+"] targetFilePath:["+targetFilePath+"]",e);
                            }finally {
                                if(inputChannel != null){
                                    try {
                                        inputChannel.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(outputChannel != null){
                                    try {
                                        outputChannel.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        return "补丁抽取成功";
    }

    /**
     * 获取该编译文件的子文件（当java类中包含内部类时，class文件会有多个）
     * @param buildPath
     * @return
     */
    public List<String> getSameTypeClass(String buildPath){
        String dirPath = buildPath.substring(0, buildPath.lastIndexOf("/"));
        String fileName = buildPath.substring(buildPath.lastIndexOf("/"), buildPath.lastIndexOf("."));
        File dirFile = new File(dirPath);
        // 获取该目录下所有的问题件
        File[] files = dirFile.listFiles();
        List<String> fileList = new ArrayList<>();
        if(files != null && files.length > 0){
            for(File file : files){
                String tempFileName = file.getName();
                if(tempFileName.startsWith(fileName)){
                    fileList.add(file.getPath());
                }
            }
        }
        return fileList;
    }

    /**
     * 获取项目名称
     * @param filePath
     * @return
     */
    private String getProjectName(String filePath) {
        int srcIndex = filePath.indexOf("/src/");
        String srcTemp = filePath.substring(0,srcIndex);
        int lastIndex = srcTemp.lastIndexOf("/");
        return srcTemp.substring(lastIndex,srcTemp.length());
    }

    /**
     * 获取输出目录路径
     * @param state
     * @param projectName
     * @return
     */
    private String getOutputPath(MakePatchState state, String dirName, String projectName){

        // 补丁文件创建目录
        String outputPath = state.getOutputPath();
        // 项目名称
        if(outputPath.endsWith("/")){
            return outputPath + dirName + projectName + "/";
        }else{
            return outputPath + "/" + dirName + projectName + "/";
        }
    }

    private String getBuildFilePath(String srcFilePath){
        String buildPath = null;
        if(srcFilePath.contains(SRC_PATH)){
            // 源文件
            String temp = srcFilePath.replace(SRC_PATH,"/target/classes/");
            buildPath = temp.substring(0,temp.indexOf(".java")) + ".class";
        }else if(srcFilePath.contains(RESOURCE_PATH)){
            // 资源文件
            buildPath = srcFilePath.replace(RESOURCE_PATH,"/target/classes/");
        }else if(srcFilePath.contains(JSP_PATH)){
            buildPath = srcFilePath;
        }
        return buildPath;
    }

    /**
     * 获取编译文件目录
     * @param srcFilePath
     * @return
     */
    private String getTargetFilePath(String srcFilePath,String fileName){
        // 先对srcFilePath进行解析，判断是class、resource、jsp
        String targetPath = null;
        if(srcFilePath.contains(SRC_PATH)){
            // 源文件
            int index = srcFilePath.indexOf(SRC_PATH);
            String temp = srcFilePath.substring(index).replace(SRC_PATH,"");
            targetPath = TARGET_CLASS_PATH + temp.substring(0,temp.lastIndexOf("/")) + "/" + fileName;
        }else if(srcFilePath.contains(RESOURCE_PATH)){
            // 资源文件
            int index = srcFilePath.indexOf(RESOURCE_PATH);
            targetPath = TARGET_RESOURCE_PATH + srcFilePath.substring(index).replace(RESOURCE_PATH,"");;
        }else if(srcFilePath.contains(JSP_PATH)){
            // JSP文件
            int index = srcFilePath.indexOf(JSP_PATH);
            targetPath = TARGET_JSP_PATH + srcFilePath.substring(index).replace(JSP_PATH,"");;
        }
        return targetPath;
    }

}
