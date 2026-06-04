package com.singsation.dto;

public class WinnerAnnouncementRequest {
    private String category;
    private String winnerName;
    private String winnerUserid;
    private Integer winnerAge;
    private String province;
    private String message;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getWinnerName() { return winnerName; }
    public void setWinnerName(String winnerName) { this.winnerName = winnerName; }
    public String getWinnerUserid() { return winnerUserid; }
    public void setWinnerUserid(String winnerUserid) { this.winnerUserid = winnerUserid; }
    public Integer getWinnerAge() { return winnerAge; }
    public void setWinnerAge(Integer winnerAge) { this.winnerAge = winnerAge; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}