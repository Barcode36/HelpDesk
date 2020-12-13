package com.android.helpdesk;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Ticket {
    int idTicket;
    String id, title, startDate, endDate, description, place, userId,fullName,technicianId,technicianName,modifiedBy;
    ArrayList<JSONObject> status;
    ArrayList<String> images;
    ArrayList<String> comment;

    public Ticket(int idTicket, String id, String title, String startDate, String endDate, String description, String place, String userId, String fullName, String technicianId, String technicianName, String modifiedBy, ArrayList<JSONObject> status, ArrayList<String> images, ArrayList<String> comment) {
        this.idTicket = idTicket;
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.place = place;
        this.userId = userId;
        this.fullName = fullName;
        this.technicianId = technicianId;
        this.technicianName = technicianName;
        this.modifiedBy = modifiedBy;
        this.status = status;
        this.images = images;
        this.comment = comment;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ArrayList<JSONObject> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<JSONObject> status) {
        this.status = status;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getComment() {
        return comment;
    }

    public void setComment(ArrayList<String> comment) {
        this.comment = comment;
    }
}
