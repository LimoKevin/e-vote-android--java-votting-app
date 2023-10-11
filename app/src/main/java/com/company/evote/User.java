package com.company.evote;

public class User {
    public String name, email,year;
    public String vote, votersVerification;
    public boolean applicant;
    public boolean accverification;
    private String school;
    private String status;
    private String post;
    private int position;
    String age;
    String gender;
    String regNo;
    String phone;
    String userId;
    int count;


    User(){
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public User(String school, String status){
        this.school = school;
        this.status = status;
    }
    public User(String post){
        this.post = post;
    }

    public User(boolean applicant ){
        this.applicant = applicant;
    }
    public User(String name, String email,String year) {
        this.name = name;
        this.email = email;
        this.year = year;
    }

    public User(String name, String email, boolean accverification, String votersVerification) {
        this.name = name;
        this.email = email;
        this.accverification = accverification;
        this.votersVerification = votersVerification;
    }

    public String getVotersVerification() {
        return votersVerification;
    }

    public void setVotersVerification(String votersVerification) {
        this.votersVerification = votersVerification;
    }

    public boolean isAccverification() {
        return accverification;
    }

    public void setAccverification(boolean accverification) {
        this.accverification = accverification;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public boolean isApplicant() {
        return applicant;
    }

    public void setApplicant(boolean applicant) {
        this.applicant = applicant;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
