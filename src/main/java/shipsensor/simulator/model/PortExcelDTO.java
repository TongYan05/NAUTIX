package shipsensor.simulator.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class PortExcelDTO {


    @ExcelProperty("wpiId")
    private String wpiId;


    @ExcelProperty("portName")
    private String portName;


    @ExcelProperty("country")
    private String country;


    @ExcelProperty("countryCode")
    private String countryCode;


    @ExcelProperty("portCode")
    private String portCode;


    @ExcelProperty("portType")
    private String portType;


    @ExcelProperty("latitude")
    private Double latitude;


    @ExcelProperty("longitude")
    private Double longitude;


    @ExcelProperty("maxShipLength")
    private Double maxShipLength;


    @ExcelProperty("maxDraft")
    private Double maxDraft;

}