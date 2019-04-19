

    1.本地化的Mock解决方案
    2.使用：创建retrofit对象时添加MockCallAdapterFactory即可

        sMainRetrofit = new Retrofit.Builder()
                .baseUrl(ServerUtils.MainDomain.getHost())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(MockCallAdapterFactory.create())
                .addCallAdapterFactory(RxErrorHandleCallAdapterFactory.create())
                .callFactory(getCallFactory())
                .build();