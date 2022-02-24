# FXGl example game as described on Oracle Java Magazine

A JavaFX/FXGL example game.

## About the project

A full description will be published on Oracle Java Magazine in different posts. For each post a separate branch is
created.

### Post 1 - "Building a game with JavaFX and FXGL"

Article: ["Look out, Duke! How to build a Java game with JavaFX and the FXGL library"](https://blogs.oracle.com/javamagazine/java-javafx-fxgl-game-development)

> Branch "main"

### Post 2 - "Controlling a JavaFX game with a joystick on Raspberry Pi"

Article: ["Look out, Duke! Part 2: Control a Java game with a Raspberry Pi"](https://blogs.oracle.com/javamagazine/post/look-out-duke-part-2-control-a-java-game-with-a-raspberry-pi-and-a-joystick)

> Branch "pi4j"

### Post 3 - "Building a JavaFX game as a native application"

For this article all dependencies have been pushed to the latest versions (of beginning of 2022).

> Branch "native"

Based on: https://docs.gluonhq.com/#platforms_linux_github_actions

## Basic Requirements

A list of the basic requirements can be found online in
the [Gluon Client documentation](https://docs.gluonhq.com/client/#_requirements).

## Quick instructions

Building on PC with GraalVM.

### GraalVM via SDKMAN

GraalVM can be installed with SDKMAN with the following commands, but this is not the ideal approach as Gluon is
providing a version which is based on the latest GraalVM with some modifications to improve the build process of JavaFX
applications.

* Use sdkman - https://sdkman.io/
* Install GraalVM with `sdk install java 22.0.0.2.r17-grl`
* Set environment variable with `export GRAALVM_HOME=${SDKMAN_CANDIDATES_DIR}/java/22.0.0.2.r17-grl`
* Check variable with `echo $GRAALVM_HOME`

### GraalVM by Gluon for JavaFX

Check for the download link for your platform on https://github.com/gluonhq/graal/releases/tag/gluon-22.0.0.3-Final

#### On MacOS

```shell 
brew install wget
cd ~/Downloads
wget https://github.com/gluonhq/graal/releases/download/gluon-22.0.0.3-Final/graalvm-svm-java17-darwin-gluon-22.0.0.3-Final.zip
unzip graalvm-svm-java17-darwin-gluon-22.0.0.3-Final.zip
export GRAALVM_HOME=~/Downloads/graalvm-svm-java17-darwin-gluon-22.0.0.3-Final/Contents/Home
```

#### On Linux

```shell 
cd ~/Downloads
wget https://github.com/gluonhq/graal/releases/download/gluon-22.0.0.3-Final/graalvm-svm-java17-linux-gluon-22.0.0.3-Final.zip
unzip graalvm-svm-java17-linux-gluon-22.0.0.3-Final.zip
export GRAALVM_HOME=~/Downloads/graalvm-svm-java17-linux-gluon-22.0.0.3-Final
```

### Configurations used to compile to native

GraalVM needs some settings files that can be auto-generated with:

```shell
$ mvn gluonfx:runagent
```

The generated files can be found in `src/resources/META-INF/native-image/`.

### Dependencies on Linux

On Linux extra dependencies are needed:

```shell
sudo apt install libasound2-dev libavcodec-dev libavformat-dev libavutil-dev libgl-dev libgtk-3-dev libpango1.0-dev libxtst-dev
```

### Maven build steps

#### Run the sample

```shell
mvn javafx:run
```

#### Run the sample as a native android image

```shell
mvn -DconsoleProcessLog=true -Pandroid gluonfx:build gluonfx:package
```

#### Build the sample as a native desktop application

```shell
mvn -Pdesktop gluonfx:build gluonfx:package
```

#### Build the sample as an Android application

```shell
mvn -Pandroid gluonfx:build gluonfx:package
```

## Debug crashes on Android device

* Connect with USB cable
* Enable USB debug in Settings
* Install Android Studio on PC
* In terminal on PC:

```shell
$ ~/Android/Sdk/platform-tools/adb logcat | grep magazine

$ ~/Android/Sdk/platform-tools/adb install JavaMagazineFXGLDemo.apk
$ ~/Android/Sdk/platform-tools/adb logcat | grep magazine
```

### Temporary fix for Android

Problem at startup: `cannot locate symbol "JNI_OnLoad_javajpeg"` which is related to the issue https://github.com/gluonhq/substrate/pull/1000, about missing awt symbols.

While we haven't solved that yet, we have just integrated a PR that will help you solve this at least until we have a
better approach. Create a simple c file (i.e. missing_symbols.c) that contains the dummy methods for the missing
symbols, following this https://github.com/gluonhq/substrate/pull/1000/files.

```c
# include <stdlib.h>

void JNI_OnLoad_javajpeg() { 
    fprintf(stderr, "We should never reach here (JNI_OnLoad_javajpeg)\n"); 
} 

...
```

Compile it for Android with Android ND (https://developer.android.com/studio/projects/install-ndk). To install CMake and the default NDK in Android Studio, do the following:

* With a project open, click Tools > SDK Manager.
* Click the SDK Tools tab.
* Select the NDK (Side by side) and CMake checkboxes.

```shell
$ ~/Android/Sdk/ndk/23.1.7779620/toolchains/llvm/prebuilt/linux-x86_64/bin/clang -c -target aarch64-linux-android -I. android/missing_symbols.c
```

See that it creates `missing_symbols.o`

Using the 1.0.13-SNAPSHOT version, add it to the plugin, and add the Sonatype repo for the snapshot:

```xml

<pluginRepositories>
    <pluginRepository>
        <id>Snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </pluginRepository>
</pluginRepositories>
```

```xml

<plugin>
    <groupId>com.gluonhq</groupId>
    <artifactId>gluonfx-maven-plugin</artifactId>
    <version>${gluonfx.maven.version}</version>
    <configuration>
        <linkerArgs>
            <arg>/path/to/symbols_awt.o</arg>
        </linkerArgs>
        ...
    </configuration>
</plugin>
```

Then build, package and install as usual, now it should work (I've got it running on my Android device...)

## Google Console

### Create a service account

* https://console.cloud.google.com
*

## Google Play Store

### Create an app

* https://play.google.com/console
* "Create app"

### Users and permissions

## GitHub Actions

https://docs.gluonhq.com/#_the_gluon_client_plugin_for_maven

### Secrets

* ANDROID_KEYALIAS
* ANDROID_KEYALIAS_PASSWORD
* ANDROID_KEYSTORE_BASE64
* ANDROID_KEYSTORE_PASSWORD
* ANDROID_SERVICE_ACCOUNT_JSON
* GLUON_LICENSE

## Credits

* Cloud computer icon from [flaticon.com](https://www.flaticon.com) > [srip](https://www.flaticon.com/authors/srip)
