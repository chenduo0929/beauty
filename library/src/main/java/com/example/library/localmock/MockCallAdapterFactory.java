package com.example.library.localmock;

import com.sankuai.meituan.retrofit2.Call;
import com.sankuai.meituan.retrofit2.CallAdapter;
import com.sankuai.meituan.retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import rx.Observable;

/**
 * @author chenduo
 */
public class MockCallAdapterFactory extends CallAdapter.Factory {

    private MockCallAdapterFactory() {
    }

    public static CallAdapter.Factory create() {
        return new MockCallAdapterFactory();
    }

    @Override
    public CallAdapter<?> get(Type type, Annotation[] annotations, Retrofit retrofit) {
        Class rawType = getRawType(type);
        if (rawType == Observable.class
                && type instanceof ParameterizedType
                && MockHelper.isMockOn(annotations)) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            if (types.length == 1) {
                return new MockCallAdapter(types[0]);
            }
        }

        return null;
    }

    private static class MockCallAdapter implements CallAdapter<Observable<?>> {

        Type mReturnType;

        public MockCallAdapter(Type returnType) {
            mReturnType = returnType;
        }

        @Override
        public Type responseType() {
            return mReturnType;
        }

        @Override
        public <R> Observable<?> adapt(Call<R> call) {
            return Observable.create((Observable.OnSubscribe<?>) subscriber -> {
                if (!subscriber.isUnsubscribed()) {
                    Object o = ObjectGenerator.gen(mReturnType);
                    subscriber.onNext(o);
                    subscriber.onCompleted();
                }
            });
        }
    }
}
