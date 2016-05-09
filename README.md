# Remote Gamepad

Turn your wired gamepad into wireless with this simple project.

# Installation

### PC(Game)
1. Install [nodejs](https://nodejs.org/en/)
2. Install [mingw64](http://mingw-w64.org/doku.php/download) Mingw-builds.
3. Download and install [vJoySetup.exe](https://sourceforge.net/projects/vjoystick/files/).
4. Clone the project.
5. Copy vJoyInterface.dll from vJoy installation directory to project directory.
6. Run compile.bat

### RaspberryPi/PC(Gamepad)
Install [python 2.7](https://www.python.org/).

### Android
Change ip address in MainACtivity.java to PC(Game) ip address

# Usage
1. Connect PC(Game) and RaspberryPi/PC(Gamepad) to same network.
2. Connect PC(Game) to TV using HDMI / VGA cable. (Optional)
3. Run server.js on PC(Game): node server.js
4. Connect gamepad to RaspberryPi / PC(Gamepad).
5. Run Client.py on RaspberryPi / PC(Gamepad): python client.py PC(Game)-IP-ADDRESS or run Android app
6. Run the game.
7. Lean on the sofa and enjoy the game. (Requierd)

# Note
Android application detects a few gamepad models.Feel free to add yours or even change input reading code entirely.
Also you can edit src/vJoyClient.cpp file to add more axes or buttons.This config works for me to play old retro games with my gamepad ^_^
