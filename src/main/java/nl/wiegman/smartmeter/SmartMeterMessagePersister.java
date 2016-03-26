package nl.wiegman.smartmeter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

@Component
public class SmartMeterMessagePersister {

    private static final Logger LOG = LoggerFactory.getLogger(SmartMeterMessagePersister.class);

    @Value("${home-server-rest-service-meterstanden-url}")
    private String homeServerRestServiceMeterstandenUrl;

    @Value("${buffer-directory}")
    private File bufferDirectory;

    @PostConstruct
    public void start() {
        if (bufferDirectory.exists()) {
            LOG.info("Using messagebuffer directory: " + bufferDirectory.getAbsolutePath());
        } else {
            String message = "buffer-directory [" + bufferDirectory + "] does not exist";
            LOG.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public void persist(SmartMeterMessage smartMeterMessage) {

        try {
            String jsonMessage = createSmartMeterJsonMessage(smartMeterMessage);

            try {
                postToHomeServer(jsonMessage);

            } catch (Exception e) {
                LOG.warn("Post to " + homeServerRestServiceMeterstandenUrl + " failed. Writing message to disk", e);

                try {
                    File file = new File(bufferDirectory, System.currentTimeMillis() + ".txt");
                    FileUtils.writeStringToFile(file, jsonMessage);

                } catch (IOException e1) {
                    LOG.error("Failed to save file. Message=" + smartMeterMessage, e1);
                }
            }

        } catch (JsonProcessingException e) {
            LOG.error("Failed to map message to json. Message=" + smartMeterMessage, e);
        }
    }

    private void postToHomeServer(String jsonString) throws Exception {
        LOG.debug("Post to home-server: " + jsonString);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpPost request = new HttpPost(homeServerRestServiceMeterstandenUrl);
            StringEntity params = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
            request.setEntity(params);

            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
                throw new RuntimeException("Unexpected statusline: " + response.getStatusLine());
            }
        }
    }

    private String createSmartMeterJsonMessage(SmartMeterMessage smartMeterMessage) throws JsonProcessingException {
        HomeServerMeterstand homeServerMeterstand = new HomeServerMeterstand();
        homeServerMeterstand.setDatumtijd(smartMeterMessage.getDatetimestamp().getTime());
        homeServerMeterstand.setStroomOpgenomenVermogenInWatt(smartMeterMessage.getActualElectricityPowerDelivered().multiply(new BigDecimal(1000.0d)).intValue());
        homeServerMeterstand.setStroomTarief1(smartMeterMessage.getMeterReadingElectricityDeliveredToClientTariff1());
        homeServerMeterstand.setStroomTarief2(smartMeterMessage.getMeterReadingElectricityDeliveredToClientTariff2());
        homeServerMeterstand.setGas(smartMeterMessage.getLastHourlyValueGasDeliveredToClient());
        return new ObjectMapper().writeValueAsString(homeServerMeterstand);
    }

    private static class HomeServerMeterstand {
        private long datumtijd;
        private int stroomOpgenomenVermogenInWatt;
        private BigDecimal stroomTarief1;
        private BigDecimal stroomTarief2;
        private BigDecimal gas;

        public long getDatumtijd() {
            return datumtijd;
        }

        public void setDatumtijd(long datumtijd) {
            this.datumtijd = datumtijd;
        }

        public int getStroomOpgenomenVermogenInWatt() {
            return stroomOpgenomenVermogenInWatt;
        }

        public void setStroomOpgenomenVermogenInWatt(int stroomOpgenomenVermogenInWatt) {
            this.stroomOpgenomenVermogenInWatt = stroomOpgenomenVermogenInWatt;
        }

        public BigDecimal getStroomTarief1() {
            return stroomTarief1;
        }

        public void setStroomTarief1(BigDecimal stroomTarief1) {
            this.stroomTarief1 = stroomTarief1;
        }

        public BigDecimal getStroomTarief2() {
            return stroomTarief2;
        }

        public void setStroomTarief2(BigDecimal stroomTarief2) {
            this.stroomTarief2 = stroomTarief2;
        }

        public BigDecimal getGas() {
            return gas;
        }

        public void setGas(BigDecimal gas) {
            this.gas = gas;
        }
    }
}
