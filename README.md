# DubboInvoker
a IntelliJ IDEA plugin invoke dubbo service by socket
https://plugins.jetbrains.com/plugin/11981-dubboinvoker/

* a develop tool help test dubbo service.
* call dubbo service with several click.
* auto generate param value according param type.
* support user define param value in doc comment.
       
usage:
1. step1: right click on dubbo service method 
2. step2: click DubboInvoker 
3. step3: check auto generate param value 
4. step4: click invoke button


user define param value example:

    /**
     *
     * @param a id
     *          example=asdfasdf
     * @param b 商户号
     *          example=123
     * @param exportParam realParam
     *                    example={"billDate":20190101}
     */
    void getData(String a, int b, ExportParam exportParam);
       1. new line for example
       2. start with token "example="

default dubbo address config:
1. open .idea/misc.xml
2. add xml node 
```<DubboInvoker ip="127.0.0.1" port="20661"/> ```
under project tag.

# RELEASE LOG

1.1-RELEASE
1. add enum support
2. add address config 
3. add dubbo telnet param 'class' attribute support
