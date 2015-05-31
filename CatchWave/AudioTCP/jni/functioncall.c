#include "com_example_audiotcp_DSPforJNI.h"

/*
 * FunctionCall Module
 */

void javaFunctionCall(JNIEnv * env, jobject obj, jbyteArray ret) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	jmethodID funcM = (*env)->GetMethodID(env, cls, "func", "([BII)V");
	(*env)->CallVoidMethod(env, obj, funcM, ret, 0, 64);
}

