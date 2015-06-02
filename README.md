# cell-rpg

[![Join the chat at https://gitter.im/EmergentOrganization/cell-rpg](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/EmergentOrganization/cell-rpg?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

RPG based in a world where cellular automata have run rampant. 
Soon to be available on android (and more).
Developed using libGDX.

![concept art](http://i.imgur.com/T4zuBZe.png)

## Importing the Project
This project (and LibGDX) uses Gradle to install dependencies. Once you have cloned the project, follow [this link](https://github.com/libgdx/libgdx/wiki/Setting-up-your-Development-Environment-%28Eclipse%2C-Intellij-IDEA%2C-NetBeans%29) to set up your development environment for libGDX. You _need_ to install the following using the android sdk manager in order for gradlew to be able to build your project:

1. Android 5.0.x (API 21)
2. Android sdk build-tools rev 21.x.x

note: you do not need all of the system images, these are for using android emulators. Select only the ones you want to run to save disk space.

Once your development environment is ready, navigate to the root directory of the project, and run `./gradlew clean` (or `gradlew.bat` if you're on Windows) to download and install dependencies. This may take a couple of minutes.

Finally, import the project **as a Gradle Project** in your favorite IDE. [Follow LibGDX's import guide](https://github.com/libgdx/libgdx/wiki/Gradle-and-Eclipse) to import the project. That particular link is for Eclipse, but if you look on the table of contents, you will see setup guides for various other IDEs.

Please contact us on our [Gitter Chatroom](https://gitter.im/EmergentOrganization/cell-rpg) if you're having any trouble, and we'd be more than happy to assist.
