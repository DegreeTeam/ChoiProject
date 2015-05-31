#include "com_example_audiotcp_DSPforJNI.h"
#include <stdlib.h>

JNIEXPORT jbyteArray JNICALL Java_com_example_audiotcp_DSPforJNI_DSPfromJNI(
		JNIEnv *env, jobject obj, jbyteArray input) {

	int i;
// INPUT
	jbyte * M = NULL;
	M = (*env)->GetByteArrayElements(env, input, NULL);
// RESULT
	jbyteArray ret = (*env)->NewByteArray(env, DataLen * 2);
	jbyte result[DataLen * 2];
	jfloat result1[DataLen * 2];
//
//	__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG",
//			"%d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d ",
//			(short) M[0], (short) M[1], (short) M[2], (short) M[3],
//			(short) M[4], (short) M[5], (short) M[6], (short) M[7],
//			(short) M[8], (short) M[9], (short) M[10], (short) M[11],
//			(short) M[12], (short) M[13], (short) M[14], (short) M[15],
//			(short) M[16], (short) M[17], (short) M[18], (short) M[19]);
//	__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG",
//			"%d %d %d %d %d %d %d %d %d %d %d %d ",
//			(short) M[20], (short) M[21], (short) M[22], (short) M[23],
//			(short) M[24], (short) M[25], (short) M[26], (short) M[27],
//			(short) M[28], (short) M[29], (short) M[30], (short) M[31]);

	javaUpSampling(M, result1);

//	__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG",
//			"%f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f %f ",
//			result1[1], result1[3], result1[5], result1[7], result1[9],
//			result1[11], result1[13], result1[15], result1[17], result1[19],
//			result1[21], result1[23], result1[25], result1[27], result1[29],
//			result1[31], result1[33], result1[35], result1[37], result1[39]);
//	__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG",
//			"%f %f %f %f %f %f %f %f %f %f %f %f ", result1[41], result1[43],
//			result1[45], result1[47], result1[49], result1[51], result1[53],
//			result1[55], result1[57], result1[59], result1[61], result1[63]);

	javaLinear_interpolation_filter(result1);

	//javaConvolution(result1);

	javaByte_Converter(result1, result);

	(*env)->SetByteArrayRegion(env, ret, 0, DataLen * 2, result);
	(*env)->ReleaseByteArrayElements(env, input, M, 0);

	//javaFunctionCall(env, obj, ret);
	return ret;

}
