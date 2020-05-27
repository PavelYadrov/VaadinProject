package com.netcracker.service;


import com.netcracker.components.AdvertisementsList;
import com.netcracker.dto.CategoryDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.router.QueryParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private FeignUserService feignUserService;
    private UserService userService;

    @Autowired
    public CategoryService(FeignUserService feignUserService, UserService userService) {
        this.feignUserService = feignUserService;
        this.userService = userService;
    }


   public List<AccordionPanel> initAccordions(String id,AdvertisementsList advertisementsList,String queryParam){
        List<CategoryDTO> categoryDTOS = feignUserService.getFirstLayerCategories(userService.getCookieByName("Authorization"),id).getBody();

        List<AccordionPanel> accordions =categoryDTOS.stream().map(categoryDTO -> {

            AccordionPanel accordion = new AccordionPanel();
            accordion.setSummaryText(categoryDTO.getName());
            accordion.setId(categoryDTO.getId().toString());
            accordion.addThemeVariants(DetailsVariant.FILLED);

            if(accordion.getId().get().equals(queryParam)) accordion.setOpened(true);

            accordion.addOpenedChangeListener(openedChangeEvent -> {
                if(openedChangeEvent.isOpened()) {

                    List<String> param = new ArrayList<>();
                    Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
                    param.add(accordion.getId().get());

                    parametersMap.put("category_id", param);

                    QueryParameters qp = new QueryParameters(parametersMap);
                    UI.getCurrent().navigate("mainPage", qp);

                    if (accordion.getContent().count() > 0L) return;

                    List<AccordionPanel> accordionPanels = initAccordions(accordion.getId().get(),advertisementsList,queryParam);
                    accordionPanels.forEach(accordion::addContent);

                }
            });
            return accordion;
        }).collect(Collectors.toList());
        return accordions;
    }
}
