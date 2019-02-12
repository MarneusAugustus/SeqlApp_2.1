package modal;

/**
 * Created by Angel on 20.11.2017.
 */

public class Scans {
    private int id;
    private String scan;
    private int boxid;
    private long today;
    private String city;
    private int cityid;
    private int boxlistid;
    private int todayboxlistid;
    private int routeorder;
    private String name;
    private String street;
    private String insti;
    private String genau;
    private String exptime;
    private String date;
    private String date2;
    private String loc;
    private int timing;
    private int status;
    private float latitude;
    private float longitude;
    private int tourID;



    /**
     * public Scans(String scan, String date, String date2, int status) {
     * this.scan = scan;
     * this.date = date;
     * this.date2 = date2;
     * this.status = status;
     * }
     */

    public Scans(int boxid, long today, String city, int cityid, int boxlistid, int todayboxlistid, int routeorder, String name, String street, String insti, String genau, String exptime, String date, String date2, int status, int timing, float latitude,float longitude, int tourID, String scan) {
        this.boxid = boxid;
        this.today = today;
        this.city = city;
        this.cityid = cityid;
        this.boxlistid = boxlistid;
        this.todayboxlistid = todayboxlistid;
        this.routeorder = routeorder;
        this.name = name;
        this.street = street;
        this.insti = insti;
        this.genau = genau;
        this.exptime = exptime;
        this.date = date;
        this.date2 = date2;
        this.status = status;
        this.timing = timing;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tourID = tourID;
        this.scan = scan;
    }



    public int getId() {
        return id;
    }


    public void setId(int id) {

        this.id = id;
    }

    public int getBoxID() {
        return boxid;
    }

    public void setBoxID(int boxid) {

        this.boxid = boxid;
    }

    public long getToday() {
        return today;
    }

    public void setToday(long today) {
        this.today = today;

    }


    public String getCity() {
        return city;

    }


    public void setCity(String city) {

        this.city = city;
    }

    public int getCityid() {
        return cityid;
    }

    public void setCityid(int cityid) {

        this.cityid = cityid;
    }

    public int getBoxlistid() {

        return boxlistid;
    }


    public void setBoxlistid(int boxlistid) {
        this.boxlistid = boxlistid;
    }

    public int getTodayboxlistid() {
        return todayboxlistid;
    }

    public void setTodayboxlistid(int todayboxlistid) {
        this.todayboxlistid = todayboxlistid;
    }

    public int getRouteorder() {
        return routeorder;
    }

    public void setRouteorder(int routeorder) {
        this.routeorder = routeorder;
    }

    public String getScan() {
        return scan;
    }

    public void setScan(String scan) {
        this.scan = scan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getInsti() {
        return insti;
    }

    public void setInsti(String insti) {

        this.insti = insti;
    }

    public String getGenau() {

        return genau;
    }

    public void setGenau(String genau) {
        this.genau = genau;
    }

    public String getExptime() {
        return exptime;
    }

    public void setExptime(String exptime) {
        this.exptime = exptime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void getLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus() {
        this.status = status;
    }

    public int getTiming() {
        return timing;
    }

    public void setTiming() {
        this.timing = timing;
    }
}