package org.rhwlab.snight;

import org.rhwlab.image.ParsingLogic.ImageNameLogic;

import java.io.File;
import java.util.Hashtable;

public class NucleiConfig {
    // the zip file generated by StarryNite
    private String zipFileName;
    private String zipFileNameKey = "zipFileName";

    // the naming method used:
    private int namingMethod;
    private String namingMethodKey = "namingMethod";

    private int startingIndex;
    private String startingIndexKey = "startIdx";

    private int endingIndex;
    private String endingIndexKey = "endIdx";

    private double xyRes;
    private String xyResKey = "xyRes";

    private double zRes;
    private String zResKey = "zRes";

    private String exprCorr;
    private String exprCorrKey = "exprCorr";

    // the size of the polar bodies (AceTree attempts to filter these - deprecated probably)
    private int polarSize;
    private String polarSizeKey = "polarSize";

    // the 2 letter axis identifier (likely going to be stored in MeasureCSV because it comes from the AuxInfo usually but it may show up as an XML tag)
    private String axisGiven; // one of "", "adl" or "avr"
    private String axisGivenKey = "axis";

    // the angle of rotation used in conjunction with the axis ID (same as axis, likely going to be in MeasureCSV, but it could be here)
    private double angle;
    private String angleKey = "angle";

    private int planeStart;

    private MeasureCSV measureCSV; // this class stores the AuxInfo data


    private static int NEWCANONICALID = 3;
    private static String zipNucDir = "nuclei/";
    /**
     * Constructor called by Config constructor which has built a hashmap of configuration values
     * from an XML file
     *
     * This constructor parses that map for relevant nuclei tags and saves them in this object
     * @param configData
     */
    public NucleiConfig(Hashtable<String, String> configData, String configFileName) {
        System.out.println("Configuring NucleiConfig using .XML data: " + configFileName);

        // prevent errors by initializing everything
        zipFileName = axisGiven = "";
        namingMethod = NEWCANONICALID; // the default naming method
        polarSize = 45; //default to 45
        angle = -1.;

        // default to a value that won't break the program
        planeStart = 1;
        startingIndex = 1;
        endingIndex = 1;
        exprCorr = "blot";

        if (configData == null) return;

        for (String s : configData.keySet()) {
            if (s.toLowerCase().equals(zipFileNameKey.toLowerCase())) {
                 String zipFile = configData.get(s);

                // check if it's a relative path
                if (zipFile != null && (ImageNameLogic.isPathRelative(zipFile) || ImageNameLogic.isPathImplicitRelative(zipFile))) {
                    // we need to make sure that we're working with the same directory delimiter in the config file and the zip file, because we're going to build
                    // the absolute path to the zip from the config file
                    String directoryDelimiter1 = ImageNameLogic.getDirectoryDelimiter(configFileName);
                    if (directoryDelimiter1.isEmpty()) {
                        System.out.println("Couldn't update relative nuc .zip path to absolute because the config file's separator couldn't be determined.");
                        return;
                    }

                    // check if this is an implicitly relative zip file and if so, prepend it with "./" or ".\"
                    if (ImageNameLogic.isPathImplicitRelative(zipFile)) {
                        zipFile = "." + directoryDelimiter1 + zipFile;
                        System.out.println("Nuc .zip is implicitly relative, updating to: " + zipFile);
                    }

                    String directoryDelimiter = ImageNameLogic.getDirectoryDelimiter(zipFile);
                    if (directoryDelimiter.isEmpty()) {
                        System.out.println("Couldn't update relative nuc .zip path to absolute because the zip's file separator couldn't be determined. Make " +
                                "sure they are consistent.");
                        return;
                    }

                    // are the delimiters the same?
                    if (!directoryDelimiter1.equals(directoryDelimiter)) {
                        // update the zip file's delimiters to that of the config file (because we know the config file's are valid, they're what got us this far)
                        zipFile = zipFile.replace(directoryDelimiter.charAt(0), directoryDelimiter1.charAt(0));
                    }

                    if (ImageNameLogic.isRelativePathDownstream(zipFile)) {
                        // if the nuc zip is relative either in the same directory or downstream, just prepend it with the absolute path in the configFileName
                        zipFile = configFileName.substring(0, configFileName.lastIndexOf(directoryDelimiter1) + 1) + zipFile.substring(zipFile.indexOf(directoryDelimiter1)+1);
                        System.out.println("NucleiConfig update relative nuc zip path to: " + zipFile);
                    } else if (ImageNameLogic.isRelativePathUpstream(zipFile)) {
                        // if the nuc zip is relative upstream, we'll need to walk back along the config file path according to the relative path specified by the zip file
                        int numUpstreamDirectoriesSpecified = ImageNameLogic.getNumberOfUpstreamDirectoriesSpecifiedInRelativePath(zipFile);

                        // make sure that there are more directories in the config file path
                        int numDirectoriesInAbsPath = ImageNameLogic.getNumberOfDirectoriesInAbsolutePath(configFileName);
                        if (numUpstreamDirectoriesSpecified < numDirectoriesInAbsPath) {
                            zipFile = ImageNameLogic.getFirstNDirectoriesInAbsolutePath(configFileName, numDirectoriesInAbsPath - numUpstreamDirectoriesSpecified) + ImageNameLogic.getImagePathAfterUpstreamDirectoryCharacters(zipFile);
                            System.out.println("Updating relative nuc zip file path to absolute: " + zipFile);
                        } else {
                            System.out.println("The number of upstream directories specified in the relative nuc zip file path is greater than the total number of nested directories in the absolute path of the config file");
                        }
                    }

                }
                this.zipFileName = zipFile;
            } else if (s.toLowerCase().equals(namingMethodKey.toLowerCase())) {
                this.namingMethod = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(xyResKey.toLowerCase())) {
                this.xyRes = Double.parseDouble(configData.get(s));
            } else if (s.toLowerCase().equals(zResKey.toLowerCase())) {
              this.zRes = Double.parseDouble(configData.get(s));
            } else if (s.toLowerCase().equals(startingIndexKey.toLowerCase())) {
                this.startingIndex = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(endingIndexKey.toLowerCase())) {
                this.endingIndex = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(exprCorrKey.toLowerCase())) {
                this.exprCorr = configData.get(s);
            } else if (s.toLowerCase().equals(polarSizeKey.toLowerCase())) {
                this.polarSize = Integer.parseInt(configData.get(s));
            } else if (s.toLowerCase().equals(axisGiven.toLowerCase())) {
                this.axisGiven = configData.get(s);
            } else if (s.toLowerCase().equals(angleKey.toLowerCase())) {
                this.angle = Double.parseDouble(configData.get(s));
            }
        }

        // the nuclei are also configured by the parameters in the AuxInfo file, so we'll process that now and keep it
        // bundled with this object
        System.out.println("Processing AuxInfo data for NucleiConfig");

        // the convention for the AuxInfo file is to have it share the base name of the .zip file, so we pass the
        // MeasureCSV constructor the base name of the zip and it will append it with the proper text and extension
        // to find the AuxInfo if the user has set up their directory and files correctly
        int preExtIdx = configFileName.lastIndexOf(".");
        if (preExtIdx > 0) {
            this.measureCSV = new MeasureCSV(configFileName.substring(0, preExtIdx));
            System.out.println("The contents of the AuxInfo file (now stored in the NucleiConfig as a MeasureCSV object: " +
                    measureCSV.toString());
        } else {
            System.out.println("There was a problem using the base of the zip file name: " + zipFileName + " when trying to " +
                    "supply MeasureCSV with a file path with which to find the AuxInfo file.");
        }
    }

