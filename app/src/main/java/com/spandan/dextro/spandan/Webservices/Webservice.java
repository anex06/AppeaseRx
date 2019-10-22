package com.spandan.dextro.spandan.Webservices;

public class Webservice {

    public static String BASE_URL                       = "https://rx.appeaseonlinestore.com/appeaseRX/patient/";
    public static String DOCTOR_VISITING_DETAILS        = BASE_URL+"fetchBookingDetails.php";
    public static String LOGIN                          = BASE_URL+"patient_login.php";
    public static String REGISTRATION                   = BASE_URL+"patient_reg.php";
    public static String GET_ALL_DEPARTMENTS            = BASE_URL+"getAllDepartments.php";
    public static String GET_DOCTOR_AVAILABILITY_DATA   = BASE_URL+"get_availability.php";//Doctor available dates
    public static String GET_DOCTORS_BY_DEPT            = BASE_URL+"getDoctorsBasedOnDeptCenter.php";//Samrat
    public static String GET_CENTER_BY_DEPT_ID          = BASE_URL+"getCenterBasedOnDept.php";//Samrat
    public static String GET_HEALTH_TIPS                = BASE_URL+ "getTips.php";//Samrat
    public static String GET_REPORTS                    = BASE_URL+"getReport.php";//Samrat
    public static String VIEW_REPORT_PDF                = "https://appeaseonlinestore.com/appeaserx/appeaseRX/upload_reports/";//+Arpan_Ghosh_LIVER_FUNCTION_TEST.pdf
    public static String BOOK_APPOINTMENT               = BASE_URL+"book_appointment.php";
    public static String BOOKING_HISTORY                = BASE_URL+"getBookingHistory.php";

}
