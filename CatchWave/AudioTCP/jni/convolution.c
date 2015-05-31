#include "com_example_audiotcp_DSPforJNI.h"

/*
 * BPF Convolution Module
 */

jfloat bpf[FilterLen] = { 0.4720, 0.6418, -0.0395, -0.1601, 0.1176, -0.0223,
		-0.0241, 0.0214, -0.0066 };

void javaConvolution(float* input) {
	__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG", "INTO CONVOLUTION");

	jint nconv = (2 * DataLen) + FilterLen - 1;
	jfloat tmp;
	jint i, j, k;

	jfloat result[nconv];

	for (i = 0; i < nconv; i++) {
		k = i;
		tmp = 0.0;
		for (j = 0; j < FilterLen; j++) {
			if (k >= 0 && k < DataLen) {
				tmp += (input[k] * bpf[j]);
			}
			k--;
			result[i] = tmp;
		}
	}

	for (i = 0; i < 2 * DataLen; i++) {
		input[i] = result[i];
	}
	__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG",
			"%f %f %f %f %f %f %f %f %f %f %f %f", input[0], input[1], input[2],
			input[3], input[4], input[5], input[6], input[7], input[8],
			input[9], input[10], input[11]);

}
