
    1.使用这套反射框架需要将目录下的文件全部加入Proguard保持，否者release版会崩溃
    2.使用方法是创建目标类的映射类，通过RefClass.load(mappingClass, targetClass)建立关联，映射类中创建对应的成员与方法即可直接访问

    public class RefPapiMockHelper {

        public static Class<?> Class = RefClass.load(
                RefPapiMockHelper.class,
                "com.meituan.android.retail.agreement.agreementbase.network.interceptor.PapiMockHelper");

        public static RefStaticMethod<Object> getInstance;


        public static RefMethod<Boolean> getEnabled;

    }