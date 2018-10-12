package org.rhwlab.snight;

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

    private int planeEnd;
    private String planeEndKey = "planeEnd";

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

    /**
     * Constructor called by Config constructor which has built a hashmap of configuration values
     * from an XML file
     *
     * This constructor parses that map for relevant nuclei tags and saves them in this object
     * @param configData
     */
    public NucleiConfig(Hashtable<String, String> configData) {
        System.out.println("Configuring NucleiConfig using .XML data");

        // prevent errors by initializing everything
        zipFileName = axisGiven = "";
        namingMethod = polarSize = -1;
        angle = -1.;

        // default to a value that won't break the program
        planeStart = 1;
        startingIndex = 1;
        endingIndex = 1;

        if (configData == null) return;

        for (String s : configData.keySet()) {
            if (s.toLowerCase().equals(zipFileNameKey.toLowerCase())) {
                this.zipFileName = configData.get(s);
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
            } else if (s.toLowerCase().equals(planeEndKey.toLowerCase())) {
                this.planeEnd = Integer.parseInt(configData.get(s));
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
        int preExtIdx = zipFileName.lastIndexOf(".");
        if (preExtIdx > 0) {
            this.measureCSV = new MeasureCSV(zipFileName.substring(0, preExtIdx));
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
    public void setStartingIndex(int startingIndex) { this.startingIndex = startingIndex; }
    public void setEndingIndex(String endingIndex) { setEndingIndex(Integer.parseInt(endingIndex)); }
    public void setEndingIndex(int endingIndex) { this.endingIndex = endingIndex; }
    public void setPlaneStart(String planeStart) { setPlaneStart(Integer.parseInt(planeStart)); }
    public void setPlaneStart(int planeStart) { this.planeStart = planeStart; }
    public void setPlaneEnd(String planeEnd) { setPlaneEnd(Integer.parseInt(planeEnd)); }
    public void setPlaneEnd(int planeEnd) { this.planeEnd = planeEnd; }
    public void setExprCorr(String exprCorr) { this.exprCorr = exprCorr; }
    public void setPolarSize(String polarSize) { setPolarSize(Integer.parseInt(polarSize)); }
    public void setPolarSize(int polarSize) { this.polarSize = polarSize; }
    public void setAxis(String axis) { this.axisGiven = axis; }
    public void setAngle(String angle) { setAngle(Double.parseDouble(angle)); }
    public void setAngle(double angle) { this.angle = angle; }
    public void setAxisGiven(String axisGiven) {
        this.axisGiven = axisGiven;
    }

    // accessors
    public String getZipFileName() { return this.zipFileName; }
    public int getNamingMethod() { return this.namingMethod; }
    public double getXyRes() { return this.xyRes; }
    public double getZRes() { return this.zRes; }
    public int getStartingIndex() { return this.startingIndex; }
    public int getEndingIndex() { return this.endingIndex; }
    public int getPlaneStart() { return this.planeStart; }
    public int getPlaneEnd() { return this.planeEnd; }
    public String getExprCorr() { return this.exprCorr; }
    public int getPolarSize() { return this.polarSize; }
    public String getAxisGiven() { return this.axisGiven; }
    public double getAngle() { return this.angle; }
    public double getZPixRes() { return this.zRes/this.xyRes; } // this value is
    public MeasureCSV getMeasureCSV() { return this.measureCSV; }

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
