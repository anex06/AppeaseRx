package com.spandan.dextro.spandan.Model;

public class Model_DoctorNameID {

    private String docName;
    private String docId;

    public Model_DoctorNameID(String docName, String docId) {
        this.docName = docName;
        this.docId = docId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public String toString(){
            return docName;
    }
}
