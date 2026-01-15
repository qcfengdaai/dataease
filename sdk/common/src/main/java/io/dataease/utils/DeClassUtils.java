package io.dataease.utils;

/**
 * 类型判断工具类
 * <p>
 * 提供Java基本类型和包装类型的判断功能。用于在运行时检查对象是否为基本数据类型或其包装类，
 * 常用于反射、序列化、数据转换等场景中区分基本类型和复杂对象。
 * </p>
 *
 * <p><b>核心功能：</b></p>
 * <ul>
 *   <li>判断对象是否为基本类型或包装类型</li>
 *   <li>支持8种基本类型及其包装类的识别</li>
 *   <li>空值安全判断</li>
 * </ul>
 *
 * <p><b>支持的类型：</b></p>
 * <ul>
 *   <li>基本类型：boolean, char, byte, short, int, long, float, double</li>
 *   <li>包装类型：Boolean, Character, Byte, Short, Integer, Long, Float, Double</li>
 * </ul>
 *
 * <p><b>应用场景：</b></p>
 * <ul>
 *   <li>对象序列化 - 区分简单类型和复杂对象，采用不同的序列化策略</li>
 *   <li>数据转换 - 判断是否需要进行类型转换</li>
 *   <li>反射操作 - 在反射设置属性值时判断类型</li>
 *   <li>参数校验 - 检查参数是否为简单类型</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>
 * // 示例1：基本类型判断
 * Integer num = 100;
 * boolean result = DeClassUtils.isPrimitiveOrWrapper(num);
 * System.out.println(result);  // 输出: true
 *
 * String str = "hello";
 * result = DeClassUtils.isPrimitiveOrWrapper(str);
 * System.out.println(result);  // 输出: false
 *
 * // 示例2：空值判断
 * Object obj = null;
 * result = DeClassUtils.isPrimitiveOrWrapper(obj);
 * System.out.println(result);  // 输出: false
 *
 * // 示例3：各种类型测试
 * DeClassUtils.isPrimitiveOrWrapper(123);              // true - Integer
 * DeClassUtils.isPrimitiveOrWrapper(123L);             // true - Long
 * DeClassUtils.isPrimitiveOrWrapper(123.45);           // true - Double
 * DeClassUtils.isPrimitiveOrWrapper(123.45f);          // true - Float
 * DeClassUtils.isPrimitiveOrWrapper(true);             // true - Boolean
 * DeClassUtils.isPrimitiveOrWrapper('A');              // true - Character
 * DeClassUtils.isPrimitiveOrWrapper((byte)1);          // true - Byte
 * DeClassUtils.isPrimitiveOrWrapper((short)1);         // true - Short
 * DeClassUtils.isPrimitiveOrWrapper("string");         // false - String
 * DeClassUtils.isPrimitiveOrWrapper(new Object());     // false - Object
 * DeClassUtils.isPrimitiveOrWrapper(new int[]{1,2});   // false - Array
 *
 * // 示例4：在序列化中使用
 * public String serialize(Object obj) {
 *     if (DeClassUtils.isPrimitiveOrWrapper(obj)) {
 *         // 基本类型直接转字符串
 *         return obj.toString();
 *     } else {
 *         // 复杂对象使用JSON序列化
 *         return JSON.toJSONString(obj);
 *     }
 * }
 *
 * // 示例5：在反射赋值中使用
 * public void setFieldValue(Object target, Field field, Object value) {
 *     if (DeClassUtils.isPrimitiveOrWrapper(value)) {
 *         // 基本类型直接赋值
 *         field.set(target, value);
 *     } else {
 *         // 复杂对象可能需要深拷贝
 *         field.set(target, deepCopy(value));
 *     }
 * }
 *
 * // 示例6：在数据转换中使用
 * public Object convertValue(Object value, Class<?> targetType) {
 *     if (DeClassUtils.isPrimitiveOrWrapper(value)) {
 *         // 基本类型使用简单转换
 *         return simpleConvert(value, targetType);
 *     } else {
 *         // 复杂对象使用对象映射
 *         return objectMapper.map(value, targetType);
 *     }
 * }
 * </pre>
 *
 * <p><b>注意事项：</b></p>
 * <ul>
 *   <li>null值会返回false，不会抛出NullPointerException</li>
 *   <li>String类型不属于基本类型，会返回false</li>
 *   <li>数组类型（包括基本类型数组）不属于基本类型，会返回false</li>
 *   <li>自动装箱的基本类型会被识别为包装类型</li>
 *   <li>该方法只判断类型，不验证值的有效性</li>
 * </ul>
 *
 * <p><b>最佳实践：</b></p>
 * <ul>
 *   <li>在处理未知类型对象前使用此方法判断</li>
 *   <li>结合instanceof进行更精确的类型判断</li>
 *   <li>序列化场景下优先使用此方法提高性能</li>
 *   <li>避免对已知类型的对象重复调用</li>
 * </ul>
 *
 * <p><b>实现原理：</b></p>
 * <ul>
 *   <li>维护包装类型数组，通过isAssignableFrom判断</li>
 *   <li>维护基本类型名称数组，通过类名匹配判断</li>
 *   <li>使用Class.isPrimitive()判断基本类型</li>
 * </ul>
 *
 * @author DataEase
 * @since 1.0.0
 */
