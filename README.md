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
7. Run vJoyConf.exe in vJoy installation directory to add more gamepads and buttons.

### RaspberryPi/PC(Gamepad)
Install [python 2.7](https://www.python.org/).

### Android
1. Change ip address in MainActivity.java to PC(Game) ip address
2. Build application

# Usage
1. Connect PC(Game) and Android/RaspberryPi/PC(Gamepad) to same network.
2. Connect PC(Game) to TV using HDMI / VGA cable. (Optional)
3. Run server.js on PC(Game): node server.js
4. Connect gamepad to Android / RaspberryPi / PC(Gamepad).
5. Run Client.py on RaspberryPi / PC(Gamepad): python client.py PC(Game)-IP-ADDRESS or run Android application
6. Run the game.
7. Lean on the sofa and enjoy the game. (Requierd)

# Note
Android application detects a few gamepad models.You can add yours or even change input reading code entirely.
Also you can edit src/vJoyClient.cpp file to make it support your gamepad keys.
This config works for me to play old genesis games ^_^
Anyway you probably need to get your hands dirty to get this working as you want.
