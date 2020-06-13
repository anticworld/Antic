@echo off

set java_bin=.\jvm\bin\java

%java_bin% -cp antic.jar org.antic.JvmOptions --gui > jvm_options.txt
set /p jvm_options=<jvm_options.txt

%java_bin% %jvm_options% -cp antic.jar org.antic.Main --gui %*