public class DeClassUtils {

    /**
     * 判断对象是否为基本类型或包装类型
     * <p>
     * 检查给定对象是否为Java的8种基本数据类型（boolean, char, byte, short, int, long, float, double）
     * 或其对应的包装类（Boolean, Character, Byte, Short, Integer, Long, Float, Double）。
     * </p>
     *
     * <p><b>判断逻辑：</b></p>
     * <ol>
     *   <li>如果对象为null，返回false</li>
     *   <li>检查对象类型是否为8种包装类之一</li>
     *   <li>检查对象类型是否为8种基本类型之一</li>
     * </ol>
     *
     * <p><b>使用示例：</b></p>
     * <pre>
     * // 包装类型
     * DeClassUtils.isPrimitiveOrWrapper(Integer.valueOf(10));  // true
     * DeClassUtils.isPrimitiveOrWrapper(Boolean.TRUE);         // true
     *
     * // 非基本类型
     * DeClassUtils.isPrimitiveOrWrapper("123");                // false
     * DeClassUtils.isPrimitiveOrWrapper(new Date());           // false
     * DeClassUtils.isPrimitiveOrWrapper(null);                 // false
     * </pre>
     *
     * @param obj 要判断的对象，可以为null
     * @return true表示是基本类型或包装类型，false表示不是或对象为null
     */
    public static boolean isPrimitiveOrWrapper(Object obj) {
        if (obj == null) {
            return false;
        }

        Class<?> objClass = obj.getClass();
        for (Class<?> primitiveWrapper : primitiveWrappers) {
            if (primitiveWrapper.isAssignableFrom(objClass)) {
                return true;
            }
        }

        return isPrimitive(objClass);
    }

    /**
     * 判断Class类型是否为基本类型
     * <p>
     * 通过Class.isPrimitive()和类名匹配两种方式判断是否为基本类型。
     * 这是一个内部辅助方法，用于支持{@link #isPrimitiveOrWrapper(Object)}。
     * </p>
     *
     * @param clazz 要判断的Class对象
     * @return true表示是基本类型，false表示不是
     */
    private static boolean isPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }

        String name = clazz.getName();
        for (String primitiveTypeName : primitiveTypeNames) {
            if (name.equals(primitiveTypeName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 8种基本类型的包装类数组
     * <p>
     * 用于判断对象是否为包装类型
     * </p>
     */
    private static final Class<?>[] primitiveWrappers = {
            Boolean.class, Character.class, Byte.class, Short.class,
            Integer.class, Long.class, Float.class, Double.class
    };

    /**
     * 8种基本类型的类名数组
     * <p>
     * 用于通过类名判断是否为基本类型
     * </p>
     */
    private static final String[] primitiveTypeNames = {
            "boolean", "char", "byte", "short",
            "int", "long", "float", "double"
    };

}
