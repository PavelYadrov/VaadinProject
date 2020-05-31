package com.netcracker.components;

import com.netcracker.dto.CategoryDTO;
import com.netcracker.dto.UserDTO;
import com.netcracker.service.CategoryService;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.QueryParameters;
import feign.FeignException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryList extends FormLayout {

        private List<Long> list;
        private AccordionPanel currentPanel;
        private FeignUserService feign;
        private UserService userService;
        private AccordionPanel mainPanel;

        public CategoryList(FeignUserService feignUserService, CategoryService categoryService,
                            UserService userService, AdvertisementsList advertisementsList, String queryParam,
                            UserDTO user) {
                this.feign = feignUserService;
                this.userService = userService;
                if (user == null) return;
                try {
                        mainPanel = new AccordionPanel();
                        currentPanel = mainPanel;
                        Span acctext = new Span("All");

                        this.setSpanListener(acctext, "1");
                        mainPanel.setSummary(acctext);

                        mainPanel.setId("1");
                        mainPanel.addThemeVariants(DetailsVariant.FILLED);

                        mainPanel.addOpenedChangeListener(openedChangeEvent -> {
                                if (openedChangeEvent.isOpened()) {

                                        if (mainPanel.getContent().count() > 0L) return;
                                        this.initAccordions("1", list).forEach(mainPanel::addContent);
                                }
                        });
                        add(mainPanel);
                } catch (FeignException.Forbidden | FeignException.BadRequest e) {
                        UI.getCurrent().getPage().setLocation("login");
                }
        }

        private List<AccordionPanel> initAccordions(String id, List<Long> list) {
                List<CategoryDTO> categoryDTOS = feign.getFirstLayerCategories(userService.getCookieByName("Authentication"), id).getBody();

                List<Span> spans = categoryDTOS.stream().filter(categoryDTO -> !categoryDTO.getHasChilds())
                        .map(categoryDTO -> {
                                Span innerText = new Span(categoryDTO.getName());
                                innerText.setId(categoryDTO.getId().toString());
                                return innerText;
                        }).collect(Collectors.toList());
                if (!spans.isEmpty()) spans.forEach(span -> {
                        // this.setSpanListener(span,span.getId().get());
                        currentPanel.addContent(setSpanListener(span, span.getId().get()));
                });

                List<AccordionPanel> accordions = categoryDTOS.stream().filter(CategoryDTO::getHasChilds).map(categoryDTO -> {

                        AccordionPanel accordion = new AccordionPanel();
                        currentPanel = accordion;
                        accordion.setId(categoryDTO.getId().toString());
                        accordion.addThemeVariants(DetailsVariant.FILLED);
                        Span acctext = new Span(categoryDTO.getName());
                        this.setSpanListener(acctext, accordion.getId().get());

                        Long currId = Long.parseLong(accordion.getId().get());

                        if (list != null && list.contains(currId)) {
                                accordion.setOpened(true);
                                list.remove(currId);
                        }

                        accordion.setSummary(acctext);

                        accordion.addOpenedChangeListener(openedChangeEvent -> {
                                if (openedChangeEvent.isOpened()) {

                                        if (accordion.getContent().count() > 0L) return;

                                        List<AccordionPanel> accordionPanels = initAccordions(accordion.getId().get(), list);
                                        accordionPanels.forEach(accordion::addContent);

                                }
                        });
                        return accordion;
                }).collect(Collectors.toList());
                return accordions;
        }

        private Div setSpanListener(Span span, String id) {
                Div wrapper = new Div();
                wrapper.add(span);
                span.getStyle().set("cursor", "pointer");
                span.getStyle().set("font-weight", "bold");
                span.getStyle().set("padding-left", "5px");
                span.getStyle().set("color", "#696969");
                wrapper.setMaxWidth("250px");
                wrapper.getStyle().set("white-space", "normal");
                span.addClickListener(spanClickEvent -> {
                        List<String> param = new ArrayList<>();
                        Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
                        param.add(id);

                        parametersMap.put("category_id", param);

                        QueryParameters qp = new QueryParameters(parametersMap);
                        UI.getCurrent().navigate("mainPage", qp);
                });
                return wrapper;
        }

        public void setOpened(String id) {
                list = feign.getCategoryList(userService.getCookieByName("Authentication"), id).getBody();
                mainPanel.setOpened(true);
                list.remove(1L);
        }
}
