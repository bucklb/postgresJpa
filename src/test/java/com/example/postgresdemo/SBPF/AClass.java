package com.example.postgresdemo.SBPF;

public class AClass {
    public String id ;
    public String name;
    public String[] color;
    public int sal;
    public BClass[] bclass ;


    public AClass(String id, String name, String[] color, int sal, BClass[] bclass) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sal = sal;
        this.bclass = bclass;
    }

    public AClass() {}

}
