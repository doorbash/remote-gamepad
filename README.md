# Remote Gamepad

Turn your wired gamepad into wireless with this simple project.

[gamepad]----[RaspberryPi/PC(Gamepad)]~~~~~~~~~~~~~~ WiFi ~~~~~~~~~~~~~~[PC(Game)]----[TV]

# Installation

### PC(Game)
1. Install [nodejs](https://nodejs.org/en/)
2. Install [mingw64](http://mingw-w64.org/doku.php/download) Mingw-builds.
3. Download and install [vJoySetup.exe](https://sourceforge.net/projects/vjoystick/files/).

### RaspberryPi/PC(Gamepad)
1. Clone the project.
2. Copy vJoyInterface.dll from vJoy installation directory to project directory.
3. Run compile.bat
4. Install [python 2.7](https://www.python.org/).

# Usage
1. Connect PC(Game) and RaspberryPi/PC(Gamepad) to same network.
2. Connect PC(Game) to TV using HDMI / VGA cable. (Optional)
3. Run server.js on PC(Game): node server.js
4. Connect gamepad to RaspberryPi / PC(Gamepad).
5. Run Client.py on RaspberryPi / PC(Gamepad): python client.py SERVER-IP-ADDRESS
6. Run the game.
7. Lean on the sofa and enjoy the game.
