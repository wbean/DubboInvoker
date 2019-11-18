/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.plugin.dubbo.invoker
 */
package me.wbean.plugin.dubbo.invoker;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author wbean
 * @date 2019/2/12 下午3:38
 */
public class InvokerPanel extends JPanel {

    private DubboService dubboService;
    private String address;

    public InvokerPanel(DubboService dubboService,String address) {
        this.dubboService = dubboService;
        this.address = address;
        initComponents();
        customerInit();
    }

    private void customerInit(){
        serviceAddress.setText(address);
        requestEditPanel.setText(dubboService.getParam().getDefaultText());
        requestEditPanel.setEditable(true);
        serviceName.setText(dubboService.getServiceName());

        invokeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dubboService.getParam().setRequestEditorText(requestEditPanel.getText().trim());
                dubboService.setServiceAddress(serviceAddress.getText().trim());
                dubboService.setServiceName(serviceName.getText().trim());
                responseTextPane.setText(dubboService.invoke().getText());

            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
    }

    private void close(){
        this.getTopLevelAncestor().setVisible(false);
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - wbean2
        serviceAddressLabel = new JLabel();
        serviceAddress = new JTextField();
        serviceNameLabel = new JLabel();
        serviceName = new JTextField();
        requestScrollPanel = new JBScrollPane();
        requestEditPanel = new JEditorPane();
        responseScrollPane = new JBScrollPane();
        responseTextPane = new JTextPane();
        cancelButton = new JButton();
        invokeButton = new JButton();

        //======== this ========

        // JFormDesigner evaluation mark

        setLayout(new GridBagLayout());
        ((GridBagLayout)getLayout()).columnWidths = new int[] {18, 97, 121, 88, 113, 144, 332, 42, 0, 0, 71, 0, 0};
        ((GridBagLayout)getLayout()).rowHeights = new int[] {37, 35, 77, 71, 434, 18, 0};
        ((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

        //---- serviceAddressLabel ----
        serviceAddressLabel.setText("ServiceAddress");
        add(serviceAddressLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
        add(serviceAddress, new GridBagConstraints(2, 0, 9, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        //---- serviceNameLabel ----
        serviceNameLabel.setText("ServiceName");
        add(serviceNameLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
        add(serviceName, new GridBagConstraints(2, 1, 9, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        //======== requestScrollPanel ========
        {
            requestScrollPanel.setViewportView(requestEditPanel);
        }
        add(requestScrollPanel, new GridBagConstraints(1, 2, 5, 3, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        //======== responseScrollPane ========
        {
            responseScrollPane.setViewportView(responseTextPane);
        }
        add(responseScrollPane, new GridBagConstraints(6, 2, 5, 3, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

        //---- cancelButton ----
        cancelButton.setText("cancel");
        add(cancelButton, new GridBagConstraints(8, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));

        //---- invokeButton ----
        invokeButton.setText("invoke");
        add(invokeButton, new GridBagConstraints(10, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 5), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - wbean2
    private JLabel serviceAddressLabel;
    private JTextField serviceAddress;
    private JLabel serviceNameLabel;
    private JTextField serviceName;
    private JScrollPane requestScrollPanel;
    private JEditorPane requestEditPanel;
    private JScrollPane responseScrollPane;
    private JTextPane responseTextPane;
    private JButton cancelButton;
    private JButton invokeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
