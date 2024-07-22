package com.micrservices.user_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HunterResponse {

    @JsonProperty("data")
    private Data data;

    @Getter
    @Setter
    public static class Data {
        @JsonProperty("status")
        private String status;
    }
}
