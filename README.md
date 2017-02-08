# EasyAndroidUpgrade
一行代码搞定android应用内部升级


        new UpgradeHelper.Builder(this)
                .setUpgradeUrl("http://192.168.1.79/public/upgrade.html?version=3")
                .setDelay(1000)
                .setIsAboutChecking(true)//关于页面手动检测更新需要设置isAboutChecking(true), 启动时检测设为false
                .build().check();
                
                
                待完善
