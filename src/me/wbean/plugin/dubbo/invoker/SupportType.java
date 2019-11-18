/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.plugin.dubbo.invoker
 */
package me.wbean.plugin.dubbo.invoker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.impl.PsiClassImplUtil;
import com.intellij.psi.search.ProjectAndLibrariesScope;

/**
 *
 * @author wbean
 * @date 2019/2/15 上午11:59
 */
enum SupportType {
    BOOLEAN {
        @Override
        Boolean getRandomValue(PsiVariable psiVariable) {
            return RandomUtils.nextBoolean();
        }

        @Override
        Boolean getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null){
                return Boolean.valueOf(defaultValue);
            }
            return getRandomValue(psiVariable);
        }
    },
    CHAR {
        @Override
        Character getRandomValue(PsiVariable psiVariable) {
            return RandomStringUtils.randomAlphabetic(1).charAt(0);
        }

        @Override
        Character getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null){
                return defaultValue.charAt(0);
            }
            return getRandomValue(psiVariable);
        }
    },
    INTEGER {
        @Override
        Integer getRandomValue(PsiVariable psiVariable) {
            return RandomUtils.nextInt(10000);
        }

        @Override
        Integer getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null && StringUtils.isNumeric(defaultValue)){
                return Integer.valueOf(defaultValue);
            }
            return getRandomValue(psiVariable);
        }
    },
    FLOAT {
        @Override
        Float getRandomValue(PsiVariable psiVariable) {
            return RandomUtils.nextFloat();
        }

        @Override
        Float getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null && StringUtils.isNumeric(defaultValue)){
                return Float.valueOf(defaultValue);
            }
            return getRandomValue(psiVariable);
        }
    },
    STRING {
        @Override
        String getRandomValue(PsiVariable psiVariable) {
            return RandomStringUtils.randomAlphanumeric(10);
        }

        @Override
        String getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null){
                return defaultValue;
            }
            return getRandomValue(psiVariable);
        }
    },
    LIST {
        @Override
        List getRandomValue(PsiVariable psiVariable) {
            return new ArrayList(0);
        }

        @Override
        List getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null){
                try{
                    return JSON.parseArray(defaultValue);
                }catch (Exception e){}
            }
            return getRandomValue(psiVariable);
        }
    },
    MAP {
        @Override
        Map getRandomValue(PsiVariable psiVariable) {
            return new HashMap(0);
        }

        @Override
        Map getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null){
                try{
                    return JSON.parseObject(defaultValue);
                }catch (Exception e){}
            }
            return getRandomValue(psiVariable);
        }
    },
    DATE {
        @Override
        Date getRandomValue(PsiVariable psiVariable) {
            return new Date();
        }

        @Override
        Date getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null){
                try{
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return simpleDateFormat.parse(defaultValue);
                }catch (Exception e){}
            }
            return getRandomValue(psiVariable);
        }
    },

    OTHER {
        @Override
        JSONObject getRandomValue(PsiVariable psiVariable) {
            PsiClass psiClass = JavaPsiFacade.getInstance(psiVariable.getProject()).findClass(psiVariable.getType().getCanonicalText(), new ProjectAndLibrariesScope(psiVariable.getProject()));
            return this.obj2Map(psiClass);
        }

        @Override
        Map getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            JSONObject docValue = null;
            if(defaultValue != null){
                try{
                    docValue = JSON.parseObject(defaultValue);
                }catch (Exception e){}
            }

            JSONObject randomValue = getRandomValue(psiVariable);
            return mergeJson(randomValue, docValue).getInnerMap();
        }

        public JSONObject mergeJson(JSONObject object1,JSONObject object2){
            if (object1 == null &&object2 == null){
                return null;
            }
            if (object1 == null){
                return object2;
            }
            if (object2 == null){
                return object1;
            }
            Iterator iterator = object2.keySet().iterator();
            while (iterator.hasNext()){
                String key = (String) iterator.next();
                Object value2 = object2.get(key);
                if (object1.containsKey(key)){
                    Object value1 = object1.get(key);

                    if (value1 instanceof JSONObject && value2 instanceof JSONObject){
                        object1.put(key,mergeJson((JSONObject) value1, (JSONObject) value2));
                    }else {
                        object1.put(key,value2);
                    }
                }else {
                    object1.put(key,value2);
                }
            }
            return object1;
        }

        private JSONObject obj2Map(PsiClass psiClass) {
            PsiField[] allField = PsiClassImplUtil.getAllFields(psiClass);
            JSONObject result = new JSONObject(allField.length);

            for (PsiField psiField : allField) {
                if(psiField.getModifierList().hasModifierProperty("static") || psiField.getModifierList().hasModifierProperty("final") ){
                    continue;
                }
                SupportType supportType = SupportType.touch(psiField);

                if (supportType == SupportType.OTHER) {
                    PsiClass subPsiClass = JavaPsiFacade.getInstance(psiClass.getProject()).findClass(psiField.getType().getCanonicalText(), new ProjectAndLibrariesScope(psiClass.getProject()));
                    result.put(psiField.getName(), obj2Map(subPsiClass));
                } else {
                    result.put(psiField.getName(), supportType.getRandomValue(psiField));
                }

            }
            return result;
        }
    },

    ENUM {
        @Override
        String getRandomValue(PsiVariable psiVariable) {
            return ((PsiClassReferenceType)psiVariable.getType()).rawType().resolve().getFields()[0].getName();
        }

        @Override
        String getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap) {
            String defaultValue = defaultValueMap.get(psiVariable.getName());
            if(defaultValue != null){
                return defaultValue;
            }
            return getRandomValue(psiVariable);
        }
    }
    ;

    abstract Object getRandomValue(PsiVariable psiVariable);

    abstract Object getValue(PsiVariable psiVariable, Map<String, String> defaultValueMap);

    public static SupportType touch(PsiVariable parameter) {
        PsiType type = parameter.getType();
        if (PsiType.BOOLEAN.equals(type)) {
            return SupportType.BOOLEAN;
        }

        if (PsiType.CHAR.equals(type)) {
            return SupportType.CHAR;
        }

        if (PsiType.BYTE.equals(type)
                || PsiType.INT.equals(type)
                || PsiType.LONG.equals(type)
                || PsiType.SHORT.equals(type)
                || type.equalsToText(Integer.class.getCanonicalName())
                || type.equalsToText(Long.class.getCanonicalName())
                || type.equalsToText(Short.class.getCanonicalName())
                || type.equalsToText(Byte.class.getCanonicalName())) {
            return SupportType.INTEGER;
        }

        if (PsiType.DOUBLE.equals(type)
                || PsiType.FLOAT.equals(type)
                || type.equalsToText(Double.class.getCanonicalName())
                || type.equalsToText(Float.class.getCanonicalName())) {
            return SupportType.FLOAT;
        }

        if (type.equalsToText(String.class.getCanonicalName())) {
            return SupportType.STRING;
        }

        if (type.equalsToText(Date.class.getCanonicalName())) {
            return SupportType.DATE;
        }

        if (type.getCanonicalText().startsWith(List.class.getCanonicalName())
                || type instanceof PsiArrayType) {
            return SupportType.LIST;
        }

        if (type.getCanonicalText().startsWith(Map.class.getCanonicalName())) {
            return SupportType.MAP;
        }

        if (type.getSuperTypes().length > 0 && type.getSuperTypes()[0].getCanonicalText().contains(Enum.class.getCanonicalName())){
            return SupportType.ENUM;
        }

        return SupportType.OTHER;
    }
}
