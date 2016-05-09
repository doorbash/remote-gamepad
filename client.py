import sys
import pygame
import time
import socket

from socketIO_client import SocketIO, LoggingNamespace

with SocketIO(sys.argv[1], 3000, LoggingNamespace) as socketIO:

    pygame.init()
    j = pygame.joystick.Joystick(0)
    j.init()
    print 'Initialized Joystick : %s' % j.get_name()

    numButtons = j.get_numbuttons()
    numAxes = j.get_numaxes()
    numHats = j.get_numhats()

    try:
        while True:
            pygame.event.pump()

            data = 0

            for i in range(0, numButtons):
                if j.get_button(i) != 0:
                    data += (1 << i)

            for i in range(numButtons,numButtons + numAxes*2)[::2]:
                axis = j.get_axis((i-numButtons)/2)
                if axis > 0:
                    data += 1 << i
                elif axis < 0:
                    data += 1 << i + 1

            for i in range(numButtons + numAxes,numButtons + numAxes + numHats*4)[::4]:
                x,y = j.get_hat((i-numButtons-numAxes)/4)
                if x > 0:
                    data += 1 << i
                elif x < 0:
                    data += 1 << i + 1
                if y > 0:
                    data += 1 << i + 2
                elif y < 0:
                    data += 1 << i + 3

            # if data > 0:
            socketIO.emit('event',str(data))

            

    except KeyboardInterrupt:
        j.quit()
    except Exception, e:
        print e