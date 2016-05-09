// vJoyClient.cpp : Simple feeder application with a FFB demo
//


// Monitor Force Feedback (FFB) vJoy device
#include "stdafx.h"
//#include "Devioctl.h"
#include "public.h"
#include <malloc.h>
#include <string.h>
#include <stdlib.h>
#include "vjoyinterface.h"
#include "Math.h"

// Default device ID (Used when ID not specified)
#define DEV_ID		1

JOYSTICK_POSITION_V2 iReport; // The structure that holds the full position data

#define bitN(arg,n) (((arg)>>(n))&1)

int
__cdecl
_tmain(int argc, _TCHAR* argv[])
{
	int stat = 0;
	int data = 0;
	UINT DevID = DEV_ID;

	PVOID pPositionMessage;
	
	BYTE id = 1;

	// Define the effect names
	static FFBEType FfbEffect= (FFBEType)-1;

	// Set the target Joystick - get it from the command-line 
	if (argc>1)
		DevID = _tstoi(argv[1]);

	// Get the driver attributes (Vendor ID, Product ID, Version Number)
	if (!vJoyEnabled())
	{
		_tprintf("Function vJoyEnabled Failed - make sure that vJoy is installed and enabled\n");
		int dummy = getchar();
		stat = - 2;
		goto Exit;
	}
	else
	{
		wprintf(L"Vendor: %s\nProduct :%s\nVersion Number:%s\n", static_cast<TCHAR *> (GetvJoyManufacturerString()), static_cast<TCHAR *>(GetvJoyProductString()), static_cast<TCHAR *>(GetvJoySerialNumberString()));
	};

	// Get the status of the vJoy device before trying to acquire it
	VjdStat status = GetVJDStatus(DevID);

	switch (status)
	{
	case VJD_STAT_OWN:
		_tprintf("vJoy device %d is already owned by this feeder\n", DevID);
		break;
	case VJD_STAT_FREE:
		_tprintf("vJoy device %d is free\n", DevID);
		break;
	case VJD_STAT_BUSY:
		_tprintf("vJoy device %d is already owned by another feeder\nCannot continue\n", DevID);
		return -3;
	case VJD_STAT_MISS:
		_tprintf("vJoy device %d is not installed or disabled\nCannot continue\n", DevID);
		return -4;
	default:
		_tprintf("vJoy device %d general error\nCannot continue\n", DevID);
		return -1;
	};

	// Acquire the vJoy device
	if (!AcquireVJD(DevID))
	{
		_tprintf("Failed to acquire vJoy device number %d.\n", DevID);
		int dummy = getchar();
		stat = -1;
		goto Exit;
	}
	else
		_tprintf("Acquired device number %d - OK\n", DevID);
		


	// Start FFB
	BOOL Ffbstarted = FfbStart(DevID);
	if (!Ffbstarted)
	{
		_tprintf("Failed to start FFB on vJoy device number %d.\n", DevID);
		int dummy = getchar();
		stat = -3;
		goto Exit;
	}
	else
		_tprintf("Started FFB on vJoy device number %d - OK\n", DevID);

	
	while (1)
	{

		// Set destenition vJoy device
		id = (BYTE)DevID;
		iReport.bDevice = id;
		
		// read data from command line
		scanf("%i",&data);
		
		iReport.lButtons = data;
		
		if(bitN(data,12)) iReport.wAxisX = 32768;
		else if(bitN(data,13)) iReport.wAxisX = 0;
		else iReport.wAxisX = 16384;
	
		
		if(bitN(data,18)) iReport.wAxisY = 32768;
		else if(bitN(data,19)) iReport.wAxisY = 0;
		else iReport.wAxisY = 16384;
	
		// Send position data to vJoy device
		pPositionMessage = (PVOID)(&iReport);
		if (!UpdateVJD(DevID, pPositionMessage))
		{
			printf("Feeding vJoy device number %d failed - try to enable device then press enter\n", DevID);
			getchar();
			AcquireVJD(DevID);
		}
		
	}

Exit:
	RelinquishVJD(DevID);
	return 0;
}