    // mutators
    public void setZipFileName(String zipFileName) { this.zipFileName = zipFileName; }
    public void setNamingMethod(String namingMethod) { setNamingMethod(Integer.parseInt(namingMethod)); }
    public void setNamingMethod(int namingMethod) { this.namingMethod = namingMethod; }
    public void setXyRes(String xyRes) { setXyRes(Double.parseDouble(xyRes)); }
    public void setXyRes(double xyRes) { this.xyRes = xyRes; }
    public void setZRes(String zRes) { setZRes(Double.parseDouble(zRes)); }
    public void setZRes(double zRes) { this.zRes = zRes; }
    public void setStartingIndex(String startingIndex) { setStartingIndex(Integer.parseInt(startingIndex)); }
    public void setStartingIndex(int startingIndex) {
        System.out.println("setting starting index in nuc config: " + startingIndex);
        this.startingIndex = startingIndex;
    }
    public void setEndingIndex(String endingIndex) { setEndingIndex(Integer.parseInt(endingIndex)); }
    public void setEndingIndex(int endingIndex) { this.endingIndex = endingIndex; }
    public void setPlaneStart(String planeStart) { setPlaneStart(Integer.parseInt(planeStart)); }
    public void setPlaneStart(int planeStart) { this.planeStart = planeStart; }
    public void setExprCorr(String exprCorr) { this.exprCorr = exprCorr; }
    public void setPolarSize(String polarSize) { setPolarSize(Integer.parseInt(polarSize)); }
    public void setPolarSize(int polarSize) { this.polarSize = polarSize; }
    public void setAxis(String axis) { this.axisGiven = axis; }
    public void setAngle(String angle) { setAngle(Double.parseDouble(angle)); }
    public void setAngle(double angle) { this.angle = angle; }
    public void setAxisGiven(String axisGiven) {
        this.axisGiven = axisGiven;
    }
    public void setMeasureCSV(MeasureCSV measureCSV) { this.measureCSV = measureCSV; }

    // accessors
    public String getZipFileName() { return this.zipFileName; }
    public int getNamingMethod() { return this.namingMethod; }
    public double getXyRes() { return this.xyRes; }
    public double getZRes() { return this.zRes; }
    public int getStartingIndex() { return this.startingIndex; }
    public int getEndingIndex() { return this.endingIndex; }
    public int getPlaneStart() { return this.planeStart; }
    public String getExprCorr() { return this.exprCorr; }
    public int getPolarSize() { return this.polarSize; }
    public String getAxisGiven() { return this.axisGiven; }
    public double getAngle() { return this.angle; }
    public double getZPixRes() { return this.zRes/this.xyRes; } // this value is
    public MeasureCSV getMeasureCSV() { return this.measureCSV; }

    public String getZipNucDir() { return zipNucDir; }

    @Override
    public String toString() {
        String toString = NL
                + "Nuclei Config:" + NL
                + zipFileNameKey + CS + zipFileName + NL
                + namingMethodKey + CS + namingMethod + NL
                + xyResKey + CS + xyRes + NL
                + zResKey + CS + zRes + NL
                + polarSizeKey + CS + polarSize + NL
                + axisGivenKey + CS + axisGiven + NL
                + angleKey + CS + angle + NL;

        return toString;
    }

    private static String CS = ", ";
    private static String NL = "\n";


}
