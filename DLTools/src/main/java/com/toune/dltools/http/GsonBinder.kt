package com.toune.dltools.http

import android.text.TextUtils
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Administrator on 2018/1/11 0011.
 */
object GsonBinder {
    //定义并配置gson
    private val gson: Gson = GsonBuilder() //建造者模式设置不同的配置
        .serializeNulls() //序列化为null对象
        .setDateFormat("yyyy-MM-dd HH:mm:ss") //设置日期的格式
        .disableHtmlEscaping() //防止对网址乱码 忽略对特殊字符的转换
        .registerTypeAdapter(String::class.java, StringConverter()) //对为null的字段进行转换
        .create()

    /**
     * 对解析数据的形式进行转换
     *
     * @param obj 解析的对象
     * @return 转化结果为json字符串
     */
    fun toJsonStr(obj: Any?): String {
        return if (obj == null) {
            ""
        } else try {
            gson.toJson(obj)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 解析为一个具体的对象
     *
     * @param json 要解析的字符串
     * @param obj  要解析的对象
     * @param <T>  将json字符串解析成obj类型的对象
     * @return
    </T> */
    fun <T> toObj(json: String?, obj: Class<T>?): T? {
        //如果为null直接返回为null
        return if (obj == null || TextUtils.isEmpty(json)) {
            null
        } else try {
            gson.fromJson(json, obj)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @return 不区分类型 传什么解析什么
     */
    fun <T> toObj(jsonStr: String?, type: Type?): T? {
        var t: T? = null
        try {
            t = gson.fromJson(jsonStr, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return t
    }

    /**
     * 将Json数组解析成相应的映射对象列表
     * 解决类型擦除的问题
     */
    fun <T> toList(jsonStr: String?, clz: Class<T>): List<T> {
        var list: List<T> = gson.fromJson(jsonStr, type(clz))
        if (list == null) list = ArrayList()
        return list
    }

    fun <T> toMap(jsonStr: String?, clz: Class<T>): Map<String?, T> {
        var map: Map<String?, T> = gson.fromJson(jsonStr, type(clz))
        if (map == null) map = HashMap()
        return map
    }

    fun toMap(jsonStr: String?): Map<String, Any> {
        val type: Type = object : TypeToken<Map<String?, Any?>?>() {}.type
        return gson.fromJson(jsonStr, type)
    }

    private class type(private val type: Type) : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(type)
        }

        override fun getRawType(): Type {
            return ArrayList::class.java
        }

        override fun getOwnerType(): Type? {
            return null
        }
    }

    /**
     * 实现了 序列化 接口    对为null的字段进行转换
     */
    internal class StringConverter : JsonSerializer<String?>, JsonDeserializer<String?> {
        //字符串为null 转换成"",否则为字符串类型
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): String {
            return json.getAsJsonPrimitive().getAsString()
        }

        override fun serialize(
            src: String?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return if (src == null || src == "null") JsonPrimitive("") else JsonPrimitive(src.toString())
        }
    }
}