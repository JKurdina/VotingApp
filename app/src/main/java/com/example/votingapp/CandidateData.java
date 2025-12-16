package com.example.votingapp;

public class CandidateData {
    private int id;
    private String firstname;
    private String secondname;
    private String thirdname;
    private String party;
    private String description;
    private String web;
    private String image;
    private int votes;

    public CandidateData(int id, String firstname, String secondname, String thirdname, String party, String description, String web, String image, int votes) {
        this.id = id;
        this.firstname = firstname;
        this.secondname = secondname;
        this.thirdname = thirdname;
        this.party = party;
        this.description = description;
        this.web = web;
        this.image = image;
        this.votes = votes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getThirdName() {
        return thirdname;
    }
    public void setThirdName(String thirdname) {
        this.thirdname = thirdname;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
