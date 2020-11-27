/*
 * JSGFPLibTest.java
 *
 * Created on February 4, 2005, 6:52 PM
 */
package SecuGen.FDxSDKPro.samples;

import java.io.*;
import SecuGen.FDxSDKPro.jni.*;

/**
 *
 * @author  Dan Riley
 */
public class JSGMultiDeviceTest {
    
    /** Creates a new instance of JFPLibTest */
    public JSGMultiDeviceTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        long err;
        byte kbBuffer[] = new byte[100];
        byte[] imageBuffer1;
        FileOutputStream fout = null;
        PrintStream fp = null;


	SGDeviceList[] devList = new SGDeviceList[10];
        for (int i=0; i < 10; ++i)
		devList[i] = new SGDeviceList();
	int[] ndevs = new int[1];

        System.out.println("");
        System.out.println("###############################");
        System.out.println("SecuGen FDx SDK Pro for Java");
        System.out.println("Multiple Device Test Program");
        System.out.println("###############################");
        System.out.println("");
        
        
        ///////////////////////////////////////////////
        // Instantiate JSGFPLib object
        System.out.println("Instantiate JSGFPLib Object");
        JSGFPLib sgfplib = new JSGFPLib();
        if ((sgfplib != null) && (sgfplib.jniLoadStatus != SGFDxErrorCode.SGFDX_ERROR_JNI_DLLLOAD_FAILED))
        {
            System.out.println(sgfplib);
        }
        else
        {
            System.out.println("An error occurred while loading JSGFPLIB.DLL JNI Wrapper");
            return;
        }

        ///////////////////////////////////////////////
        // Init()
        System.out.println("Call Init(SGFDxDeviceName.SG_DEV_AUTO)");
        err = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);
        System.out.println("Init returned : [" + err + "]");

        System.out.println("Call EnumerateDevice()");
  	err = sgfplib.EnumerateDevice(ndevs, devList);

  	if (err != SGFDxErrorCode.SGFDX_ERROR_NONE)
  	{
        	System.out.println("EnumerateDevice failed : [" + err + "]");
     		return;
  	}
	else
        	System.out.println("EnumerateDevice returned : [" + err + "]");


  	for (int i=0; i<ndevs[0]; ++i)
        	System.out.println("Device " + devList[i].devID + ": S/N[" + new String(devList[i].deviceSN()) + "] devType:[" +devList[i].devType + "] devName:[" + devList[i].devName + "]" );

	for (int i=0; i<ndevs[0]; ++i)
	{
		///////////////////////////////////////////////
		// Each device must be initialized 
		// Init()
		System.out.println("Call Init(" + devList[i].devName + ")");
        err = sgfplib.Init(devList[i].devName);
		System.out.println("Init returned : [" + err + "]");

		///////////////////////////////////////////////
		// OpenDevice()
        err = sgfplib.Init(devList[i].devName);
		System.out.println("Call OpenDevice(" + i + ")");
		err = sgfplib.OpenDevice(devList[i].devID);
		System.out.println("OpenDevice returned : [" + err + "]");
		if (err != SGFDxErrorCode.SGFDX_ERROR_NONE)
			 System.out.println("OpenDevice: Failed : ErrorCode = " + err);
		else
		{
			///////////////////////////////////////////////
			// GetDeviceInfo()
			System.out.println("Call GetDeviceInfo()");
			SGDeviceInfoParam deviceInfo = new SGDeviceInfoParam();
			err = sgfplib.GetDeviceInfo(deviceInfo);
			if (err == SGFDxErrorCode.SGFDX_ERROR_NONE) {
				System.out.println( "GetDeviceInfo returned : [" + err + "]");
				System.out.println("\tdeviceInfo.DeviceSN:    [" + new String(deviceInfo.deviceSN()) + "]");
				System.out.println("\tdeviceInfo.Brightness:  [" + deviceInfo.brightness + "]");
				System.out.println("\tdeviceInfo.ComPort:     [" + deviceInfo.comPort + "]");
				System.out.println("\tdeviceInfo.ComSpeed:    [" + deviceInfo.comSpeed + "]");
				System.out.println("\tdeviceInfo.Contrast:    [" + deviceInfo.contrast + "]");
				System.out.println("\tdeviceInfo.DeviceID:    [" + deviceInfo.deviceID + "]");
				System.out.println("\tdeviceInfo.FWVersion:   [" + deviceInfo.FWVersion + "]");
				System.out.println("\tdeviceInfo.Gain:        [" + deviceInfo.gain + "]");
				System.out.println("\tdeviceInfo.ImageDPI:    [" + deviceInfo.imageDPI + "]");
				System.out.println("\tdeviceInfo.ImageHeight: [" + deviceInfo.imageHeight + "]");
				System.out.println("\tdeviceInfo.ImageWidth:  [" + deviceInfo.imageWidth + "]");
			}
			else
				 System.out.println("GetDeviceInfo: Failed : ErrorCode = " + err);

			System.out.println("Call SetLedOn(true)");
			err = sgfplib.SetLedOn(true);
			System.out.println("SetLedOn returned : [" + err + "]");

			System.out.print("Place finger on sensor " + i + " - SN[" + new String(deviceInfo.deviceSN()) + "] with LED on and press <ENTER>:");
			try
			{
			    System.in.read(kbBuffer);
			}
			catch (IOException e)
			{
			    System.out.println("Exception reading keyboard : " + e);
			}

			System.out.println("Call SetLedOn(false)");
			err = sgfplib.SetLedOn(false);
			System.out.println("SetLedOn returned : [" + err + "]");

			int[] quality = new int[1];

			///////////////////////////////////////////////
			// getImage()
			imageBuffer1 = new byte[deviceInfo.imageHeight*deviceInfo.imageWidth];
			try
			{
			    System.out.println("Call GetImage()");
			    err = sgfplib.GetImage(imageBuffer1);
			    System.out.println("GetImage returned : [" + err + "]");
			    if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
			    {
				err = sgfplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer1, quality);
				System.out.println("GetImageQuality returned : [" + err + "]");
				System.out.println("Image Quality is : [" + quality[0] + "]");
				//String fileName = "finger_" + new String(deviceInfo.deviceSN()) + ".raw";
				String fileName = "multidev_" + i + ".raw";
				System.out.println("File Name is : [" + fileName + "]");
				fout = new FileOutputStream(fileName);
				fp = new PrintStream(fout);
				fp.write(imageBuffer1,0, imageBuffer1.length);
				fp.close();
				fout.close();
				fp = null;
				fout = null;
			    }
			    else
			    {
				System.out.println("ERROR: Fingerprint image capture failed for [" + new String(deviceInfo.deviceSN()) + "]" );
			    }
			}
			catch (IOException e)
			{
			    System.out.println("Exception reading keyboard : " + e);
			}

			///////////////////////////////////////////////
			// CloseDevice()
			System.out.println("Call CloseDevice()");
			err = sgfplib.CloseDevice();
			System.out.println("CloseDevice returned : [" + err + "]");
		}
	}

