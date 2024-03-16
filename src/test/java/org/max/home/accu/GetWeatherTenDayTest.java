package org.max.home.accu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;
import org.max.home.accu.weather.DailyForecast;
import org.max.home.accu.weather.Headline;
import org.max.home.accu.weather.Weather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class GetWeatherTenDayTest extends AbstractTest{

    private static final Logger logger
            = LoggerFactory.getLogger(GetWeatherTenDayTest.class);

    @Test
    void get_shouldReturn401() throws IOException, URISyntaxException {
        logger.info("Тест на 10 дней код ответ 401 запущен");
        //given
        logger.debug("Формирование мока для GET /forecasts/v1/daily/10day/%7BlocationKey%7D");
        stubFor(get(urlPathEqualTo("/forecasts/v1/daily/10day/%7BlocationKey%7D"))
                .withQueryParam("apiKey", matching("A_82c9229354f849e78efe010d94150807"))
                .willReturn(aResponse()
                        .withStatus(401).withBody("ERROR")));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(getBaseUrl()+"/forecasts/v1/daily/10day/%7BlocationKey%7D");
        URI uri = new URIBuilder(request.getURI())
                .addParameter("apiKey", "A_82c9229354f849e78efe010d94150807")
                .build();
        request.setURI(uri);
        logger.debug("http клиент создан");
        //when
        HttpResponse response = httpClient.execute(request);
        //then
        verify(getRequestedFor(urlPathEqualTo("/forecasts/v1/daily/10day/%7BlocationKey%7D")));
        assertEquals(401, response.getStatusLine().getStatusCode());
        assertEquals("ERROR", convertResponseToString(response));

    }
}
