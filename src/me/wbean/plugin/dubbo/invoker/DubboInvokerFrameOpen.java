package me.wbean.plugin.dubbo.invoker;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.ui.JBDimension;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.io.InputStream;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;

/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.plugin.dubbo.invoker
 */
public class DubboInvokerFrameOpen extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(EDITOR);

        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if(! (psiElement instanceof PsiMethod)){
            Messages.showMessageDialog("only apply on method", "warn", null);
            return;
        }

        PsiMethod psiMethod = (PsiMethod) psiElement;

        PsiJavaFile javaFile = (PsiJavaFile) psiMethod.getContainingFile();
        PsiClass psiClass = (PsiClass) psiElement.getParent();
        String dubboServiceStr = String.format("%s.%s.%s",javaFile.getPackageName(), psiClass.getName(), psiMethod.getName());


        DubboService.Param param = new DubboService.Param(psiMethod.getParameterList(), psiMethod.getDocComment());
        DubboService dubboService = new DubboService(dubboServiceStr, param);

        InvokerPanel invokerPanel = new InvokerPanel(dubboService, getDefaultConfig(editor));
        ComponentPopupBuilder componentPopupBuilder = JBPopupFactory.getInstance().createComponentPopupBuilder(invokerPanel, null);
        JBPopup jbPopup = componentPopupBuilder
                .setCancelOnClickOutside(false)
                .setCancelOnOtherWindowOpen(false)
                .setCancelOnWindowDeactivation(false)
                .setRequestFocus(true)
                .setResizable(false)
                .setMovable(true)
                .setMayBeParent(true)
                .setTitle("DubboInvoker")
                .setProject(e.getProject())
                .setCancelKeyEnabled(true)
                .createPopup();
        jbPopup.setMinimumSize(new JBDimension(200,100));
        jbPopup.showCenteredInCurrentWindow(e.getProject());
    }

    private String getDefaultConfig(Editor editor){
        String ipStr = "127.0.0.1";
        String portStr = "20661";
        try {
            InputStream inputStream = editor.getProject().getProjectFile().getInputStream();
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(inputStream);

            NodeList dubboInvoker = document.getElementsByTagName("DubboInvoker");

            if(dubboInvoker.getLength()>0){
                Node ip = dubboInvoker.item(0).getAttributes().getNamedItem("ip");
                if(ip!=null){
                    ipStr = ip.getTextContent();
                }

                Node port = dubboInvoker.item(0).getAttributes().getNamedItem("port");
                if(port !=null){
                    portStr = port.getTextContent();
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
        }

        return ipStr + ":" + portStr;
    }
}
