#include "com_example_audiotcp_DSPforJNI.h"


JNIEXPORT void JNICALL Java_com_example_audiotcp_DSPforJNI_DSPfromLOG
  (JNIEnv *env , jobject obj){

	__android_log_print(ANDROID_LOG_DEBUG, "NDK_LOG", "INTO_NDK");

}
