package com.preregistration.proxy.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ProxyController {

	private String protocol = "https";
	private String server = "dev.mosip.net";
	private int port = -1;
	private String cookie = "Authorization=eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJWRkkyb2Jxb0Y3VGxXcEVwUmZ3MDltaEs3ME1sNGFLaWo5M1I5ODZ4VzNRIn0.eyJqdGkiOiI2NzQ1NTUyNi0xZmUwLTRjZWUtOGIwOC1lYWVjYzI2NWQxMTAiLCJleHAiOjE2MDgxMzY0NTEsIm5iZiI6MCwiaWF0IjoxNjA4MTAwNDUxLCJpc3MiOiJodHRwczovL2Rldi5tb3NpcC5uZXQva2V5Y2xvYWsvYXV0aC9yZWFsbXMvcHJlcmVnaXN0cmF0aW9uIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImE1MjZlMjQ4LWI1YWUtNDZkNi1iNDZmLThlMzlmN2NiZDViMCIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1vc2lwLXByZXJlZy1jbGllbnQiLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiIzODY5MzY0YS05NTQyLTQyZjctYTk4ZS1lMDQ3OGE0ODE3ZDUiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vZGV2Lm1vc2lwLm5ldCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsIklORElWSURVQUwiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6ImFqYXkuYjJAbWluZHRyZWUuY29tIn0.MUYfQdX-fqYq_9aK0OEw9Ma-a4rl3H52eatHg1ihNAZO6_re2QEfoq3zFdh7vwPWZOC6rjR6B9oOx8epClGh-DXas0vlD8ZVH1WLWolI9N4fhAw2L4jZ3oQ8IxmCs4LBAOZf1JWvoPnGXm4BK7wVvx_YQgO6bYVd-8VMfo9Q74oeKnh2qh0Rbn_4X5blZLkwem2p96L6UZ3biUT7L7B_N3JrVdNZsKfWZR2zlw9ud1ohDgAZ4N0SLt-nMCTs1BmKoC-JuI9bD5r44oldY3F141tb4FiBuKI9omahpJvpT_EArd3CKLDhdXAutSTaXWb20BBTUEFOwZ6ueNRs-kfVsw";

	@RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT,
			RequestMethod.POST })
	public ResponseEntity mirrorAnyRequest(@RequestBody(required = false) String body, HttpMethod method,
			HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
		System.out.println("**********************************************************************");

		String requestUrl = request.getRequestURI();
		URI uri = new URI(protocol, null, server, port, null, null, null);
		uri = UriComponentsBuilder.fromUri(uri).path(requestUrl).query(request.getQueryString()).build(true).toUri();
		System.out.println("Calling method: " + method);
		System.out.println("Calling URL: " + uri);

		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			//ignore the host header
			if (!headerName.equalsIgnoreCase("host")) {
				headers.set(headerName, request.getHeader(headerName));
			}
		}
		//v imp to set the auth cookie
		headers.set("cookie", cookie);
		// printHeaders(headers, "request");
		HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity responseEntity = restTemplate.exchange(uri, method, httpEntity, String.class);
			System.out.println(responseEntity);
			// printHeaders(responseEntity.getHeaders(), "response");
			return responseEntity;
		} catch (HttpStatusCodeException e) {
			System.out.println("Exception: " + e.getResponseBodyAsString());

			return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders())
					.body(e.getResponseBodyAsString());
		}
	}

	private void printHeaders(HttpHeaders headers, String headersType) {
		System.out.println("With " + headersType + " Headers");
		Iterator itr = headers.entrySet().iterator();
		while (itr.hasNext()) {
			System.out.println(itr.next().toString());
		}
	}


}
