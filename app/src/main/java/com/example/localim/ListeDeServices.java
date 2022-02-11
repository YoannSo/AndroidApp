package com.example.localim;


import android.content.Context;

import java.util.ArrayList;

public class ListeDeServices {
    private ArrayList<Services> serviceList;

    public ArrayList<Services> getServiceList() {
        return serviceList;
    }

    public void setServiceList(ArrayList<Services> serviceList) {
        this.serviceList = serviceList;
    }

    public ListeDeServices(){

        this.serviceList=new ArrayList<Services>();
    }
    public void addService(Services service){
        this.serviceList.add(service);
    }
    public void remove(ArrayList<Services> list){
        this.serviceList.retainAll(list);
    }

}
