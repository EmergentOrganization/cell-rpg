# cell-rpg

RPG based in a world where cellular automata have run rampant. 
Developed using libGDX.
All rights reserved

* [concept art](http://imgur.com/a/5sx9C)
* [concept/demo gifs/gfys](http://www.gfycat.com/7yl4r/planiverse_bridge)
* [demo videos](https://www.youtube.com/playlist?list=PL-nLMUFdPaNRnWnwuSzN5RnzLE7Iey3cB)
* [game-world wiki](http://planiverse-bridge.wikia.com/wiki/Planiverse_bridge_Wikia)
* [post a feature request on Trello](https://trello.com/b/GG9NKgSf/feature-requests)
* dev chat : [![Join the chat at https://gitter.im/EmergentOrganization/cell-rpg](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/EmergentOrganization/cell-rpg?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
 
[![GitHub release](https://img.shields.io/github/release/EmergentOrganization/cell-rpg.svg)](https://github.com/EmergentOrganization/cell-rpg/releases)
[![Build Status](https://api.travis-ci.org/EmergentOrganization/cell-rpg.svg)](https://travis-ci.org/EmergentOrganization/cell-rpg) 
[![Bountysource](https://img.shields.io/bountysource/team/emergent-organization/activity.svg)](https://www.bountysource.com/teams/emergent-organization)
[![Code Climate](https://codeclimate.com/github/EmergentOrganization/cell-rpg/badges/gpa.svg)](https://codeclimate.com/github/EmergentOrganization/cell-rpg)
[![ghit.me hit counter](https://ghit.me/badge.svg?repo=EmergentOrganization/cell-rpg)](https://ghit.me/repo/EmergentOrganization/cell-rpg)


## Developer Setup
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

### Import into Intellij
Finally, import the project **as a Gradle Project** in your favorite IDE. [Follow LibGDX's import guide](https://github.com/libgdx/libgdx/wiki/Gradle-and-Eclipse) to import the project. That particular link is for Eclipse, but if you look on the table of contents, you will see setup guides for various other IDEs.

[//]: # (### Update submodules)

[//]: # (Navigate to the root directory of the project in a shell/cmd and run the following commands:)

[//]: # (* `git submodule init`)

[//]: # (* `git submodule update`)

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

> Gradle's stacktrace isn't formatted with symbolic links to code in IntelliJ IDEA

With the recent addition of IDEA version 15, java-grade and android-gradle misbehave when they are in the same project. 
To fix this, open the desktop module settings in IntelliJ, and remove any references to android-gradle under *Facets*

> ArrayIndexOutOfBoundsException: 0 at LinuxDisplay.getAvailableDisplayModes(LinuxDisplay.java:951)

This seems to occur when running the project on an xorg-server with more than one monitor attached. Ensure that `xorg-xrandr` is installed and configured correctly.

## Getting Started

### Project Structure

```
# build-targets
android/
desktop/
html/
ios/

# main codebase
core/src/io.github.emergentorganization.cellrpg/
    /PixelonTransmission    # main game class
    /components/            # component classes used in entity-component system
    /core/                  # classes deemed generalizable enough for possible use in other projects
        /entityfactory/
                /Entities/    # entities for entity-component system
    /events/        # event classes for pub/sub event system
    /input/         # player input controllers
    /managers/      # classes which act as middlemen in use of assets by systems
    /scenes/        # in-game scene classes
    /systems/       # controllers for entity components
    /tools/
        /mapeditor/ # in-game map editor classes
        /menus/     # in-game GUI menu classes
        /mixpanel/  # metric collection/reporting
        /physics/   
        /postprocessing/    # custom shaders
        /profiling/         # helper scripts for performance testing 
        /saves/             # game state i/o helpers

# raw assets not *directly* used by game
art/  # NOTE: !!! assets used by all targets are in `android/assets/resources`

# helper libraries
aurelienribon/
profilingViolinist/
```

### Basic Concepts
The codebase is organized using the Entity–component–system pattern. 
In short, every in-game object has an entity which is composed of components.
Component behavior is controlled via various systems.
This allows for a wide variety of entity attributes to be shared while maximizing performance (since systems don't waste
time on entities without relevant components) and minimizing entity boilerplate (entity definition is primarily a listing
of components and values which define the entity).

Events in the game are organized using the publish-subscribe pattern.
Using this pattern, entities and systems can respond to a wide variety of contextual events without needing to maintain 
game state information; they need only to subscribe to the events of interest on the entities they care about.

### Workflows
My most typical workflow goes something like:
1. create/modify component/entity/system
2. use RunTests build configuration to verify new functionality
3. modify `./android/assets/resources/log4j2.xml` to enable detailed logging on classes I am working on
4. test game manually using desktopGame build configuration
5. commit & push to github if working (branch if I want to commit something that isn't working)

If modifying art assets, the steps are a bit different:
1. modify svg asset in ./art/ using inkscape
2. export frames named 0.png, 1.png, etc to a directory in `android/assets/resources/textures/unpacked`
3. ~~use `spritify.sh` script to collect sprites into intermediate spritesheet~~ 
4. use packTextures build configuration to automatically generate spritesheets & TexturePack.atlas
5. use aurelienribon/physics-body-editor.jar to open & modify `android/assets/resources/data/colliderProject`

For particle effects:
1. use libgdx-particle-editor to create particle effects & save them to `android/assets/resources/particleEffects`

### Other Random Notes
There are a lot of random experiments sprinkled in here that might catch your interest. For example:

* one of the Cellular Automata Systems (CASystems) implements a Digital Gene Regulatory Network (DGRN) that gives each cell a hereditary genetic code.
* the music system attempts to dynamically combine multiple track loops based on the intensity of the current gameplay.
* the TimingSystem tries to align enemy spawns with musical changes such that in-game sounds might be on tempo.
