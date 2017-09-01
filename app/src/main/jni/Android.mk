LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := jni_study
LOCAL_SRC_FILES := jni_study.c
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)