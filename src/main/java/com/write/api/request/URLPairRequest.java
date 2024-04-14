package com.write.api.request;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class URLPairRequest {
    
    @SerializedName("short_url")
    private String shortURL;

    @SerializedName("long_url")
    private String longURL;
    
}
