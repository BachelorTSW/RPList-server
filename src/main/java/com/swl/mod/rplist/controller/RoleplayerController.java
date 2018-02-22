package com.swl.mod.rplist.controller;

import com.swl.mod.rplist.dto.PlayfieldDto;
import com.swl.mod.rplist.dto.PlayfieldInstanceDto;
import com.swl.mod.rplist.dto.UpdateRoleplayerDto;
import com.swl.mod.rplist.model.Roleplayer;
import com.swl.mod.rplist.service.RoleplayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.SortedSet;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;

@Controller
public class RoleplayerController {

    private static final int SWL_SUPPORTED_URL_LENGTH = 15381;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoleplayerService roleplayerService;

    @RequestMapping(value = {"/", "/list"})
    public String list() {
        return "list";
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public SortedSet<PlayfieldDto> listJson() {
        return roleplayerService.getAll(true);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ResponseEntity<String> update(UpdateRoleplayerDto updateRoleplayerDto) {
        if (updateRoleplayerDto.getPlayerId() == null) {
            return badRequest().body("No playerId found in the request.");
        }
        if (updateRoleplayerDto.getNick() == null) {
            return badRequest().body("No nick found in the request.");
        }
        if (updateRoleplayerDto.getFirstName() == null) {
            return badRequest().body("No firstName found in the request.");
        }
        if (updateRoleplayerDto.getLastName() == null) {
            return badRequest().body("No lastName found in the request.");
        }
        if (updateRoleplayerDto.getPlayfieldId() == null) {
            return badRequest().body("No playfieldId found in the request.");
        }

        roleplayerService.update(updateRoleplayerDto);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ResponseEntity<String> remove(@RequestParam("playerId") Long playerId) {
        roleplayerService.remove(playerId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping("/list-mod")
    public String listMod() {
        String query = "List of roleplayers temporarily unavailable=1_Unavailable";
        try {
            query = roleplayerService.getAll(false).stream()
                    .map(PlayfieldDto::getPlayfieldInstances)
                    .flatMap(Collection::stream)
                    .map(RoleplayerController::instanceToString)
                    .collect(Collectors.joining("&"));

        } catch (Exception e) {
            logger.error("Unable to get list of roleplayers", e);
        }
        query = query.replace(" ", "%20");
        if (!query.isEmpty()) {
            query = "?" + query;
        }
        if (query.length() > SWL_SUPPORTED_URL_LENGTH - 80) {
            logger.error("Exceeding max length of SWL Browser's supported URL length {} with {}", SWL_SUPPORTED_URL_LENGTH, query.length());
            query = query.substring(0, SWL_SUPPORTED_URL_LENGTH - 80);
            query = query.substring(0, query.lastIndexOf(','));
        }
        return "redirect:/list-mod-response" + query;
    }

    private static String instanceToString(PlayfieldInstanceDto instance) {
        String roleplayersText = instance.getRoleplayers().stream()
                .map(RoleplayerController::roleplayerToString)
                .collect(Collectors.joining(","));
        return instance.getPlayfield().getName() + " " + instance.getInstanceNumber() + "=" + roleplayersText;
    }

    private static String roleplayerToString(Roleplayer roleplayer) {
        String autoMeetup = "2";
        if (roleplayer.getAutoMeetup() != null) {
            autoMeetup = roleplayer.getAutoMeetup() ? "1" : "0";
        }
        return roleplayer.getId() + "_" + roleplayer.getNick() + "_" + autoMeetup;
    }

    @RequestMapping("/list-mod-response")
    public String listCallback() {
        return "list-mod";
    }

}
