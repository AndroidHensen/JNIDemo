[toc]


## 前言

要学习NDK之前，我们得先在AndroidStudio中学习JNI，还有C和C++基础，这些都是接触NDK的前提，那么废话不多说，开始吧

对于NDK在AndroidStudio的配置，可以关注我的博客找到相关文章，本文章是基于配置好NDK环境之后来操作的

## 效果预览

![这里写图片描述](http://img.blog.csdn.net/20170901123609445?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMzAzNzk2ODk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

github：https://github.com/AndroidHensen/JNIDemo

## 导入依赖库

我们先导入我们需要用到的依赖库

```
#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <string.h>

#include <android/log.h>
```

## 宏定义

定义两个宏函数，用于Log输出调试
```
#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"TAG",FORMAT,__VA_ARGS__)
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"TAG",FORMAT,__VA_ARGS__)
```
这里使用到AndroidLog的输出，需要手动在mk中导入，或者在Gradle的ndk配置中

```
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := jni_study
LOCAL_SRC_FILES := jni_study.c
LOCAL_LDLIBS := -llog //Log打印
include $(BUILD_SHARED_LIBRARY)
```

```
ndk{
    moduleName "jni_study"
    ldLibs "log" //Log打印
    abiFilters "armeabi", "armeabi-v7a", "x86"
}
```

## 访问Java属性

```c
JNIEXPORT jstring JNICALL Java_com_handsome_ndkdemo_MainActivity_accessField
        (JNIEnv *env, jobject jobj){
    //获取对象的class
    jclass cls = (*env)->GetObjectClass(env, jobj);
    //获取属性id，参数二：class对象，参数三：属性名，参数四：属性签名
    jfieldID fid = (*env)->GetFieldID(env, cls, "key", "Ljava/lang/String;");
    //获取属性值
    jstring jstr = (*env)->GetObjectField(env, jobj, fid);
    //isCopy代表：函数内部是否已经复制，复制了返回JNI_TRUE，没复制返回JNI_FALSE
    jboolean isCopy = NULL;
    //转换成c语言识别的字符串
    char *c_str = (*env)->GetStringUTFChars(env,jstr,&isCopy);
    //拼接字符串
    char text[20] = "myName is";
    strcat(text,c_str);
    //生成Java识别的字符串
    jstring new_jstr = (*env)->NewStringUTF(env,text);
    //修改属性的值
    (*env)->SetObjectField(env,jobj,fid,new_jstr);
    //只要使用了GetStringUTFChars，官方建议一定要释放
    (*env)->ReleaseStringUTFChars(env, jstr, c_str);
    return new_jstr;
}
```
## 访问java静态属性

```c
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_accessStaticField
        (JNIEnv *env, jobject jobj){
    //获取对象的class
    jclass cls = (*env)->GetObjectClass(env, jobj);
    //获取属性id，参数二：class对象，参数三：属性名，参数四：属性签名
    jfieldID fid = (*env)->GetStaticFieldID(env, cls, "count", "I");
    //获取属性值
    jint count = (*env)->GetStaticIntField(env, cls, fid);
    //++操作
    count++;
    //修改属性的值
    (*env)->SetStaticIntField(env,cls,fid,count);
}
```

## 访问java方法

```c
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_accessMethod
(JNIEnv *env, jobject jobj){
    //jclass
    jclass cls = (*env)->GetObjectClass(env, jobj);
    //获取方法id，参数二：class对象，参数三：方法名，参数四：方法签名
    jmethodID mid = (*env)->GetMethodID(env, cls, "genRandomInt", "(I)I");
    //调用方法
    jint random = (*env)->CallIntMethod(env, jobj, mid, 200);
}
```

## 访问java静态方法

```c
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_accessStaticMethod
(JNIEnv *env, jobject jobj){
    //jclass
    jclass cls = (*env)->GetObjectClass(env, jobj);
    //获取方法id，参数二：class对象，参数三：方法名，参数四：方法签名
    jmethodID mid = (*env)->GetStaticMethodID(env, cls, "getUUID", "()Ljava/lang/String;");
    //调用方法
    jstring uuid = (*env)->CallStaticObjectMethod(env, cls, mid);
}
```

## 访问构造方法

使用java.util.Date产生一个当前的时间戳

```c
JNIEXPORT jobject JNICALL Java_com_handsome_ndkdemo_MainActivity_accessConstructor
(JNIEnv *env, jobject jobj){
    //jclass
    jclass cls = (*env)->FindClass(env, "java/util/Date");
    //获取方法id，参数二：class对象，参数三：方法名，参数四：方法签名
    jmethodID constructor_mid = (*env)->GetMethodID(env, cls, "<init>", "()V");
    //创建对象
    jobject date_obj = (*env)->NewObject(env , cls, constructor_mid);
    //获取方法id，参数二：class对象，参数三：方法名，参数四：方法签名
    jmethodID mid = (*env)->GetMethodID(env , cls, "getTime", "()J");
    //调用方法
    jlong time = (*env)->CallLongMethod(env, date_obj, mid);
    printf("time:%lld\n",time);
    return date_obj;
}
```
## 调用父类的方法

该方法出错，还未找到解决方法

```c
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_accessNonvirtualMethod
(JNIEnv *env, jobject jobj){
    //jclass
    jclass cls = (*env)->GetObjectClass(env, jobj);
    //获取human对象id
    jfieldID fid = (*env)->GetFieldID(env, cls, "human", "Lcom/handsome/ndkdemo/Bean/Human;");
    //获取human这个对象
    jobject human_obj = (*env)->GetObjectField(env, jobj, fid);

    //获取父类Human的jclass
    jclass human_cls = (*env)->FindClass(env, "com/handsome/ndkdemo/Bean/Human");
    //获取父类Human的sayHi的方法
    jmethodID mid = (*env)->GetMethodID(env, human_cls, "sayHi", "()V");
    //调用的父类的方法
    (*env)->CallNonvirtualObjectMethod(env, human_obj, human_cls, mid);
}
```

## 字符串转码

字符串转码，c字符串直接给jstring时，由于编码不同会出现中文乱码问题，解决方法是采用Java的String(byte bytes[], String charsetName)构造方法

```c
JNIEXPORT jstring JNICALL Java_com_handsome_ndkdemo_MainActivity_chineseChars
(JNIEnv *env, jobject jobj){
    char *c_str = "马蓉与宋江";
    //第一步：jmethodID
    jclass str_cls = (*env)->FindClass(env, "java/lang/String");
    jmethodID constructor_mid = (*env)->GetMethodID(env, str_cls, "<init>", "([BLjava/lang/String;)V");

    //第二步：创建byte数组
    jbyteArray bytes = (*env)->NewByteArray(env, strlen(c_str));
    //byte数组赋值
    (*env)->SetByteArrayRegion(env, bytes, 0, strlen(c_str), c_str);

    //第三步：字符编码jstring
    jstring charsetName = (*env)->NewStringUTF(env, "UTF-8");

    //调用构造函数，返回编码之后的jstring
    return (*env)->NewObject(env,str_cls,constructor_mid,bytes,charsetName);
}
```

## 给定一个数组进行排序并同步到Java中

```
int compare(int *a,int *b){
    return (*a) - (*b);
}

JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_giveArray
        (JNIEnv *env, jobject jobj,jintArray arr){
    //jintArray -> jint指针 -> c int 数组
    jint *elems = (*env)->GetIntArrayElements(env, arr, NULL);
    //数组的长度
    int len = (*env)->GetArrayLength(env, arr);
    //排序，参数三：排序函数
    qsort(elems, len, sizeof(jint), compare);
    //同步，以下对第四个参数的解释
    //0, Java数组进行更新，并且释放C/C++数组
    //JNI_ABORT, Java数组不进行更新，但是释放C/C++数组
    //JNI_COMMIT，Java数组进行更新，不释放C/C++数组（函数执行完，数组还是会释放）
    (*env)->ReleaseIntArrayElements(env, arr, elems, JNI_COMMIT);
}
```

## 创建一个指定大小的数组

```
JNIEXPORT jintArray JNICALL Java_com_handsome_ndkdemo_MainActivity_getArray
        (JNIEnv *env, jobject jobj, jint len){
    jintArray jint_arr = (*env)->NewIntArray(env, len);
    jint *elems = (*env)->GetIntArrayElements(env, jint_arr, NULL);
    int i = 0;
    for (; i < len; i++){
        elems[i] = i;
    }
    //同步
    (*env)->ReleaseIntArrayElements(env, jint_arr, elems, 0);
    return jint_arr;
}
```

## 局部变量引用

```
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_localRef
        (JNIEnv *env, jobject jobj){
    int i = 0;
    for (; i < 5; i++){
        //创建Date对象
        jclass cls = (*env)->FindClass(env, "java/util/Date");
        jmethodID constructor_mid = (*env)->GetMethodID(env, cls, "<init>", "()V");
        jobject obj = (*env)->NewObject(env, cls, constructor_mid);
        //此处省略一百行代码...
        //通知垃圾回收器回收这些对象
        (*env)->DeleteLocalRef(env, obj);
    }
}
```
## 创建全局引用

```
//全局引用，可以共享(跨多个线程)，手动控制内存使用
jstring global_str;

JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_createGlobalRef
        (JNIEnv *env, jobject jobj){
    jstring obj = (*env)->NewStringUTF(env, "jni development is powerful!");
    global_str = (*env)->NewGlobalRef(env, obj);
}
```
## 获得全局引用

```
JNIEXPORT jstring JNICALL Java_com_handsome_ndkdemo_MainActivity_getGlobalRef
        (JNIEnv *env, jobject jobj){
    return global_str;
}
```
## 释放全局引用

```
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_deleteGlobalRef
        (JNIEnv *env, jobject jobj){
    (*env)->DeleteGlobalRef(env, global_str);
}
```

## 弱全局引用

* 节省内存，在内存不足时可以是释放所引用的对象
* 可以引用一个不常用的对象，如果为NULL，临时创建
* 创建：NewWeakGlobalRef,销毁：DeleteGlobalWeakRef



## 抛出异常处理

异常处理做法

1. Java层捕获JNI自己抛出的Throwable异常
2. 用户通过清空异常，然后抛出ThrowNew新的异常
```
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_exeception
        (JNIEnv *env, jobject jobj){
    jclass cls = (*env)->GetObjectClass(env, jobj);
    //会抛出异常
    jfieldID fid = (*env)->GetFieldID(env, cls, "key2", "Ljava/lang/String;");
    //检测是否发生Java异常
    jthrowable exception = (*env)->ExceptionOccurred(env);
    if (exception != NULL){
        //清空异常信息，为了让Java代码可以继续运行
        (*env)->ExceptionClear(env);
        //补救措施
        fid = (*env)->GetFieldID(env, cls, "key", "Ljava/lang/String;");
    }
    //获取属性的值
    jstring jstr = (*env)->GetObjectField(env, jobj, fid);
    char *str = (*env)->GetStringUTFChars(env, jstr, NULL);
    //对比属性值是否合法
    if (strcmp(str, "super Hensen") != 0){
        //不合法则抛出异常，给Java层处理
        jclass newExcCls = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env,newExcCls,"key's value is invalid!");
    }
}
```
## 局部静态变量

```
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_cached
        (JNIEnv *env, jobject jobj){
    jclass cls = (*env)->GetObjectClass(env, jobj);
    //局部静态变量
    static jfieldID key_id = NULL;
    if (key_id == NULL){
        //只执行一次代码
        key_id = (*env)->GetFieldID(env, cls, "key", "Ljava/lang/String;");
    }
}
```

## 初始化全局变量

```
jfieldID key_fid;
jmethodID random_mid;

JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_MainActivity_initIds
        (JNIEnv *env, jclass jcls){
    key_fid = (*env)->GetFieldID(env, jcls, "key", "Ljava/lang/String;");
    random_mid = (*env)->GetMethodID(env, jcls, "genRandomInt", "(I)I");
}
```

## 文件加密

```
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_Utils_FileUtils_crypt
(JNIEnv *env, jclass jcls, jstring normal_path_jstr,jstring crypt_path_jstr) {
    const char* normal_path = (*env)->GetStringUTFChars(env, normal_path_jstr, NULL);
    const char* cpypt_path = (*env)->GetStringUTFChars(env, crypt_path_jstr, NULL);
    FILE *normal_fp = fopen(normal_path,"rb");
    FILE *cpypt_fp = fopen(cpypt_path,"wb");
    int ch;
    while((ch = fgetc(normal_fp)) != EOF){
        fputc(ch ^ 9 ,cpypt_fp);
    }
    fclose(normal_fp);
    fclose(cpypt_fp);
}
```

## 文件解密

```
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_Utils_FileUtils_decrypt
(JNIEnv * env, jclass jcls, jstring crypt_path_jstr, jstring decrypt_path_jstr) {
    const char* normal_path = (*env)->GetStringUTFChars(env, crypt_path_jstr, NULL);
    const char* cpypt_path = (*env)->GetStringUTFChars(env, decrypt_path_jstr, NULL);
    FILE *normal_fp = fopen(normal_path,"rb");
    FILE *cpypt_fp = fopen(cpypt_path,"wb");
    int ch;
    while((ch = fgetc(normal_fp)) != EOF){
        fputc(ch ^ 9 ,cpypt_fp);
    }
    fclose(normal_fp);
    fclose(cpypt_fp);
}
```

## 获取文件大小

```
long get_file_size(char *path){
    FILE *fp = fopen(path,"r");
    fseek(fp,0,SEEK_END);
    return ftell(fp);
}
```

## 分割文件

```
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_Utils_FileUtils_diff
        (JNIEnv *env, jclass jcls, jstring path_jstr,jstring path_pattern_jstr, jint file_num){
    const char* path = (*env)->GetStringUTFChars(env,path_jstr,NULL);
    const char* path_pattern = (*env)->GetStringUTFChars(env,path_pattern_jstr,NULL);
    //得到分割之后的子文件的路径列表
    char **patches = malloc(sizeof(char*) * file_num);
    int i = 0;
    for (; i < file_num; i++) {
        patches[i] = malloc(sizeof(char) * 100);
        //元素赋值
        sprintf(patches[i], path_pattern, (i+1));
        LOGI("patch path:%s",patches[i]);
    }

    int filesize = get_file_size(path);
    FILE *fpr = fopen(path,"rb");

    //不断读取path文件，循环写入file_num个文件中
    //如果文件大小和文件数目整除，则每个文件大小为 filesize / file_num
    //如果文件大小和文件数目不整除，则每个文件大小为 filesize / (file_num - 1)，最后一个文件大小为 filesize % (file_num - 1)
    if(filesize % file_num == 0){
        //单个文件大小
        int part = filesize / file_num;
        i = 0;
        //逐一写入不同的分割子文件中
        for (; i < file_num; i++) {
            FILE *fpw = fopen(patches[i], "wb");
            int j = 0;
            for(; j < part; j++){
                //边读边写
                fputc(fgetc(fpr),fpw);
            }
            fclose(fpw);
        }
    }else{
        int part = filesize / (file_num - 1);
        i = 0;
        //逐一写入不同的分割子文件中
        for (; i < file_num - 1; i++) {
            FILE *fpw = fopen(patches[i], "wb");
            int j = 0;
            for(; j < part; j++){
                //边读边写
                fputc(fgetc(fpr),fpw);
            }
            fclose(fpw);
        }
        //最后一个文件
        FILE *fpw = fopen(patches[file_num - 1], "wb");
        i = 0;
        for(; i < filesize % (file_num - 1); i++){
            fputc(fgetc(fpr),fpw);
        }
        fclose(fpw);
    }

    //关闭文件流
    fclose(fpr);
    //释放动态内存
    i = 0;
    for(; i < file_num; i++){
        free(patches[i]);
    }
    free(patches);

    (*env)->ReleaseStringUTFChars(env,path_jstr,path);
    (*env)->ReleaseStringUTFChars(env,path_pattern_jstr,path_pattern);
}
```
## 合并文件


```
JNIEXPORT void JNICALL Java_com_handsome_ndkdemo_Utils_FileUtils_patch
        (JNIEnv *env, jclass jcls,jstring path_pattern_jstr, jint file_num,jstring merge_path_jstr){
    const char* merge_path = (*env)->GetStringUTFChars(env,merge_path_jstr,NULL);
    const char* path_pattern = (*env)->GetStringUTFChars(env,path_pattern_jstr,NULL);
    //得到分割之后的子文件的路径列表
    char **patches = malloc(sizeof(char*) * file_num);
    int i = 0;
    for (; i < file_num; i++) {
    patches[i] = malloc(sizeof(char) * 100);
    //元素赋值
        sprintf(patches[i], path_pattern, (i+1));
        LOGI("patch path:%s",patches[i]);
    }

    FILE *fpw = fopen(merge_path,"wb");

    //把所有的分割文件读取一遍，写入一个总的文件中
    i = 0;
    for(; i < file_num; i++){
        int filesize = get_file_size(patches[i]);
        FILE *fpr = fopen(patches[i], "rb");
        int j = 0;
        for (; j < filesize; j++) {
            fputc(fgetc(fpr),fpw);
        }
        fclose(fpr);
    }

    //关闭文件流
    fclose(fpw);
    //释放动态内存
    i = 0;
    for(; i < file_num; i++){
        free(patches[i]);
    }
    free(patches);

    (*env)->ReleaseStringUTFChars(env,path_pattern_jstr,path_pattern);
    (*env)->ReleaseStringUTFChars(env,merge_path_jstr,merge_path);
}
```




