package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class StatsClient extends BaseClient{
    @Autowired
    public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }
    public ResponseEntity<Object> createEndpointHit(EndpointHitDto newEndpointHit) {
        return post("/hit", newEndpointHit);
    }

    public ResponseEntity<Object> getStat(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder path = new StringBuilder();
        path.append("/stats?start={start}&end={end}");
        parameters.put("start", start);
        parameters.put("end", end);
        if(uris != null) {
            path.append("&uris={uris}");
            parameters.put("uris", uris);
        }
        if(unique == null) {
            unique = false;
        }
        path.append("&unique={unique}");
        parameters.put("unique", unique);
        return get(path.toString(), parameters);
    }

}
