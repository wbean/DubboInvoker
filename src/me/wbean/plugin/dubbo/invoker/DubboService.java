/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.plugin.dubbo.invoker
 */
package me.wbean.plugin.dubbo.invoker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;

import static me.wbean.plugin.dubbo.invoker.SupportType.ENUM;

/**
 * @author wbean
 * @date 2019/2/13 上午10:28
 */
public class DubboService {
    /**
     * dubo服务地址
     */
    private String serviceAddress;

    /**
     * dubbo服务名
     */
    private String serviceName;

    /**
     * 参数
     */
    private Param param;


    private InvokeService invokeService = new InvokeService();

    public DubboService(String serviceName, Param param){
        this.param = param;
        this.serviceName = serviceName;
    }

    public void setServiceAddress(String serviceAddress){
        this.serviceAddress = serviceAddress;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Param getParam(){
        return this.param;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 发起dubbbo调用
     * @return
     */
    public Response invoke(){
        String response = invokeService.invoke(this);
        return new Response(response);
    }

    /**
     * 返回值处理相关
     */
    static class Response{
        private String responseStr;
        public Response(String response){
            responseStr = response;
        }

        public String getText(){
            try {
                JSONObject obj = JSON.parseObject(responseStr);
                if (obj != null) {
                    return JSON.toJSONString(obj, SerializerFeature.PrettyFormat).replace("\t", "    ");
                }
            }catch (Exception e){}
            return responseStr;
        }

    }

    /**
     * 参数处理相关
     */
    static class Param {
        static final String PARAM_TAG = "param";
        static final String PREFIX = "example=";

        private PsiParameterList psiParameterList;

        private PsiDocComment psiDocComment;

        private List<Object> paramList;

        private String requestEditorText;

        public Param(PsiParameterList psiParameterList, PsiDocComment psiDocComment) {
            this.psiParameterList = psiParameterList;
            this.psiDocComment = psiDocComment;
            Map<String,String> docParams = getJavaDocParam();
            initParam(docParams);
        }

        /**
         * 获取页面RequestEditPanel上展示的Text
         *
         * @return
         */
        public String getDefaultText() {
            String defaultText = JSON.toJSONString(paramList,SerializerFeature.PrettyFormat);
            defaultText = defaultText.replace("\t", "    ");
            return defaultText;
        }

        /**
         * 获取dubbo调用时需要的格式
         *
         * @return
         */
        public String getDubboParamString() {
            String dubboParam = requestEditorText;
            JSONArray arr = JSON.parseArray(dubboParam);
            dubboParam = JSON.toJSONString(arr);
            dubboParam = dubboParam.substring(1,dubboParam.length() - 1);
            return dubboParam;
        }

        public void setRequestEditorText(String requestEditorText) {
            this.requestEditorText = requestEditorText;
        }

        /**
         * 初始化参数
         *
         * @param docParams
         */
        private void initParam(Map<String, String> docParams) {
            paramList = new ArrayList<>();
            for (PsiParameter psiParameter : psiParameterList.getParameters()) {
                SupportType supportType = SupportType.touch(psiParameter);
                Object value = supportType.getValue(psiParameter, docParams);
                if(value instanceof Map){
                    if(!((Map) value).containsKey("class")){
                        ((Map) value).put("class", psiParameter.getType().getCanonicalText());
                    }
                }
                paramList.add(value);
            }
        }

        /**
         * 从method的javadoc中，获取param的example value
         *
         * @return paramName => example value
         */
        private Map<String, String>  getJavaDocParam() {
            Map<String, String> docParams = new HashMap<>();
            if(psiDocComment == null){
                return docParams;
            }
            for (PsiDocTag docTag : psiDocComment.findTagsByName(PARAM_TAG)) {
                if(docTag.getValueElement() == null){
                    continue;
                }
                String paramName = docTag.getValueElement().getText();
                String[] docTagTextLineArr = docTag.getText().split("\n");
                for (String line : docTagTextLineArr) {
                    if(line.contains(PREFIX)){
                        docParams.put(paramName, line.split(PREFIX)[1].trim());
                    }
                }
            }
            return docParams;
        }
    }

}

