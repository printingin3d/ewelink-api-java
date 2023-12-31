
package com.github.realzimboguy.ewelink.api.model.login;

public class User {
    private Timezone timezone;
    private Integer accountLevel;
    private String countryCode;
    private String email;
    private String apikey;
    private Boolean accountConsult;
    private Boolean appForumEnterHide;
    private String appVersion;
    private Boolean denyRecharge;
    private String ipCountry;
    public Timezone getTimezone() {
        return timezone;
    }
    public Integer getAccountLevel() {
        return accountLevel;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getEmail() {
        return email;
    }
    public String getApikey() {
        return apikey;
    }
    public Boolean getAccountConsult() {
        return accountConsult;
    }
    public Boolean getAppForumEnterHide() {
        return appForumEnterHide;
    }
    public String getAppVersion() {
        return appVersion;
    }
    public Boolean getDenyRecharge() {
        return denyRecharge;
    }
    public String getIpCountry() {
        return ipCountry;
    }
}
