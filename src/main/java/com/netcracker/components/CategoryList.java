package com.netcracker.components;

import com.netcracker.dto.CategoryDTO;
import com.netcracker.service.CategoryService;
import com.netcracker.service.FeignUserService;
import com.netcracker.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.QueryParameters;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryList extends FormLayout {

        private List<CategoryDTO> categories;

        private List<AccordionPanel> panels;

        private UserService userService;

        public CategoryList(@Autowired FeignUserService feignUserService, @Autowired CategoryService categoryService,
                            @Autowired UserService userService,AdvertisementsList advertisementsList,String queryParam) {
                try{
                        AccordionPanel panel = new AccordionPanel();
                        panel.setSummaryText("All");
                        HorizontalLayout horizontalLayout = new HorizontalLayout(new Span("All"),new Button("get"));
                        panel.setSummary(horizontalLayout);
                        panel.setId("1");
                        panel.addThemeVariants(DetailsVariant.FILLED);
                      //  if (Integer.parseInt(queryParam)>Integer.parseInt("1")) panel.setOpened(true);
                        panel.setOpened(true);

                        panel.addOpenedChangeListener(openedChangeEvent -> {
                                if(openedChangeEvent.isOpened()) {
                                        List<String> param = new ArrayList<>();
                                        Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
                                        param.add("1");

                                        parametersMap.put("category_id", param);

                                        QueryParameters qp = new QueryParameters(parametersMap);
                                        this.getUI().ifPresent(ui -> ui.navigate("mainPage", qp));
                                        UI.getCurrent().navigate("mainPage", qp);

                                        if (panel.getContent().count() > 1L) return;
                                        categoryService.initAccordions("1",advertisementsList,queryParam).forEach(panel::addContent);
                                }
                        });
                        add(panel);
                }
                catch (FeignException.Forbidden | FeignException.BadRequest e){
                        UI.getCurrent().getPage().setLocation("login");
                }
        }
}
