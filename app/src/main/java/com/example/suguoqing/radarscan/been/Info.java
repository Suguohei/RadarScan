package com.example.suguoqing.radarscan.been;

public class Info {
    private int id;//头像id
    private String name;//姓名
    private String age;//年龄
    private Gender gender;//性别
    private double distance;//距离

    public Info() {
    }

    public Info(int id, String name, String age, Gender gender, double distance) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Info{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", gender=" + gender +
                ", distance=" + distance +
                '}';
    }
}
