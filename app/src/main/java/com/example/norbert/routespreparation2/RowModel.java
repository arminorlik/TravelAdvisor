package com.example.norbert.routespreparation2;



class RowModel {
    public RowModel(String StartPos, String StopPos, String CzasPod) {
        this.StartPos = StartPos;
        this.StopPos = StopPos;
        this.CzasPod = CzasPod;
    }

    String StartPos;
    String StopPos;
    String CzasPod;


    public String getStartPos() {
        return StartPos;
    }

    public void setStartPos(String startPos) {
        this.StartPos = startPos;
    }

    public String getStopPos() {
        return StopPos;
    }

    public void setStopPos(String stopPos) {
        this.StopPos = stopPos;
    }

    public String getCzasPod() {
        return CzasPod;
    }

    public void setCzasPod(String czasPod) {
        this.CzasPod = czasPod;
    }

}
