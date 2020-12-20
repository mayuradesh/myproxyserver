package com.preregistration.proxy.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ProxyController {

	private String protocol = "https";
	private String server = "qa3.mosip.net";
	private int port = -1;
	private String cookie = "Authorization=eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJrZldBZURJd0EwWHJVVWh2RE11M2JzM1JsUkRIQ1RCMUNHWG5qTlhhY2lNIn0.eyJqdGkiOiI0NGZmYzg4Yy1kOWY3LTRhNGQtOWRhZC1jNWNkOWVlZThiYWUiLCJleHAiOjE2MDg0ODMwMDMsIm5iZiI6MCwiaWF0IjoxNjA4NDQ3MDAzLCJpc3MiOiJodHRwczovL3FhMy5tb3NpcC5uZXQva2V5Y2xvYWsvYXV0aC9yZWFsbXMvcHJlcmVnaXN0cmF0aW9uIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImU3ZjEzZGIxLWY4MjktNDQ2Ny05ZTllLTY2OWIzZjMzZWJiYSIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1vc2lwLXByZXJlZy1jbGllbnQiLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiIyNTJjOWZkYS1mM2IxLTRiZGMtYjgwNy0wYjA5ZDhiOTBiZDgiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vcWEzLm1vc2lwLm5ldCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiIsIklORElWSURVQUwiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6Im1heXVyYS5kZXNobXVraEBnbWFpbC5jb20ifQ.pelMMsA39NgM3V1YMMlw4Sb_LMKTMtymbbgQf3__yTcH2osegqCPSLdt76YxAJkfcTitCFBuYt8err5Su1kz206tsijk_BYziOHiSlFxfGGfHeR7OxatzfBcXxkXC4U9S-MhwP5R70xkwG4qE3rWeO_5ip3ilDqOHjawo06qJISAM0nLMveYV5_O91N6xRxAJ7p8TjwNnp3o53Jncd4G4ABvNfVAvOazrWrtc6XyY1_adU2tT4Z4wtoZ5m8KRJ2lu7HebUnB7-eEvV1uWFyJRDrzd5RtwLVjV9jjrRB6xO3AJZtpOKvncg2tjviTMFNH4Rg8Xe8h-5ou6y4t7o7T4A";

	@RequestMapping(value = "/**", method = { RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT,
			RequestMethod.POST })
	public ResponseEntity mirrorAnyRequest(@RequestBody(required = false) String body, HttpMethod method,
			HttpServletRequest request, HttpServletResponse response)
			throws URISyntaxException, IOException, ServletException {
		// handle the multipart form data request
		if (isMultipart(request)) {
			return mirrorMultiPartRequest(method, (MultipartHttpServletRequest) request, response);
		}
		System.out.println("**********************************************************************");
		String requestUrl = request.getRequestURI();
		URI uri = new URI(protocol, null, server, port, null, null, null);
		uri = UriComponentsBuilder.fromUri(uri).path(requestUrl).query(request.getQueryString()).build(true).toUri();
		System.out.println("Calling method: " + method);
		System.out.println("Calling URL: " + uri);
		System.out.println("Request Body: " + body);
		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			// ignore the host header
			if (!headerName.equalsIgnoreCase("host")) {
				headers.set(headerName, request.getHeader(headerName));
			}
		}
		// v imp to set the auth cookie
		headers.set("cookie", cookie);
		// printHeaders(headers, "request");
		HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		try {
			System.out.println("not multipart request");
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

	private ResponseEntity mirrorMultiPartRequest(HttpMethod method, MultipartHttpServletRequest request,
			HttpServletResponse response) throws URISyntaxException, IOException, ServletException {
		System.out.println("Multi Part Request **********************************************************************");
		System.out.println(request);
		String requestUrl = request.getRequestURI();
		URI uri = new URI(protocol, null, server, port, null, null, null);
		uri = UriComponentsBuilder.fromUri(uri).path(requestUrl).query(request.getQueryString()).build(true).toUri();
		System.out.println("Calling method: " + method);
		System.out.println("Calling URL: " + uri);
		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			// ignore the host header
			if (!headerName.equalsIgnoreCase("host")) {
				headers.set(headerName, request.getHeader(headerName));
			}
		}
		// v imp to set the auth cookie
		headers.set("cookie", cookie);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		// printHeaders(headers, "request");
		HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
		RestTemplate restTemplate = new RestTemplate();
		try {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
			MultiValueMap<String, MultipartFile> map = request.getMultiFileMap();
			MultipartFile file = map.getFirst("file");
			body.add("file", file.getResource());
			body.add("Document request", request.getParameter("Document request").toString());
			System.out.println(body);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
					body, headers);
			ResponseEntity responseEntity = restTemplate.exchange(uri, method, requestEntity, String.class);
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

	private boolean isMultipart(HttpServletRequest request) {
		final String header = request.getHeader("Content-Type");
		if (header == null) {
			return false;
		}
		return header.contains("multipart/form-data");
	}

}
