# cell-rpg

[![Join the chat at https://gitter.im/EmergentOrganization/cell-rpg](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/EmergentOrganization/cell-rpg?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

RPG based in a world where cellular automata have run rampant. 
Soon to be available on android (and more).
Developed using libGDX.

![concept art](http://i.imgur.com/e6d5DJc.png)

## Getting Started
This project (and LibGDX) uses Gradle to install dependencies, but you _need_ to ensure your java environment and android sdk are properly set up first. We use primarily intellij IDEA to develop (but you can use others) and will do our best to guide you through the process. In addition to the guide below, you can find more information in [libGDX's dev environment set up guide](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29).

If you're having trouble, check the troubleshooting section at the bottom of this section and contact us on our [Gitter Chatroom](https://gitter.im/EmergentOrganization/cell-rpg); we're more than happy to assist.

### Set up Java
You will need Java Development Kit 7 (6 and 8 will not work! JREs will not work). 

On windows, download and install the appropriate JDK from Oracle.

On *buntu, `sudo apt-get install openjdk-7-jdk`. You may also need to set the `$JAVA_HOME` environment variable to your jdk dir (probably `usr/lib/jvm/java-7-openjdk` or similar). You can do this by editing `etc/environment`.

### Set up Android SDK

1. Install the Android Stand-alone SDK Tools (not the ADT bundle!) using (this link)[http://developer.android.com/sdk/installing.html].
2. [start the Android SDK Manager](http://developer.android.com/tools/help/sdk-manager.html).
3. install _exactly_ the following:

    * Android SDK build-tools rev 21.x.x
    * Android 5.0.x (API 21) > SDK Platform
    * you may install system images, but note that each is ~500mb and only needed to run the different android emulators. Select only the ones you want to run to save disk space, or none at all if you only want to run the desktop build.

4. You may need to set an environment variable, $ANDROID_HOME to the android sdk's directory.

### Install Intellij IDEA
D/l and install using included instructions [here](https://www.jetbrains.com/idea/download/).

### Install gradle dependencies
Once your development environment is ready, navigate to the root directory of the project, and run `./gradlew clean` (or `gradlew.bat` if you're on Windows) to download and install dependencies. This will take a few minutes.

Now that you have all of the dependencies you can make and run a build using `./gradlew desktop:run`. Please ensure this works before trying to import into intellij.

### import into Intellij
Finally, import the project **as a Gradle Project** in your favorite IDE. [Follow LibGDX's import guide](https://github.com/libgdx/libgdx/wiki/Gradle-and-Eclipse) to import the project. That particular link is for Eclipse, but if you look on the table of contents, you will see setup guides for various other IDEs.

#### run configurations:

* desktop game (application): `workingDir=\android\assets`, `mainClass=desktop.DesktopLauncher`
* texturePacker (application): `workingDir=\`, `mainClass=desktop.TexPacker`
* jUnit test suite (JUnit): `workingDir=\`, `mainClass=desktop.AllTestSuite`

### Troubleshooting
> `JDK Required: 'tools.jar' seems to be not in IDEA classpath. Please ensure JAVA_HOME points to JDK rather than JRE.`

Your `$JAVA_HOME` is pointing at an incomplete JDK or a JRE, verify that `$JAVA_HOME` is pointing to the right place, or try re-installing the JDK.

> *buntu: JAVA_HOME or ANDROID_HOME are set in `/etc/environment` are not working

You must log out and log back in to re-load the environment globally. To check these variables, open a terminal and enter `$JAVA_HOME` or `$ANDROID_HOME`.

> `.gradlew` won't run! 

Make sure it is marked as executable using `chmod +x ./gradlew`

> `.gradlew clean` FAILURE: SDK location not found

$ANDROID_HOME environment variable is not set properly. You may need to edit it or reboot.

> Gradle is giving me unusual build errors!

Ensure that you are using the copy of gradle provided in the repository (`gradlew`), or that you are using version `2.4`
