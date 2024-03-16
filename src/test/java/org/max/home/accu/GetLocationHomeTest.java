package org.max.home.accu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.max.seminar.accu.AbstractTest;
import org.max.seminar.accu.GetLocationTest;
import org.max.seminar.accu.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class GetLocationHomeTest extends AbstractTest {
    private static final Logger logger
            = LoggerFactory.getLogger(GetLocationHomeTest.class);

    @Test
    void get_shouldReturn404() throws IOException, URISyntaxException {
        logger.info("Тест код ответ 404 запущен");
        //given
        logger.debug("Формирование мока для GET /locations/v1/cities/autocomplete");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("apiKey", notMatching("A_82c9229354f849e78efe010d94150807"))
                .willReturn(aResponse()
                        .withStatus(404).withBody("NOT_FOUND")));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/locations/v1/cities/autocomplete");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "A_82c9229354f849e78efe010d94150807")
                .build();
        request.setURI(uri);
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete")));
        assertEquals(404, response.getStatusLine().getStatusCode());

    }

    @Test
    void get_shouldReturn200() throws IOException, URISyntaxException {
        logger.info("Тест код ответ 200 запущен");
        //given
        ObjectMapper mapper = new ObjectMapper();
        Location bodyOk = new Location();
        bodyOk.setKey("OK");

        Location bodyError = new Location();
        bodyError.setKey("Error");

        logger.debug("Формирование мока для GET /locations/v1/cities/autocomplete");
        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("q", equalTo("Omsk"))
                .willReturn(aResponse()
                        .withStatus(200).withBody(mapper.writeValueAsString(bodyOk))));

        stubFor(get(urlPathEqualTo("/locations/v1/cities/autocomplete"))
                .withQueryParam("q", equalTo("error"))
                .willReturn(aResponse()
                        .withStatus(200).withBody(mapper.writeValueAsString(bodyError))));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        logger.debug("http клиент создан");
        //when

        HttpGet request = new HttpGet(getBaseUrl()+"/locations/v1/cities/autocomplete");
        URI uriOk = new URIBuilder(request.getURI())
                .addParameter("q", "Omsk")
                .build();
        request.setURI(uriOk);
        HttpResponse responseOk = httpClient.execute(request);

        URI uriError = new URIBuilder(request.getURI())
                .addParameter("q", "error")
                .build();
        request.setURI(uriError);

        HttpResponse responseError = httpClient.execute(request);

        //then

        verify(2, getRequestedFor(urlPathEqualTo("/locations/v1/cities/autocomplete")));
        assertEquals(200, responseOk.getStatusLine().getStatusCode());
        assertEquals(200, responseError.getStatusLine().getStatusCode());
        assertEquals("OK", mapper.readValue(responseOk.getEntity().getContent(), Location.class).getKey());
        assertEquals("Error", mapper.readValue(responseError.getEntity().getContent(), Location.class).getKey());


    }

}
