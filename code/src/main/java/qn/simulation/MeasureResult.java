package qn.simulation;

import org.jdom2.Element;

public class MeasureResult {
    //<measure alfa="0.01" analyzedSamples="51200" class="" discardedSamples="720" lowerLimit="197.87636810752332" maxSamples="1000000" meanValue="200.97642988092804" measureType="System Response Time" nodeType="" precision="0.03" station="" successful="true" upperLimit="204.07649165433276"/>
    private final double alfa;
    private final double analyzedSamples;
    private final String classField;
    private final int discardedSamples;
    private final double lowerLimit;
    private final int maxSamples;
    private final double meanValue;
    private final String measureType;
    private final String nodeType;
    private final double precision;
    private final String station;
    private final boolean successful;
    private final double upperLimit;

    public MeasureResult(double alfa, double analyzedSamples, String classField, int discardedSamples, double lowerLimit, int maxSamples, double meanValue, String measureType, String nodeType, double precision, String station, boolean successful, double upperLimit) {
        this.alfa = alfa;
        this.analyzedSamples = analyzedSamples;
        this.classField = classField;
        this.discardedSamples = discardedSamples;
        this.lowerLimit = lowerLimit;
        this.maxSamples = maxSamples;
        this.meanValue = meanValue;
        this.measureType = measureType;
        this.nodeType = nodeType;
        this.precision = precision;
        this.station = station;
        this.successful = successful;
        this.upperLimit = upperLimit;
    }

    public String getMeasureType() {
        return measureType;
    }

    public double getMeanValue() {
        return meanValue;
    }

    public double getLowerLimit() {
        return lowerLimit;
    }

    public double getUpperLimit() {
        return upperLimit;
    }

    public String getStation() {
        return station;
    }

    public String getClassField() {
        return classField;
    }

    public boolean isSignificantlyDifferent(MeasureResult original) {
        boolean meansDifferent;
        if (original.getMeanValue() == 0) {
            meansDifferent = this.getMeanValue() != 0;
        } else {
            meansDifferent = 100 * Math.abs(original.getMeanValue() - this.getMeanValue()) / original.getMeanValue() > 100;
        }
        return meansDifferent
                && (original.lowerLimit > this.upperLimit
                || original.upperLimit < this.lowerLimit);
    }

    public static MeasureResult buildMeasureResult(Element xmlMeasureResult) {
        return new MeasureResult(Double.parseDouble(xmlMeasureResult.getAttributeValue("alfa")),
                Double.parseDouble(xmlMeasureResult.getAttributeValue("analyzedSamples")),
                xmlMeasureResult.getAttributeValue("class"),
                Integer.parseInt(xmlMeasureResult.getAttributeValue("discardedSamples")),
                Double.parseDouble(xmlMeasureResult.getAttributeValue("lowerLimit")),
                Integer.parseInt(xmlMeasureResult.getAttributeValue("maxSamples")),
                Double.parseDouble(xmlMeasureResult.getAttributeValue("meanValue")),
                xmlMeasureResult.getAttributeValue("measureType"),
                xmlMeasureResult.getAttributeValue("nodeType"),
                Double.parseDouble(xmlMeasureResult.getAttributeValue("precision")),
                xmlMeasureResult.getAttributeValue("station"),
                Boolean.parseBoolean(xmlMeasureResult.getAttributeValue("successful")),
                Double.parseDouble(xmlMeasureResult.getAttributeValue("upperLimit")));
    }

    public static String printFullHeader() {
        return printHeaderFixedPart("") + "\t" + printHeaderVariablePart("");
    }

    public static String printHeaderFixedPart(String prefix) {
        return prefix + "alfa\t" + prefix + "nodeType\t" + prefix + "precision\t" + prefix + "station\t" + prefix + "class\t" + prefix + "measureType";
    }

    public static String printHeaderVariablePart(String prefix) {
        return prefix + "analyzedSamples\t" + prefix + "discardedSamples\t" + prefix + "lowerLimit\t" + prefix + "maxSamples\t" + prefix + "meanValue\t" + prefix + "successful\t" + prefix + "upperLimit";
    }

    @Override
    public String toString() {
        return getFixedPart() + "\t" + getVariablePart();
    }

    public String getFixedPart() {
        return alfa + "\t" + nodeType + "\t" + precision + "\t" + station + "\t" + classField + "\t" + measureType;
    }

    public String getVariablePart() {
        return analyzedSamples + "\t" + discardedSamples + "\t" + lowerLimit + "\t" + maxSamples + "\t" + meanValue + "\t" + successful + "\t" + upperLimit;
    }

    public static String getFixedPartNull() {
        return "-1\t-1\t-1\t-1\t-1\t-1";
    }

