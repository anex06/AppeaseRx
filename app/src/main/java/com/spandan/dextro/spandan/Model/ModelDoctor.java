package com.spandan.dextro.spandan.Model;

public class ModelDoctor {

    public String getDoctorImage() {
        return doctorImage;
    }

    public void setDoctorImage(String doctorImage) {
        this.doctorImage = doctorImage;
    }

    private String doctorName;
    private String doctorImage;
    private double docExperience;
    private String docId;
    private String docAddress;
    private String docFees;
    private double docFeedBack;
    private String docNextVisitDay;
    private String docQualification;

    //Doctor available data
    private String docDates;
    private String docDays;
    private String startTime;
    private String endTime;

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }


    public String getDocAddress() {
        return docAddress;
    }

    public void setDocAddress(String docAddress) {
        this.docAddress = docAddress;
    }

    public double getDocFeedBack() {
        return docFeedBack;
    }

    public void setDocFeedBack(double docFeedBack) {
        this.docFeedBack = docFeedBack;
    }

    public String getDocNextVisitDay() {
        return docNextVisitDay;
    }

    public void setDocNextVisitDay(String docNextVisitDay) {
        this.docNextVisitDay = docNextVisitDay;
    }

    public double getDocExperience() {
        return docExperience;
    }

    public void setDocExperience(double docExperience) {
        this.docExperience = docExperience;
    }

    public String getDocQualification() {
        return docQualification;
    }

    public void setDocQualification(String docQualification) {
        this.docQualification = docQualification;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocFees() {
        return docFees;
    }

    public void setDocFees(String docFees) {
        this.docFees = docFees;
    }

    public String getDocDates() {
        return docDates;
    }

    public void setDocDates(String docDates) {
        this.docDates = docDates;
    }


    public String getDocDays() {
        return docDays;
    }

    public void setDocDays(String docDays) {
        this.docDays = docDays;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
