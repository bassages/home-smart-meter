package nl.homesensors;

import nl.homesensors.sensortag.SensorTagReader;
import nl.homesensors.smartmeter.SerialSmartMeterReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationRunnerTest {

    @InjectMocks
    private ApplicationRunner applicationRunner;

    @Mock
    private SerialSmartMeterReader serialSmartMeterReader;
    @Mock
    private SensorTagReader sensorTagReader;

    @Test
    void whenInitializedThenReadersRun() throws Exception {
        // when
        applicationRunner.run();

        // then
        verify(serialSmartMeterReader).run();
        verify(sensorTagReader).run();
    }
}
