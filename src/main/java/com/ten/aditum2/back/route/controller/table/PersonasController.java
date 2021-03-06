package com.ten.aditum2.back.route.controller.table;

import com.ten.aditum2.back.model.entity.PersonasPortrait;
import com.ten.aditum2.back.model.AditumCode;
import com.ten.aditum2.back.model.ResultModel;
import com.ten.aditum2.back.service.table.PersonasLabelService;
import com.ten.aditum2.back.service.table.PersonasPortraitService;
import com.ten.aditum2.back.service.table.PersonasService;
import com.ten.aditum2.back.model.vo.Personas;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/personas")
public class PersonasController {

    private static final int NO_DELETED = 0;
    private static final int IS_DELETED = 1;

    private final PersonasService personasService;
    private final PersonasLabelService personasLabelService;
    private final PersonasPortraitService personasPortraitService;

    @Autowired
    public PersonasController(PersonasService personasService,
                              PersonasLabelService personasLabelService,
                              PersonasPortraitService personasPortraitService) {
        this.personasService = personasService;
        this.personasLabelService = personasLabelService;
        this.personasPortraitService = personasPortraitService;
    }

    /**
     * 根据personId获取用户画像标签集合
     */
    @RequestMapping(method = RequestMethod.GET)
    public Mono<ResultModel> getPersonas(Personas personas) {
        log.debug("Personas [GET] : {}", personas);

        if (personas.getPersonnelId() == null) {
            return new ResultModel(AditumCode.ERROR);
        }

        PersonasPortrait personasPortrait = new PersonasPortrait()
                .setPersonnelId(personas.getPersonnelId())
                .setIsDeleted(NO_DELETED);

        List<PersonasPortrait> personasPortraitList = personasPortraitService.select(personasPortrait);
        if (personasPortraitList.size() < 1) {
            log.warn("Personas [GET] FAILURE : {}", personas);
            return new ResultModel(AditumCode.ERROR);
        }

        PersonasPortrait select = personasPortraitList.get(0);

        String personasExt = select.getPersonasExt();
        String[] personasList = personasExt.split(",");
        if (personasList.length == 0) {
            personasList = new String[]{"小白"};
        }

        Personas personasEntity = new Personas()
                .setPersonnelId(select.getPersonnelId())
                .setPersonasList(Arrays.asList(personasList));

        log.debug("Personas [GET] SUCCESS : {} -> {}", personas, personasEntity);
        return new ResultModel(AditumCode.OK, personasEntity);
    }

    /**
     * 根据personId获取用户画像标签集合(带随机权重)
     */
    @RequestMapping(value = "/weight", method = RequestMethod.GET)
    public Mono<ResultModel> getPersonasWeight(Personas personas) {
        log.debug("PersonasWeight [GET] : {}", personas);

        if (personas.getPersonnelId() == null) {
            return new ResultModel(AditumCode.ERROR);
        }

        PersonasPortrait personasPortrait = new PersonasPortrait()
                .setPersonnelId(personas.getPersonnelId())
                .setIsDeleted(NO_DELETED);

        List<PersonasPortrait> personasPortraitList = personasPortraitService.select(personasPortrait);
        if (personasPortraitList.size() < 1) {
            log.warn("PersonasWeight [GET] FAILURE : {}", personas);
            return new ResultModel(AditumCode.ERROR);
        }

        PersonasPortrait select = personasPortraitList.get(0);

        String personasExt = select.getPersonasExt();
        String[] personasList = personasExt.split(",");
        if (personasList.length == 0) {
            personasList = new String[]{"小白"};
        }

        List<Personas.WeightsLabel> weightsLabelList = new ArrayList<>();
        for (String personasLabel : personasList) {
            Personas.WeightsLabel weightsLabel = new Personas.WeightsLabel();
            weightsLabel.setName(personasLabel);
            weightsLabel.setValue((int) (Math.random() * 100));
            weightsLabelList.add(weightsLabel);
        }

        Personas personasEntity = new Personas()
                .setPersonnelId(select.getPersonnelId())
                .setPersonasWeightList(weightsLabelList);

        log.debug("PersonasWeight [GET] SUCCESS : {} -> {}", personas, personasEntity);
        return new ResultModel(AditumCode.OK, personasEntity);
    }

}
