#include "com_example_audiotcp_DSPforJNI.h"

void javaByte_Converter(float* data, char* output) {
	int i;
	for (i = 0; i < 2 * DataLen; i++) {
		data[i] *= 127;
		output[i] = (char) (data[i]);
	}

}
