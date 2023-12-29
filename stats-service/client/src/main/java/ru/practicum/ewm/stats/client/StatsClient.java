package ru.practicum.ewm.stats.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class StatsClient extends BaseClient {

    private final ObjectMapper mapper = new ObjectMapper();

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
            path.append("&uris={uris}");
            parameters.put("uris", uris);
        }
        if (unique == null) {
            unique = false;
        }
        path.append("&unique={unique}");
        parameters.put("unique", unique);
        ResponseEntity<Object> objectResponseEntity = get(path.toString(), parameters);

        Object[] objects = (Object[]) objectResponseEntity.getBody();
        return Arrays.stream(Objects.requireNonNull(objects))
                .map(object -> mapper.convertValue(object, ViewStatsDto.class))
                .collect(Collectors.toList());
        //return get(path.toString(), parameters);
    }

}
