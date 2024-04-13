package com.write.api.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.write.api.exception.BadRequestException;
import com.write.api.repository.UrlRepository;
import com.write.api.utils.Constants;

@Service
public class UrlService {
    
    @Autowired
    private UrlRepository urlRepository;

    public String shortenURL(String longURL) throws BadRequestException {
        if (longURL == null || longURL.isEmpty()) {
            throw new BadRequestException("URL cannot be empty nor null");
        }

        String fragment = generateFragment(longURL);

        while (urlRepository.checkIfShortUrlExists(fragment)) {
            fragment = generateFragment(longURL);
        }
        
        return save(fragment, longURL);
    }

    private String save(String fragment, String longURL) {
        try {
            urlRepository.insert(fragment, longURL);
            return Constants.SHORT_URL_PREFIX + fragment;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateRandomSuffix(int length) {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder suffix = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < length; i++) {
            suffix.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return suffix.toString();
    }

    private String generateFragment(String longURL) {
        String randomSuffix = generateRandomSuffix(6);

        String longURLWithSuffix = longURL + randomSuffix;

        BigInteger urlHash = generateUrlHashInt(longURLWithSuffix);
        
        String base62Fragment = encodeBase62(urlHash);

        return base62Fragment.substring(0, 6);
    }

    private BigInteger generateUrlHashInt(String longURL) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(longURL.getBytes());
            BigInteger hashInt = new BigInteger(1, digest);
            return hashInt;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }   

    private String encodeBase62(BigInteger num) {
        String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder base62 = new StringBuilder();
        
        while (num.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = num.divideAndRemainder(BigInteger.valueOf(62));
            base62.insert(0, alphabet.charAt(divmod[1].intValue()));
            num = divmod[0];
        }
        return base62.toString();
    }

}