/*

        ///////////////////////////////////////////////
        // GetError()
        System.out.println("Call GetLastError()");
        err = sgfplib.GetLastError();
        System.out.println("GetLastError returned : [" + err + "]");

        
        ///////////////////////////////////////////////
        // SetLedOn(true)
        System.out.print("Press <Enter> to turn fingerprint sensor LEDs on >> ");
        try
        {
            System.in.read(kbBuffer);
            System.out.println("Call SetLedOn(true)");
            err = sgfplib.SetLedOn(true);
            System.out.println("SetLedOn returned : [" + err + "]");
        }
        catch (IOException e)
        {
            System.out.println("Exception reading keyboard : " + e);
        }

        ///////////////////////////////////////////////
        // SetLedOn(false)
        System.out.println("Fingerprint Sensor LEDS should now be illuminated.");
        System.out.print("Press <Enter> to turn fingerprint sensor LEDs off >> ");
        try
        {
            System.in.read(kbBuffer);
            System.out.println("Call SetLedOn(false)");
            err = sgfplib.SetLedOn(false);
            System.out.println("SetLedOn returned : [" + err + "]");
        }
        catch (IOException e)
        {
            System.out.println("Exception reading keyboard : " + e);
        }

        ///////////////////////////////////////////////
        ///////////////////////////////////////////////
        System.out.println("Fingerprint Sensor LEDS should now be off.");
        System.out.println("The next tests will require mutiple captures of the same finger.");
        System.out.print("Which finger would you like to test with? (e.g. left thumb) >> ");
        try
        {
            System.in.read(kbWhichFinger);
            //Remove CR/NL (<ENTER>)
            for (int i=0; i < kbWhichFinger.length; ++i)
            {
                if ((kbWhichFinger[i] == 0x0A) || (kbWhichFinger[i] == 0x0D)|| (kbWhichFinger[i] == 0x00))
                {
                    fingerLength = i;
                    break;
                }
            }
            if (fingerLength > 0)
                finger = new String(kbWhichFinger,0,fingerLength);
            else finger = new String("finger");
        }
        catch (IOException e)
        {
            System.out.println("Exception reading keyboard : " + e);
        }


        int[] quality = new int[1];
        int[] maxSize = new int[1];
        int[] size = new int[1];
        SGFingerInfo fingerInfo = new SGFingerInfo();
        fingerInfo.FingerNumber = SGFingerPosition.SG_FINGPOS_LI;
        fingerInfo.ImageQuality = quality[0];
        fingerInfo.ImpressionType = SGImpressionType.SG_IMPTYPE_LP;
        fingerInfo.ViewNumber = 1;



//////////////////////////////////////////////////////////////////////////////
// Finger 1
        ///////////////////////////////////////////////
        // getImage() - 1st Capture
        System.out.println("Call SetLedOn(true)");
        err =sgfplib.SetLedOn(true);
        System.out.println("SetLedOn returned : [" + err + "]");
        System.out.print("Capture 1. Please place [" + finger + "] on sensor with LEDs on and press <ENTER> ");
        imageBuffer1 = new byte[deviceInfo.imageHeight*deviceInfo.imageWidth];
        try
        {
            System.in.read(kbBuffer);
            System.out.println("Call GetImage()");
            err = sgfplib.GetImage(imageBuffer1);
            System.out.println("GetImage returned : [" + err + "]");
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                err = sgfplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer1, quality);
                System.out.println("GetImageQuality returned : [" + err + "]");
                System.out.println("Image Quality is : [" + quality[0] + "]");
                fout = new FileOutputStream(finger + "1.raw");
                fp = new PrintStream(fout);
                fp.write(imageBuffer1,0, imageBuffer1.length);
                fp.close();
                fout.close();
                fp = null;
                fout = null;SGFDxErrorCode.SGFDX_ERROR_NONE
            }
            else
            {
                System.out.println("ERROR: Fingerprint image capture failed for sample1.");
                return; //Cannot continue test if image not captured
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception reading keyboard : " + e);
        }

        ///////////////////////////////////////////////
        // Set Template format SG400
        System.out.println("Call SetTemplateFormat(SG400)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
        System.out.println("SetTemplateFormat returned : [" + err + "]");

        ///////////////////////////////////////////////
        // Get Max Template Size for SG400
        System.out.println("Call GetMaxTemplateSize()");
        err = sgfplib.GetMaxTemplateSize(maxSize);
        System.out.println("GetMaxTemplateSize returned : [" + err + "]");
        System.out.println("Max SG400 Template Size is : [" + maxSize[0] + "]");

        ///////////////////////////////////////////////
        // Greate SG400 Template for Finger 1
        SG400minutiaeBuffer1 = new byte[maxSize[0]];
        System.out.println("Call CreateTemplate()");
        err = sgfplib.CreateTemplate(fingerInfo, imageBuffer1, SG400minutiaeBuffer1);
        System.out.println("CreateTemplate returned : [" + err + "]");
        err = sgfplib.GetTemplateSize(SG400minutiaeBuffer1, size);
        System.out.println("GetTemplateSize returned : [" + err + "]");
        System.out.println("SG400 Template Size is : [" + size[0] + "]");
        try
        {
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                fout = new FileOutputStream(finger +"1.sg400");
                fp = new PrintStream(fout);
                fp.write(SG400minutiaeBuffer1,0, size[0]);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception writing minutiae file : " + e);
        }

        ///////////////////////////////////////////////
        // Set Template format ANSI378
        System.out.println("Call SetTemplateFormat(ANSI378)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378);
        System.out.println("SetTemplateFormat returned : [" + err + "]");

        ///////////////////////////////////////////////
        // Get Max Template Size for ANSI378
        System.out.println("Call GetMaxTemplateSize()");
        err = sgfplib.GetMaxTemplateSize(maxSize);
        System.out.println("GetMaxTemplateSize returned : [" + err + "]");
        System.out.println("Max ANSI378 Template Size is : [" + maxSize[0] + "]");

        ///////////////////////////////////////////////
        // Greate ANSI378 Template for Finger1
        ANSIminutiaeBuffer1 = new byte[maxSize[0]];
        System.out.println("Call CreateTemplate()");
        err = sgfplib.CreateTemplate(fingerInfo, imageBuffer1, ANSIminutiaeBuffer1);
        System.out.println("CreateTemplate returned : [" + err + "]");
        err = sgfplib.GetTemplateSize(ANSIminutiaeBuffer1, size);
        System.out.println("GetTemplateSize returned : [" + err + "]");
        System.out.println("ANSI378 Template Size is : [" + size[0] + "]");
        try
        {
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                fout = new FileOutputStream(finger +"1.ansi378");
                fp = new PrintStream(fout);
                fp.write(ANSIminutiaeBuffer1,0, size[0]);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception writing minutiae file : " + e);
        }

        ///////////////////////////////////////////////
        // Set Template format ISO19794
        System.out.println("Call SetTemplateFormat(ISO19794)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
        System.out.println("SetTemplateFormat returned : [" + err + "]");

        ///////////////////////////////////////////////
        // Get Max Template Size for ISO19794
        System.out.println("Call GetMaxTemplateSize()");
        err = sgfplib.GetMaxTemplateSize(maxSize);
        System.out.println("GetMaxTemplateSize returned : [" + err + "]");
        System.out.println("Max ISO19794 Template Size is : [" + maxSize[0] + "]");

        ///////////////////////////////////////////////
        // Greate ISO19794 Template for Finger1
        ISOminutiaeBuffer1 = new byte[maxSize[0]];
        System.out.println("Call CreateTemplate()");
        err = sgfplib.CreateTemplate(fingerInfo, imageBuffer1, ISOminutiaeBuffer1);
        System.out.println("CreateTemplate returned : [" + err + "]");
        err = sgfplib.GetTemplateSize(ISOminutiaeBuffer1, size);
        System.out.println("GetTemplateSize returned : [" + err + "]");
        System.out.println("ISO19794 Template Size is : [" + size[0] + "]");
        try
        {
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                fout = new FileOutputStream(finger +"1.iso19794");
                fp = new PrintStream(fout);
                fp.write(ISOminutiaeBuffer1,0, size[0]);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception writing minutiae file : " + e);
        }



//////////////////////////////////////////////////////////////////////////////
// Finger 2
        ///////////////////////////////////////////////
        // getImage() - 2nd Capture
        System.out.println("Call SetLedOn(true)");
        err =sgfplib.SetLedOn(true);
        System.out.println("SetLedOn returned : [" + err + "]");
        System.out.print("Capture 2. Please place [" + finger + "] on sensor with LEDs on and press <ENTER> ");
        imageBuffer2 = new byte[deviceInfo.imageHeight*deviceInfo.imageWidth];
        try
        {
            System.in.read(kbBuffer);
            System.out.println("Call GetImage()");
            err = sgfplib.GetImage(imageBuffer2);
            System.out.println("GetImage returned : [" + err + "]");
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                err = sgfplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer2, quality);
                System.out.println("GetImageQuality returned : [" + err + "]");
                System.out.println("Image Quality is : [" + quality[0] + "]");
                fout = new FileOutputStream(finger + "2.raw");
                fp = new PrintStream(fout);
                fp.write(imageBuffer2,0, imageBuffer2.length);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
            else
            {
                System.out.println("ERROR: Fingerprint image capture failed for sample2.");
                return; //Cannot continue test if image not captured
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception reading keyboard : " + e);
        }

        ///////////////////////////////////////////////
        // Set Template format SG400
        System.out.println("Call SetTemplateFormat(SG400)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
        System.out.println("SetTemplateFormat returned : [" + err + "]");

        ///////////////////////////////////////////////
        // Get Max Template Size for SG400
        System.out.println("Call GetMaxTemplateSize()");
        err = sgfplib.GetMaxTemplateSize(maxSize);
        System.out.println("GetMaxTemplateSize returned : [" + err + "]");
        System.out.println("Max SG400 Template Size is : [" + maxSize[0] + "]");

        ///////////////////////////////////////////////
        // Greate SG400 Template for Finger 2
        SG400minutiaeBuffer2 = new byte[maxSize[0]];
        System.out.println("Call CreateTemplate()");
        err = sgfplib.CreateTemplate(fingerInfo, imageBuffer2, SG400minutiaeBuffer2);
        System.out.println("CreateTemplate returned : [" + err + "]");
        err = sgfplib.GetTemplateSize(SG400minutiaeBuffer2, size);
        System.out.println("GetTemplateSize returned : [" + err + "]");
        System.out.println("SG400 Template Size is : [" + size[0] + "]");
        try
        {
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                fout = new FileOutputStream(finger +"1.sg400");
                fp = new PrintStream(fout);
                fp.write(SG400minutiaeBuffer2,0, size[0]);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception writing minutiae file : " + e);
        }

        ///////////////////////////////////////////////
        // Set Template format ANSI378
        System.out.println("Call SetTemplateFormat(ANSI378)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378);
        System.out.println("SetTemplateFormat returned : [" + err + "]");

        ///////////////////////////////////////////////
        // Get Max Template Size for ANSI378
        System.out.println("Call GetMaxTemplateSize()");
        err = sgfplib.GetMaxTemplateSize(maxSize);
        System.out.println("GetMaxTemplateSize returned : [" + err + "]");
        System.out.println("Max ANSI378 Template Size is : [" + maxSize[0] + "]");

        ///////////////////////////////////////////////
        // Greate ANSI378 Template for Finger 2
        ANSIminutiaeBuffer2 = new byte[maxSize[0]];
        System.out.println("Call CreateTemplate()");
        err = sgfplib.CreateTemplate(fingerInfo, imageBuffer2, ANSIminutiaeBuffer2);
        System.out.println("CreateTemplate returned : [" + err + "]");
        err = sgfplib.GetTemplateSize(ANSIminutiaeBuffer2, size);
        System.out.println("GetTemplateSize returned : [" + err + "]");
        System.out.println("ANSI378 Template Size is : [" + size[0] + "]");
        try
        {
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                fout = new FileOutputStream(finger +"2.ansi378");
                fp = new PrintStream(fout);
                fp.write(ANSIminutiaeBuffer2,0, size[0]);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception writing minutiae file : " + e);
        }

        ///////////////////////////////////////////////
        // Set Template format ISO19794
        System.out.println("Call SetTemplateFormat(ISO19794)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
        System.out.println("SetTemplateFormat returned : [" + err + "]");

        ///////////////////////////////////////////////
        // Get Max Template Size for ISO19794
        System.out.println("Call GetMaxTemplateSize()");
        err = sgfplib.GetMaxTemplateSize(maxSize);
        System.out.println("GetMaxTemplateSize returned : [" + err + "]");
        System.out.println("Max ISO19794 Template Size is : [" + maxSize[0] + "]");

        ///////////////////////////////////////////////
        // Greate ISO19794 Template for Finger 2
        ISOminutiaeBuffer2 = new byte[maxSize[0]];
        System.out.println("Call CreateTemplate()");
        err = sgfplib.CreateTemplate(fingerInfo, imageBuffer2, ISOminutiaeBuffer2);
        System.out.println("CreateTemplate returned : [" + err + "]");
        err = sgfplib.GetTemplateSize(ISOminutiaeBuffer2, size);
        System.out.println("GetTemplateSize returned : [" + err + "]");
        System.out.println("ISO19794 Template Size is : [" + size[0] + "]");
        try
        {
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                fout = new FileOutputStream(finger +"2.iso19794");
                fp = new PrintStream(fout);
                fp.write(ISOminutiaeBuffer2,0, size[0]);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception writing minutiae file : " + e);
        }


        boolean[] matched = new boolean[1];
        int[] score = new int[1];
        ///////////////////////////////////
        //Match SG400 Templates
        matched[0] = false;
        score[0] = 0;
        System.out.println("Call SetTemplateFormat(SG400)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
        System.out.println("SetTemplateFormat returned : [" + err + "]");
        System.out.println("Call MatchTemplates()");
        err = sgfplib.MatchTemplate(SG400minutiaeBuffer1, SG400minutiaeBuffer2, SGFDxSecurityLevel.SL_NORMAL, matched);
        System.out.println("MatchTemplates returned : [" + err + "]");
        System.out.println("SG400-1 <> SG400-2 Match Result : [" + matched[0] + "]");
        System.out.println("Call GetMatchingScore()");
        err = sgfplib.GetMatchingScore(SG400minutiaeBuffer1, SG400minutiaeBuffer2, score);
        System.out.println("GetMatchingScore returned : [" + err + "]");
        System.out.println("SG400-1  <> SG400-2 Match Score : [" + score[0] + "]");


        ///////////////////////////////////
        //Match ANSI378 Templates
        matched[0] = false;
        score[0] = 0;
        System.out.println("Call SetTemplateFormat(ANSI378)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378);
        System.out.println("SetTemplateFormat returned : [" + err + "]");
        System.out.println("Call MatchAnsiTemplates()");
        err = sgfplib.MatchAnsiTemplate(ANSIminutiaeBuffer1, 0, ANSIminutiaeBuffer2, 0, SGFDxSecurityLevel.SL_NORMAL, matched);
        System.out.println("MatchANSITemplates returned : [" + err + "]");
        System.out.println("ANSI-1 <> ANSI-2 Match Result : [" + matched[0] + "]");
        System.out.println("Call GetAnsiMatchingScore()");
        err = sgfplib.GetAnsiMatchingScore(ANSIminutiaeBuffer1, 0, ANSIminutiaeBuffer2, 0, score);
        System.out.println("GetAnsiMatchingScore returned : [" + err + "]");
        System.out.println("ANSI-1  <> ANSI-2 Match Score : [" + score[0] + "]");

        ///////////////////////////////////
        //Match ISO19794 Templates
        matched[0] = false;
        score[0] = 0;
        System.out.println("Call SetTemplateFormat(ISO19794)");
        err = sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794);
        System.out.println("SetTemplateFormat returned : [" + err + "]");
        System.out.println("Call MatchIsoTemplates()");
        err = sgfplib.MatchIsoTemplate(ISOminutiaeBuffer1, 0, ISOminutiaeBuffer2, 0, SGFDxSecurityLevel.SL_NORMAL, matched);
        System.out.println("MatchISOTemplates returned : [" + err + "]");
        System.out.println("ISO-1 <> ISO-2 Match Result : [" + matched[0] + "]");
        System.out.println("Call GetIsoMatchingScore()");
        err = sgfplib.GetIsoMatchingScore(ISOminutiaeBuffer1, 0, ISOminutiaeBuffer2, 0, score);
        System.out.println("GetIsoMatchingScore returned : [" + err + "]");
        System.out.println("ISO-1  <> ISO-2 Match Score : [" + score[0] + "]");


        ///////////////////////////////////////////////
        // getImageEx()
        System.out.println("--------");
        System.out.println("New Functions--------");
        System.out.println("--------");
        System.out.println("Call GetImageEx()");
        System.out.print("Please place [" + finger + "] on sensor and press <ENTER> ");
        try
        {
            System.in.read(kbBuffer);
            int image_quality = 50; //0 - 100. 50 or above recommended for registration. 40 or above for verification
            int timeout = 10000; //10 seconds
            err = sgfplib.GetImageEx(imageBuffer2,timeout,0,image_quality);
            System.out.println("GetImageEx returned : [" + err + "]");
            if (err == SGFDxErrorCode.SGFDX_ERROR_NONE)
            {
                err = sgfplib.GetImageQuality(deviceInfo.imageWidth, deviceInfo.imageHeight, imageBuffer2, quality);
                System.out.println("GetImageQuality returned : [" + err + "]");
                System.out.println("Image Quality is : [" + quality[0] + "]");
                fout = new FileOutputStream(finger + "_ex.raw");
                fp = new PrintStream(fout);
                fp.write(imageBuffer2,0, imageBuffer2.length);
                fp.close();
                fout.close();
                fp = null;
                fout = null;
            }
            else
            {
                if (err == SGFDxErrorCode.SGFDX_ERROR_NOT_USED)
                    System.out.println("WARNING: GetImageEx() is not supported on this platform.");
                else
                	System.out.println("ERROR: Fingerprint image capture failed for sample2.");
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception reading keyboard : " + e);
        }

               
*/

        ///////////////////////////////////////////////
        // Close JSGFPLib native library
        System.out.println("Call Close()");
        sgfplib.Close();
        System.out.println("Close returned : [" + err + "]");

        sgfplib = null;
        imageBuffer1 = null;

    }    
}
