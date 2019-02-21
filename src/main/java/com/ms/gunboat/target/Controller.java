package com.ms.gunboat.target;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private ConcurrentHashMap<String, MockServ> downstreams = new ConcurrentHashMap<>();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping("/greeting/v{majorVersion:[\\d]+}.{minorVersion:[\\d]+}")
    public Greeting greetingSpecific(@PathVariable(value="majorVersion") long major, @PathVariable(value="minorVersion") long minor) {
        Integer bounces = downstreams.reduceEntries(10, (m -> m.getValue().getMajor() == major ? (m.getValue().isUp() ? 1 : 0) : 0), ((a, b) -> a + b));
        try {
            TimeUnit.MILLISECONDS.sleep(bounces*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Greeting(counter.incrementAndGet(),String.format(template, "World"));
    }

    @RequestMapping("/up/v{majorVersion:[\\d]+}.{minorVersion:[\\d]+}")
    public String up(@PathVariable(value="majorVersion") long major, @PathVariable(value="minorVersion") long minor) {
            UUID uuid = UUID.randomUUID();
        String dss_id = uuid.toString();
        MockServ mockServ = new MockServ(dss_id, major, minor, true);
        downstreams.put(dss_id, mockServ);
        return dss_id;
    }


    @RequestMapping("/down/{uuid}")
    public Boolean down(@PathVariable(value="uuid") String uuid) {
        MockServ mockServ = downstreams.get(uuid);
        if(mockServ != null){
            mockServ.setUp(false);
            return true;
        }
        return false;
    }

    @RequestMapping("/metric")
    public Integer metric() {
        Integer bounces = downstreams.reduceEntries(10, (m -> (m.getValue().isUp() ? 1 : 0)), ((a, b) -> a + b));
        return bounces;
    }
}
