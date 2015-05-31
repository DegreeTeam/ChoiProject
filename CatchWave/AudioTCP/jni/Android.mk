LOCAL_PATH := $(call my-dir)
TARGET_ARCH:=arm

include $(CLEAR_VARS)

LOCAL_MODULE    := libdsp-eachmodule
LOCAL_SRC_FILES := convolution.c       \
				   functioncall.c	   \
				   upsampling.c		   \
				   interpolation.c     \
				   byte_convert.c
				   

include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)


LOCAL_MODULE :=libdsp-main
LOCAL_SRC_FILES := dsp_block.c    \
				   dsp_void.c

LOCAL_LDLIBS:= -llog

LOCAL_STATIC_LIBRARIES := libdsp-eachmodule

include $(BUILD_SHARED_LIBRARY)