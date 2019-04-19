package com.example.library.localmock;

import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenduo
 * 随机对象生成器
 */
public class ObjectGenerator {

    public static Object gen(Type type) {
        StringBuffer stringBuffer = new StringBuffer("Gen Object");

        Object o = genInner(type, null, stringBuffer, 0, null);

        Log.d("ObjectGenerator", stringBuffer.toString());

        return o;
    }

    public static Object genInner(Type type, Type actualType, StringBuffer sb, int tabCount, String mockValue) {
        if (type instanceof TypeVariable) {
            if (actualType != null) {
                return genInner(actualType, null, sb, tabCount, null);
            }
            sb.append(" = null");
            return null;
        } else {
//        byte  Byte，short  Short，int  Integer，long  Long，float  Float，double  Double，char  Character，boolean  Boolean
            String classSimpleName = type.toString();
            if (classSimpleName.equals("Byte") || classSimpleName.equals("byte")) {
                Byte value = Integer.valueOf(1).byteValue();
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.equals("Short") || classSimpleName.equals("short")) {
                Short value = (short) 1;
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.equals("Integer") || classSimpleName.equals("int")) {
                Integer value = mockValue == null ? 1 : Integer.valueOf(mockValue);
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.equals("Long") || classSimpleName.equals("long")) {
                Long value = 1L;
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.equals("Float") || classSimpleName.equals("float")) {
                Float value = 1f;
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.equals("Double") || classSimpleName.equals("double")) {
                Double value = 1d;
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.equals("Character") || classSimpleName.equals("char")) {
                Character value = 'a';
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.equals("Boolean") || classSimpleName.equals("boolean")) {
                Boolean value = true;
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.equals("class java.lang.String")) {
                String value = "Default";
                sb.append(" = " + value);
                return value;
            } else if (classSimpleName.startsWith("java.util.List")) {
                Integer size = mockValue == null ? 1 : Integer.valueOf(mockValue);
                List l = new ArrayList();
                Type t = getActualType(type);
                if (t != null) {
                    for (int i = 0; i < size; i++) {
                        l.add(genInner(t, null, sb, tabCount, null));
                    }
                }
                return l;
            } else if (classSimpleName.startsWith("java.util.Map")) {
                return null;
            } else {
                Class clazz = getRawType(type);
                sb.append("(" + clazz.getSimpleName() + ") = ");
                Object o = null;
                try {
                    o = clazz.newInstance();
                    Type realType = getActualType(type);

                    Field[] fields = clazz.getFields();
                    for (Field f :
                            fields) {
                        f.setAccessible(true);
                        sb.append("\n" + genTab(tabCount) + f.getName());

                        f.set(o, genInner(f.getGenericType(), realType, sb, tabCount + 1, getMockValue(f)));
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (o == null) {
                    sb.append("null");
                }
                return o;
            }
        }
    }

    static String getMockValue(Field f) {
        if (f == null) {
            return null;
        }

        MockValue val = f.getAnnotation(MockValue.class);
        if (val != null) {
            return val.value();
        } else {
            return null;
        }
    }

    static Type getActualType(Type type) {
        Type realType = null;
        if (type instanceof ParameterizedType) {
            Type[] ts = ((ParameterizedType) type).getActualTypeArguments();
            if (ts != null && ts.length == 1) {
                realType = ts[0];
            }
        }

        return realType;
    }

    static String genTab(int tabCount) {
        String str = "";
        for (int i = 0; i < tabCount; i++) {
            str += "\t";
        }

        return str;
    }

    static Class<?> getRawType(Type type) {
        if (type == null) throw new NullPointerException("type == null");

        if (type instanceof Class<?>) {
            // Type is a normal class.
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            Type rawType = parameterizedType.getRawType();
            if (!(rawType instanceof Class)) throw new IllegalArgumentException();
            return (Class<?>) rawType;
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            return Object.class;
        }
        if (type instanceof WildcardType) {
            return getRawType(((WildcardType) type).getUpperBounds()[0]);
        }

        throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                + "GenericArrayType, but <" + type + "> is of type " + type.getClass().getName());
    }

}
