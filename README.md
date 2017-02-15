EasyAndroidUpgrade
================

##EasyAndroidUpgrade支持的功能
1. 一行代码搞定android应用内部升级。（欢迎加QQ群讨论：127591825）

###EasyAndroidUpgrade用法及原理

1、在你的 app module build.gradle 文件中添加如下依赖

```
compile 'com.lijunhuayc.upgrade:easyupgrade:1.0.4'
```

2、在application或者activity中添加如下代码使用APP升级功能
```
new UpgradeHelper.Builder(this)
       .setUpgradeUrl("http://192.168.1.79/public/upgrade.html?version=3")
       .setDelay(1000)
       .setIsAboutChecking(true)//关于页面手动检测更新需要设置isAboutChecking(true), 启动时检测设为false
       .build().check();
```
链式调用方式设置升级参数，框架内部检测到升级后会启动一个Dialog样式Activity来提示用户进行升级，升级时用户可以等待App下载完成也可以选择后台静默下载，下载过程中用户可以暂停或者取消下载从而终止升级，下载完成后会根据配置检测包名是否一直，文件MD5值是否一致，检测完成后会根据配置决定直接弹出App安装界面还是Notification通知用户已下载完成。

###配置参数解释
```
public class UpgradeConfig implements Parcelable {
    private String upgradeUrl;                      //upgrade check remote-interface.
    private boolean isAutoStartInstall = true;
    private boolean isQuietDownload = false;        //whether quiet download when the update is detected.
    private boolean isCheckPackageName = true;      //whether check the package name.
    private boolean isAboutChecking = false;        //whether is "about" check upgrade.
    private long delay = 0;                         //millisecond. whether delay check upgrade.
}
```

<pre>
upgradeUrl              升级接口
isAutoStartInstall      下载完成后是否直接启动安装
isQuietDownload         是否直接后台下载App
isCheckPackageName      下载玩APP是否检测App包名一致（通常必须要检测，如果公司产品需要变更包名的则不用检测，还能引导使用盗版App的用户升级到正版App）
isAboutChecking         “关于”页面中用户手动检测升级需要设置此参数
delay                   启动检测时可以设置延迟请求时间，避开启动时大批量接口请求数据。
</pre>


## License
    Copyright 2017 lijunhuayc <lijunhuayc@sina.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
