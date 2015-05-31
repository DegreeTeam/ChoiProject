#include "com_example_audiotcp_DSPforJNI.h"

void javaUpSampling(char* poper, float* result) {
	__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG", "INTO UPSAMPLING");
	int i;
	const float upto = 128.0;
	for (i = 0; i < DataLen; i++) {
		result[DOWN_FACTOR * i + 1] = (float) poper[i] / upto;
	}

//	for (i = 0; i < DataLen * 2; i++) {
//		__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG", " %d : %d\n", i,
//				result[i]);
//	}
}

