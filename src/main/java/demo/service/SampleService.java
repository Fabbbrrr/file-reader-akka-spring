package demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SampleService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void doStuff(Object o) {
        logger.info("Sample service doing stuff with: {}", o);
    }
}