    public static String getVariablePartNull() {
        return "-1\t-1\t-1\t-1\t-1\t-1\t-1";
    }
}
//model
//<measure alpha="0.01" name="System Response Time" nodeType="" precision="0.03" referenceNode="" referenceUserClass="" type="System Response Time" verbose="false" />
//<measure alpha="0.01" name="System Response Time" nodeType="" precision="0.03" referenceNode="" referenceUserClass="statusCar" type="System Response Time" verbose="false" />
//<measure alpha="0.01" name="System Response Time" nodeType="" precision="0.03" referenceNode="" referenceUserClass="statusScanCar" type="System Response Time" verbose="false" />
//<measure alpha="0.01" name="CAR.statusReport_statusCar_Utilization" nodeType="station" precision="0.03" referenceNode="CAR.statusReport" referenceUserClass="statusCar" type="Utilization" verbose="false" />
//<measure alpha="0.01" name="CAR.statusReport_statusScanCar_Utilization" nodeType="station" precision="0.03" referenceNode="CAR.statusReport" referenceUserClass="statusScanCar" type="Utilization" verbose="false" />
//<measure alpha="0.01" name="SERVER.scanRequest_statusCar_Utilization" nodeType="station" precision="0.03" referenceNode="SERVER.scanRequest" referenceUserClass="statusCar" type="Utilization" verbose="false" />
//<measure alpha="0.01" name="SERVER.scanRequest_statusScanCar_Utilization" nodeType="station" precision="0.03" referenceNode="SERVER.scanRequest" referenceUserClass="statusScanCar" type="Utilization" verbose="false" />
//<measure alpha="0.01" name="SERVER.scanData_statusCar_Utilization" nodeType="station" precision="0.03" referenceNode="SERVER.scanData" referenceUserClass="statusCar" type="Utilization" verbose="false" />
//<measure alpha="0.01" name="SERVER.scanData_statusScanCar_Utilization" nodeType="station" precision="0.03" referenceNode="SERVER.scanData" referenceUserClass="statusScanCar" type="Utilization" verbose="false" />

//result
//<measure alfa="0.01" analyzedSamples="30720" class="" discardedSamples="1925" lowerLimit="194.4466957793575" maxSamples="1000000" meanValue="199.5188726245449" measureType="System Response Time" nodeType="" precision="0.03" station="" successful="true" upperLimit="204.59104946973233"/>
//<measure alfa="0.01" analyzedSamples="56100" class="statusCar" discardedSamples="330" lowerLimit="190.80722316529113" maxSamples="1000000" meanValue="193.95834358452683" measureType="System Response Time" nodeType="" precision="0.03" station="" successful="true" upperLimit="197.10946400376253"/>
//<measure alfa="0.01" analyzedSamples="35840" class="statusScanCar" discardedSamples="1070" lowerLimit="198.8994660495811" maxSamples="1000000" meanValue="202.3167067483612" measureType="System Response Time" nodeType="" precision="0.03" station="" successful="true" upperLimit="205.73394744714133"/>
//<measure alfa="0.01" analyzedSamples="61440" class="statusCar" discardedSamples="165" lowerLimit="0.04451803377982573" maxSamples="1000000" meanValue="0.04569929552904392" measureType="Utilization" nodeType="station" precision="0.03" station="CAR.statusReport" successful="true" upperLimit="0.04688055727826211"/>
//<measure alfa="0.01" analyzedSamples="81920" class="statusScanCar" discardedSamples="5" lowerLimit="0.08425723728072912" maxSamples="1000000" meanValue="0.08681854408348572" measureType="Utilization" nodeType="station" precision="0.03" station="CAR.statusReport" successful="true" upperLimit="0.08937985088624231"/>
//<measure alfa="0.01" analyzedSamples="10000" class="statusCar" discardedSamples="0" lowerLimit="0.0" maxSamples="1000000" meanValue="0.0" measureType="Utilization" nodeType="station" precision="0.03" station="SERVER.scanRequest" successful="true" upperLimit="0.0"/>
//<measure alfa="0.01" analyzedSamples="81920" class="statusScanCar" discardedSamples="5" lowerLimit="0.04646366218422864" maxSamples="1000000" meanValue="0.047587319822529686" measureType="Utilization" nodeType="station" precision="0.03" station="SERVER.scanRequest" successful="true" upperLimit="0.04871097746083073"/>
//<measure alfa="0.01" analyzedSamples="10000" class="statusCar" discardedSamples="0" lowerLimit="0.0" maxSamples="1000000" meanValue="0.0" measureType="Utilization" nodeType="station" precision="0.03" station="SERVER.scanData" successful="true" upperLimit="0.0"/>
//<measure alfa="0.01" analyzedSamples="7040" class="statusScanCar" discardedSamples="1730" lowerLimit="0.9569918107996762" maxSamples="1000000" meanValue="0.9854604346439874" measureType="Utilization" nodeType="station" precision="0.03" station="SERVER.scanData" successful="true" upperLimit="1.0139290584882985"/>

//map
//alpha -> alpha
//nodeType -> nodeType
//precision -> precision
//referenceNode -> station
//referenceUserClass -> class
//type -> measureType