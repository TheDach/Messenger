package com.thedach.messenger_20;

public class User {

    private String id; // индентификатор из FireBase
    private String name;
    private String lastName;
    private int age;
    private boolean online;

    public User(int age, String id, boolean isOnline, String lastName, String name) {
        this.age = age;
        this.id = id;
        this.online = isOnline;
        this.lastName = lastName;
        this.name = name;
    }

    public User() {}

    public int getAge() {
        return age;
    }
    public String getId() {
        return id;
    }
    public boolean isOnline() {
        return online;
    }
    public String getLastName() {
        return lastName;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", isOnline=" + online +
                '}';
    }
}
