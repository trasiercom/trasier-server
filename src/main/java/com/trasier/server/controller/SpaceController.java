package com.trasier.server.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

import java.util.List;

@Controller("/api")
public class SpaceController {

    @Get("/accounts/{accountId}/spaces")
    public List<SpaceResponseModel> listSpaces(@PathVariable("accountId") String accountId) {
        SpaceResponseModel space = new SpaceResponseModel();
        space.setAccountId(Long.parseLong(accountId));
        space.setSpaceKey("default");
        space.setSpaceDescription("none");
        space.setClientId("none");
        space.setClientSecret("none");
        List<SpaceResponseModel> spaces = List.of(space);
        return spaces;
    }

}