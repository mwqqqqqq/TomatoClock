package com.example.tomatoclock.Task;

public class Task {
    private int id;
    private String ddl;
    private String infor;

    public Task(int id, String ddl, String infor){
        this.id = id;
        this.ddl = ddl;
        this.infor = infor;
    }

    public int getId(){
        return id;
    }
    public String getDdl(){
        return ddl;
    }
    public void updateDdl(String newDdl){
        ddl = newDdl;
    }
    public String getInfor(){
        return infor;
    }
    public void updateInfor(String newInfor){
        infor = newInfor;
    }
}
