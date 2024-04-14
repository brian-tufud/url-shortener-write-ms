package com.write.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.write.api.dto.CrudStatusDto;
import com.write.api.dto.ShortURLDto;
import com.write.api.dto.CrudStatusDto.CrudStatus;
import com.write.api.request.ShortenURLRequest;
import com.write.api.service.URLService;
import com.write.api.utils.UtilsService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/url")
public class URLController {

    private final URLService urlService;
    private final UtilsService utilsService;

    public URLController(URLService urlService, UtilsService utilsService) {
        super();
        this.urlService = urlService;
        this.utilsService = utilsService;
    }

    @PostMapping()
    public ResponseEntity<ShortURLDto> shortenURL(HttpServletRequest request, @RequestBody ShortenURLRequest body) throws Exception {

        ShortURLDto shortURL = urlService.shortenURL(body.getLongURL());

        HttpHeaders responseHeaders = utilsService.getResponseHeaders();
        return ResponseEntity.ok().headers(responseHeaders).body(shortURL);
    }

    @DeleteMapping("/{short_url}") 
    public ResponseEntity<CrudStatusDto> deleteShortURL(HttpServletRequest request,
        @PathVariable(value = "short_url") String shortURL) throws Exception {

        urlService.deleteShortURL(shortURL);

        CrudStatusDto response = CrudStatusDto.builder().status(CrudStatus.DELETED).build();

        HttpHeaders responseHeaders = utilsService.getResponseHeaders();
        return ResponseEntity.ok().headers(responseHeaders).body(response);
    }
    
}
