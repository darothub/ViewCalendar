#include <jni.h>
#include <string>
extern "C"
JNIEXPORT jstring JNICALL

Java_com_darothub_viewcalendar_Keys_apiKey(JNIEnv *env, jobject thiz) {
    std::string api_key = "2a3dd3a40117fba520644457910ffc0b";
    return env->NewStringUTF(api_key.c_str());
}