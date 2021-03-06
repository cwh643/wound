package org.opencv.android;

import org.opencv.core.Core;

import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StaticHelper {

    private static Logger logger = LoggerFactory.getLogger(StaticHelper.class);

    public static boolean initOpenCV(boolean InitCuda)
    {
        boolean result;
        String libs = "";

        if(InitCuda)
        {
            loadLibrary("cudart");
            loadLibrary("nppc");
            loadLibrary("nppi");
            loadLibrary("npps");
            loadLibrary("cufft");
            loadLibrary("cublas");
        }

        logger.debug("Trying to get library list");

        try
        {
            System.loadLibrary("opencv_info");
            libs = getLibraryList();
        }
        catch(UnsatisfiedLinkError e)
        {
            logger.error("OpenCV error: Cannot load info library for OpenCV");
        }

        logger.debug("Library list: \"" + libs + "\"");
        logger.debug("First attempt to load libs");
        if (initOpenCVLibs(libs))
        {
            logger.debug("First attempt to load libs is OK");
            String eol = System.getProperty("line.separator");
            for (String str : Core.getBuildInformation().split(eol))
                logger.info(str);

            result = true;
        }
        else
        {
            logger.debug("First attempt to load libs fails");
            result = false;
        }

        return result;
    }

    private static boolean loadLibrary(String Name)
    {
        boolean result = true;

        logger.debug("Trying to load library " + Name);
        try
        {
            System.loadLibrary(Name);
            logger.debug("Library " + Name + " loaded");
        }
        catch(UnsatisfiedLinkError e)
        {
            logger.debug("Cannot load library \"" + Name + "\"");
            e.printStackTrace();
            result &= false;
        }

        return result;
    }

    private static boolean initOpenCVLibs(String Libs)
    {
        logger.debug("Trying to init OpenCV libs");

        boolean result = true;

        if ((null != Libs) && (Libs.length() != 0))
        {
            logger.debug("Trying to load libs by dependency list");
            StringTokenizer splitter = new StringTokenizer(Libs, ";");
            while(splitter.hasMoreTokens())
            {
                result &= loadLibrary(splitter.nextToken());
            }
        }
        else
        {
            // If dependencies list is not defined or empty.
            result &= loadLibrary("opencv_java300");
        }

        return result;
    }

    private static final String TAG = "OpenCV/StaticHelper";

    private static native String getLibraryList();
}
