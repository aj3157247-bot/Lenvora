@ECHO OFF
SET APP_HOME=%~dp0
SET CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar
IF NOT EXIST "%CLASSPATH%" (
  ECHO gradle-wrapper.jar is missing. Generate the wrapper once with Gradle 8.9 and commit it.
  EXIT /B 1
)
IF DEFINED JAVA_HOME (SET JAVACMD=%JAVA_HOME%\bin\java.exe) ELSE (SET JAVACMD=java.exe)
"%JAVACMD%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
