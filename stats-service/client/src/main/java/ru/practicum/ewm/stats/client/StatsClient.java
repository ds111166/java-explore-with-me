package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class StatsClient extends BaseClient {


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

    public List<ViewStatsDto> getStat(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder path = new StringBuilder();
        path.append("/stats?start={start}&end={end}");
        parameters.put("start", start);
        parameters.put("end", end);
        if (uris != null) {
            for (int i = 0; i < uris.size(); i++) {
                path.append(String.format("&uris[%s]={uris%s}", i, i));
                parameters.put("uris" + i, uris.get(i));
            }
        }
        if (unique == null) {
            unique = false;
        }
        path.append("&unique={unique}");
        parameters.put("unique", unique);
        ResponseEntity<Object> objectResponseEntity = get(path.toString(), parameters);
        HttpStatus statusCode = objectResponseEntity.getStatusCode();
        if (statusCode == HttpStatus.OK) {
            try {
                return (List<ViewStatsDto>) objectResponseEntity.getBody();
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        throw new RuntimeException(objectResponseEntity.getStatusCode().getReasonPhrase());
    }

}
