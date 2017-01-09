package mobile.uom.gr.androidproject;

/**
 * This class holds the model of a single flight to get data for CustomAdapter
 */

public class FlightModel {
    private String total_price = "";
    private String inbound_flight = "";
    private String inbound_flight_time = "";
    private String outbound_flight = "";
    private String outbound_flight_time = "";

    public FlightModel(String out_flight, String out_time, String in_flight, String in_time, String price) {
        total_price = price;
        inbound_flight = in_flight;
        inbound_flight_time = in_time;
        outbound_flight = out_flight;
        outbound_flight_time = out_time;
    }

    /****** Getters ******/

    public String getTotal_price() {
        return total_price;
    }

    public String getInbound_flight() {
        return inbound_flight;
    }

    public String getInbound_flight_time() {
        return inbound_flight_time;
    }

    public String getOutbound_flight() {
        return outbound_flight;
    }

    public String getOutbound_flight_time() {
        return outbound_flight_time;
    }
}
