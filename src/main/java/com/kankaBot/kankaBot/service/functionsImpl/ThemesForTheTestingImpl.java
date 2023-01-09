package com.kankaBot.kankaBot.service.functionsImpl;

import com.kankaBot.kankaBot.service.functions.MarginFunc;
import org.springframework.stereotype.Component;

@Component
public class ThemesForTheTestingImpl {
    private final MarginFunc marginFunc;

    public ThemesForTheTestingImpl(MarginFunc marginFunc) {
        this.marginFunc = marginFunc;
    }


}
