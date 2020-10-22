# XDCOV
## 使用方法

### 不打包运行

修改`src\main\resources\account.properties`的内容为自己的学号和密码，以及`src\main\resources\data.json`的内容为要上报的信息。

```json
{
        "sfzx": "1", # 是否在校(0->否, 1->是)
        "tw": "1", # 体温 (36℃->0, 36℃到36.5℃->1, 36.5℃到36.9℃->2, 36.9℃到37℃.3->3, 37.3℃到38℃->4, 38℃到38.5℃->5, 38.5℃到39℃->6, 39℃到40℃->7, 40℃以上->8)
        "sfcyglq": "0", # 是否处于隔离期? (0->否, 1->是)
        "sfyzz": "0", # 是否出现乏力、干咳、呼吸困难等症状？ (0->否, 1->是)
        "qtqk": "", # 其他情况 (文本)
        "askforleave": "0" # 是否请假外出? (0->否, 1->是)
}
```

### 打包运行

修改jar包中`account.properties`和`data.json`文件同上。

java -jar -Dfile.encoding=UTF-8 xxxxx.jar

## 实现思路

向https://xxcapp.xidian.edu.cn/uc/wap/login/check发送post请求获取cookie，再带着获得的cookie向https://xxcapp.xidian.edu.cn/xisuncov/wap/open-report/save发送post请求上传数据。

通过Timer使这个过程每两个小时重复一次，把程序挂在服务器就可以高枕无忧了。
