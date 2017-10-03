package nl.wiegman.homesensors.sensortag;

import nl.wiegman.homesensors.sensortag.publisher.KlimaatPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class KlimaatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KlimaatService.class);

    private final List<KlimaatPublisher> klimaatPublishers;

    @Autowired
    public KlimaatService(List<KlimaatPublisher> klimaatPublishers) {
        this.klimaatPublishers = klimaatPublishers;
    }

    public void publish(String klimaatSensorCode, BigDecimal temperatuur, BigDecimal luchtvochtigheid) {
        LOGGER.debug("Publishing to {} publishers", klimaatPublishers.size());
        klimaatPublishers.forEach(publisher -> publisher.publish(klimaatSensorCode, temperatuur, luchtvochtigheid));
    }
}