package com.swl.mod.rplist.controller;

import com.swl.mod.rplist.enumerated.Playfield;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Controller
public class RoleplayerController {

    @Value("${redirect.domain}")
    private String redirectDomain;

    @Autowired
    private RestTemplate proxyRestTemplate;

    private <T> ResponseEntity<T> proxyCall(HttpServletRequest request, HttpMethod method, String body, Class<T> responseType) {
        try {
            URI uri = new URI("http", null, redirectDomain, 80, request.getRequestURI(), request.getQueryString(), null);
            return proxyRestTemplate.exchange(uri, method, new HttpEntity<>(body), responseType);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = {"/", "/list"})
    public String list(Model model) {
        // TODO: http redirect
        model.addAttribute("zones", stream(Playfield.values())
                .filter(zone -> !Playfield.UNKNOWN.equals(zone))
                .sorted(comparing(Playfield::getPriority))
                .collect(toList()));
        return "list";
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public ResponseEntity<String> listJson(HttpServletRequest request, HttpMethod method, @RequestBody(required = false) String body) {
        return proxyCall(request, method, body, String.class);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ResponseEntity<String> update(HttpServletRequest request, HttpMethod method, @RequestBody(required = false) String body) {
        return proxyCall(request, method, body, String.class);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ResponseEntity<String> remove(HttpServletRequest request, HttpMethod method, @RequestBody(required = false) String body) {
        return proxyCall(request, method, body, String.class);
    }

    @RequestMapping("/list-mod")
    public ResponseEntity<String> listMod(HttpServletRequest request, HttpMethod method, @RequestBody(required = false) String body) {
        return proxyCall(request, method, body, String.class);
    }

    @RequestMapping("/list-mod-response")
    public String listCallback() {
        return "list-mod";
    }

}
