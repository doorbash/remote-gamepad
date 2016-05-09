import sys
import pygame
import time
import socket

from socketIO_client import SocketIO, LoggingNamespace

pygame.init()

with SocketIO(sys.argv[1], 3000, LoggingNamespace) as socketIO:
    joysticks = [pygame.joystick.Joystick(x) for x in range(pygame.joystick.get_count())]
    data = [-1 for x in range(pygame.joystick.get_count())]
    old = [0 for x in range(pygame.joystick.get_count())]
    for i in range(pygame.joystick.get_count()):
        joysticks[i].init()
        print 'Initialized Joystick : %s' % joysticks[i].get_name()

    try:
        while True:
            pygame.event.pump()

            for k in range(pygame.joystick.get_count()):
                j = joysticks[k]
                
                numButtons = j.get_numbuttons()
                numAxes = j.get_numaxes()
                numHats = j.get_numhats()

                old[k] = data[k]
                data[k] = 0

                for i in range(0, numButtons):
                    if j.get_button(i) != 0:
                        data[k] += (1 << i)

                for i in range(numButtons,numButtons + numAxes*2)[::2]:
                    axis = j.get_axis((i-numButtons)/2)
                    if axis > 0:
                        data[k] += 1 << i
                    elif axis < 0:
                        data[k] += 1 << i + 1

                for i in range(numButtons + numAxes,numButtons + numAxes + numHats*4)[::4]:
                    x,y = j.get_hat((i-numButtons-numAxes)/4)
                    if x > 0:
                        data[k] += 1 << i
                    elif x < 0:
                        data[k] += 1 << i + 1
                    if y > 0:
                        data[k] += 1 << i + 2
                    elif y < 0:
                        data[k] += 1 << i + 3

                if old[k] != data[k]:   
                    socketIO.emit('e',"%i %i" % (k,data[k]))

    except KeyboardInterrupt:
        j.quit()
    except Exception, e:
        print e