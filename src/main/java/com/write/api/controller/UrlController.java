package com.write.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.write.api.service.UrlService;
import com.write.api.utils.UtilsService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UrlController {

    private final UrlService urlService;
    private final UtilsService utilsService;

    public UrlController(UrlService urlService, UtilsService utilsService) {
        super();
        this.urlService = urlService;
        this.utilsService = utilsService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenURL(HttpServletRequest request) throws Exception {

        String shortUrl = urlService.shortenURL(request.getParameter("url"));

        HttpHeaders responseHeaders = utilsService.getResponseHeaders();
        return ResponseEntity.ok().headers(responseHeaders).body(shortUrl);
    }
    
}
