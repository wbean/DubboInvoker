/**
 * Copyright ©2014-2019 Youzan.com All rights reserved
 * me.wbean.plugin.dubbo.invoker
 */
package me.wbean.plugin.dubbo.invoker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author wbean
 * @date 2019/2/13 下午3:33
 */
public class InvokeService {
    public String invoke(DubboService dubboService){
        String[] addressSplit = dubboService.getServiceAddress().split(":");
        String ip = addressSplit[0];
        int port = Integer.valueOf(addressSplit[1]);

        try {
            Socket socket = new Socket(ip, port);
            PrintWriter out =
                    new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),"GBK"));
            String command = String.format("invoke %s(%s)", dubboService.getServiceName(), dubboService.getParam().getDubboParamString());
            out.println(command);

            String response = in.readLine();

            socket.close();
            return response;
        } catch (IOException e) {
            return "connection error:" + e.getMessage();
        }
    }

    public static void main(String[] args){
        InvokeService invokeService = new InvokeService();
    }
}
