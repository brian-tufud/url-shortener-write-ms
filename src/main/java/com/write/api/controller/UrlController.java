package com.write.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.write.api.request.ShortenURLRequest;
import com.write.api.service.URLService;
import com.write.api.utils.UtilsService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class URLController {

    private final URLService urlService;
    private final UtilsService utilsService;

    public URLController(URLService urlService, UtilsService utilsService) {
        super();
        this.urlService = urlService;
        this.utilsService = utilsService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenURL(HttpServletRequest request, @RequestBody ShortenURLRequest body) throws Exception {

        String shortURL = urlService.shortenURL(body.getURL());

        HttpHeaders responseHeaders = utilsService.getResponseHeaders();
        return ResponseEntity.ok().headers(responseHeaders).body(shortURL);
    }

    @DeleteMapping("/{short_url}") 
    public ResponseEntity<Void> deleteShortURL(HttpServletRequest request,
        @PathVariable(value = "short_url") String shortURL) throws Exception {

        urlService.deleteShortURL(shortURL);

        HttpHeaders responseHeaders = utilsService.getResponseHeaders();
        return ResponseEntity.ok().headers(responseHeaders).build();
    }
    
}
