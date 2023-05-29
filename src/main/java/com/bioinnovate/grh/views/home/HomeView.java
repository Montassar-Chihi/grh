package com.bioinnovate.grh.views.home;

import com.bioinnovate.grh.data.entity.*;
import com.bioinnovate.grh.data.service.*;
import com.bioinnovate.grh.views.main.MainView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.bioinnovate.grh.data.utils.UpdateSoldeDaysOff.countBusinessDaysBetween;
import static java.lang.Math.round;


@Route(value = "dashboard", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport("./styles/views/main/main-view.css")
@Secured({"ADMIN","USER","SUPER USER"})
public class HomeView extends Div {

    public HomeView(@Autowired AbsenceService absenceService,@Autowired EmployeeService employeeService,
                    @Autowired ReclamationService reclamationService,@Autowired DepartmentService departmentService,
                    @Autowired OvertimeService overtimeService,@Autowired DelaysService delaysService){
        add(createStatsLayout(delaysService ,reclamationService, overtimeService,employeeService));
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.add(createChartForAbsencesByDepartment(departmentService,employeeService));
        H3 titleUpload = new H3("Mise à jour d'historique");
        titleUpload.getStyle().set("align-text","center").set("width","100%").set("margin","1rem auto");

        MemoryBuffer memoryBufferProduct = new MemoryBuffer();

        Upload uploadProduct = new Upload(memoryBufferProduct);
        uploadProduct.addFinishedListener(e -> {
            InputStream inputStream = memoryBufferProduct.getInputStream();
            System.out.println("Processing File");
            try {
                uploadFile(inputStream,employeeService,absenceService,delaysService,overtimeService);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        uploadProduct.setWidth("80%");
        VerticalLayout verticalLayout = new VerticalLayout(createChartForAbsences(absenceService,employeeService),titleUpload,uploadProduct);
        verticalLayout.setWidth("50%");
        horizontalLayout.add(verticalLayout);
        add(horizontalLayout);
    }

    private void uploadFile(InputStream inputStream, EmployeeService employeeService,
                            AbsenceService absenceService, DelaysService delaysService,
                            OvertimeService overtimeService) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);
        ArrayList<Double> idsToSkip = new ArrayList<>();
        for (int j = 0; j < 1; j++) {
            Sheet firstSheet = workbook.getSheetAt(j);
            Iterator<Row> iterator = firstSheet.iterator();
            iterator.next();
            int i = 1;
            while (iterator.hasNext()) {
                System.out.println("Line : " + i);
                i++;
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                Double employee = null;
                Employee employeeToUpdate = new Employee();
                Cell cell = cellIterator.next();
                if (cell != null) {
                    employee = cell.getNumericCellValue();
                }
                System.out.println("employee : "+employee);
                if (employee != null && !idsToSkip.contains(employee)) {
                    employeeToUpdate = employeeService.get(employee.intValue()).get();
                    idsToSkip.add(employee);
                    LocalDate date = LocalDate.parse(cellIterator.next().getStringCellValue());
                    List<LocalTime> startTimes = new ArrayList<>();
                    List<LocalTime> endTimes = new ArrayList<>();

                    Iterator<Row> iteratorForEmployees = firstSheet.iterator();
                    iteratorForEmployees.next();
                    // Iterate over all rows for the same employee and date
                    while (iteratorForEmployees.hasNext()) {
                        Row row = iteratorForEmployees.next();
                        Cell employeeCell = row.getCell(0);
                        if (employeeCell != null){
                            if (employeeCell.getNumericCellValue() != employee){
                                continue;
                            }
                        }else{
                            continue;
                        }
                        Cell dateCell = row.getCell(1);
                        if (dateCell == null || dateCell.getCellType() != CellType.STRING) {
                            continue; // Stop if the date cell is empty or not a string
                        }
                        LocalDate rowDate = LocalDate.parse(dateCell.getStringCellValue());
                        if (!rowDate.isEqual(date)) {
                            continue; // Stop if the row date is different
                        }

                        Cell startTimeCell = row.getCell(2);
                        Cell endTimeCell = row.getCell(3);

                        if (startTimeCell == null || endTimeCell == null ||
                                startTimeCell.getCellType() != CellType.STRING ||
                                endTimeCell.getCellType() != CellType.STRING) {
                            continue; // Skip rows with missing or invalid time values
                        }

                        LocalTime startTime = LocalTime.parse(startTimeCell.getStringCellValue());
                        LocalTime endTime = LocalTime.parse(endTimeCell.getStringCellValue());

                        startTimes.add(startTime);
                        endTimes.add(endTime);
                    }

                    int totalSeconds = 0;
                    for (int k = 0; k < startTimes.size(); k++) {
                        LocalTime start = startTimes.get(k);
                        LocalTime end = endTimes.get(k);
                        totalSeconds += start.until(end, ChronoUnit.SECONDS);
                    }
                    System.out.println(totalSeconds + "*************************************");
                    if (startTimes.isEmpty() || totalSeconds < (8*3600)) {
                        // Employee is absent or late
                        if (totalSeconds <= (4*3600) || startTimes.isEmpty()) {
                            Absences absences = new Absences();
                            absences.setDate(Date.valueOf(date));
                            if (totalSeconds/(4*3600) <=0.5){
                                absences.setDuration(0.5);
                            }else {
                                absences.setDuration(1.0);
                            }
                            absences.setJustified(false);
                            absenceService.update(absences);
                            List<Absences> absencesList = employeeToUpdate.getAbsences();
                            absencesList.add(absences);
                            employeeToUpdate.setAbsences(absencesList);
                        } else {
                            Delays delays = new Delays();
                            delays.setDate(Date.valueOf(date));
                            delays.setDuration(8*3600 - totalSeconds);
                            delaysService.update(delays);
                            List<Delays> delaysList = employeeToUpdate.getDelays();
                            delaysList.add(delays);
                            employeeToUpdate.setDelays(delaysList);
                        }
                        employeeService.update(employeeToUpdate);
                    } else {
                        // Employee has overtime
                        Overtime overtime = new Overtime();
                        overtime.setDate(Date.valueOf(date));
                        overtime.setDuration(totalSeconds - 8*3600);
                        overtimeService.update(overtime);
                        List<Overtime> overtimeList = employeeToUpdate.getOvertime();
                        overtimeList.add(overtime);
                        employeeToUpdate.setOvertime(overtimeList);
                        employeeService.update(employeeToUpdate);
                    }
                }
            }
        }
        workbook.close();
        inputStream.close();
    }

    private Chart createChartForAbsences(AbsenceService absenceService, EmployeeService employeeService){
        Chart chart = new Chart(ChartType.SOLIDGAUGE);

        Configuration configuration = chart.getConfiguration();

        Pane pane = configuration.getPane();
        pane.setCenter(new String[] {"50%", "50%"});
        pane.setStartAngle(-90);
        pane.setEndAngle(90);

        Background paneBackground = new Background();
        paneBackground.setInnerRadius("60%");
        paneBackground.setOuterRadius("100%");
        paneBackground.setShape(BackgroundShape.ARC);
        pane.setBackground(paneBackground);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTickAmount(2);
        yAxis.setTitle("Heures de travail");
        yAxis.setMinorTickInterval("null");
        yAxis.getTitle().setY(-50);
        yAxis.getLabels().setY(16);
        yAxis.setMin(0);
        int hoursPerMonth = employeeService.findAll().size()*countBusinessDaysBetween(LocalDate.of(LocalDate.now().getYear(),LocalDate.now().getMonth(),1 ),LocalDate.now()) * 8;
        yAxis.setMax(hoursPerMonth);


        PlotOptionsSolidgauge plotOptionsSolidgauge = new PlotOptionsSolidgauge();

        DataLabels dataLabels = plotOptionsSolidgauge.getDataLabels();
        dataLabels.setY(5);
        dataLabels.setUseHTML(true);

        configuration.setPlotOptions(plotOptionsSolidgauge);

        DataSeries series = new DataSeries("Absences");

        DataSeriesItem item = new DataSeriesItem();
        double value ;
        try {
            value = absenceService.findAbsencesByMonth() * 8;
        }catch (Exception e){
            value = 0.0;
        }
        item.setY(value);
//        item.setColorIndex(2);
        item.setClassName("myClassName");
        DataLabels dataLabelsSeries = new DataLabels();
        dataLabelsSeries.setFormat("<div style=\"text-align:center\"><span style=\"font-size:25px;"
                + "color:black' + '\">{y}</span><br/>"
                + "<span style=\"font-size:12px;color:silver\">Heures d'absences</span></div>");

        item.setDataLabels(dataLabelsSeries);

        series.add(item);

        configuration.addSeries(series);

        return chart;
    }

    private Chart createChartForAbsencesByDepartment(DepartmentService departmentService,EmployeeService employeeService){
        Chart chart = new Chart(ChartType.BAR);
        Configuration conf = chart.getConfiguration();
        chart.setHeightFull();

        conf.setTitle("Comparaison Entre Heures supplémentaires et Retards par département en Mois de "+LocalDate.now().getMonth().name());

        List<Department> departments = departmentService.findAll();
        String[] categories = new String[] { "IT","Comptabilité","Export","Resources Humaines" };

        XAxis x1 = new XAxis();
        conf.addxAxis(x1);
        x1.setCategories(categories);
        x1.setReversed(false);

        XAxis x2 = new XAxis();
        conf.addxAxis(x2);
        x2.setCategories(categories);
        x2.setOpposite(true);
        x2.setReversed(false);
        x2.setLinkedTo(x1);

        YAxis y = new YAxis();
        Double max = 0.0;
        for (int i=0;i<4;i++){
            if (max < getDelaysPerDepartment(employeeService, departments.get(i))){
                max = getDelaysPerDepartment(employeeService, departments.get(i));
            }
            if (max < getOvertimePerDepartment(employeeService, departments.get(i))){
                max = getOvertimePerDepartment(employeeService, departments.get(i));
            }
        }
        y.setMin(-max);
        y.setMax(max);
        y.setTitle(new AxisTitle(""));
        conf.addyAxis(y);

        PlotOptionsSeries plot = new PlotOptionsSeries();
        plot.setStacking(Stacking.NORMAL);
        conf.setPlotOptions(plot);
        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("function() {return '<b>'+ this.series.name +', age '+ this.point.category +'</b><br/>'+ 'Population: '+ Highcharts.numberFormat(Math.abs(this.point.y), 0)}");
        conf.setTooltip(tooltip);

        conf.addSeries(new ListSeries("Retards",
                (-1)*getDelaysPerDepartment(employeeService, departments.get(0)),
                (-1)*getDelaysPerDepartment(employeeService, departments.get(1)),
                (-1)*getDelaysPerDepartment(employeeService, departments.get(2)),
                (-1)*getDelaysPerDepartment(employeeService, departments.get(3))));
        conf.addSeries(new ListSeries("Heures Supplémentaires",
                getOvertimePerDepartment(employeeService, departments.get(0)),
                getOvertimePerDepartment(employeeService, departments.get(1)),
                getOvertimePerDepartment(employeeService, departments.get(2)),
                getOvertimePerDepartment(employeeService, departments.get(3))));
        return chart;
    }

    private HorizontalLayout createStatsLayout(DelaysService delaysService, ReclamationService reclamationService,
                                               OvertimeService overtimeService, EmployeeService employeeService){

        Div reclamationDiv = createStatsBox("red",VaadinIcon.FILE,"Reclamations",reclamationService.findAll().size()+"");
        reclamationDiv.getStyle().set("cursor","pointer");
        reclamationDiv.addClickListener(event -> {
            Dialog dialog = new Dialog();
            dialog.setSizeFull();
            Button closeDialog = new Button(new Icon(VaadinIcon.CLOSE));
            closeDialog.addClickListener(e -> {
                dialog.close();
            });
            dialog.add(closeDialog);
            closeDialog.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            closeDialog.getStyle().set("float","right").set("color","red");
            H1 title = new H1("Reclamations");
            title.getStyle().set("margin","1rem auto").set("wdith","100%");
            dialog.add(title);
            HorizontalLayout reclamationsLayout = new HorizontalLayout();
            reclamationsLayout.add(createReclamations(reclamationService,reclamationsLayout));
            dialog.add(reclamationsLayout);
            add(dialog);
            dialog.open();
        });
        double overtime ;
        try {
            overtime = overtimeService.findTotalOvertime()/3600;
        }catch (Exception e){
            overtime = 0.0;
        }
        Div overtimeDiv = createStatsBox("green",VaadinIcon.PLUS,"Heures Supplémentaires",overtime+" H");
        Div Div = createStatsBox("blue",VaadinIcon.USER,"En congé",employeeService.findEmployeesInDaysOff()+"");
        double delays ;
        try {
            delays = (delaysService.findTotalDelays()/3600)
                    /
                    (employeeService.findAll().size()*countBusinessDaysBetween(LocalDate.of(LocalDate.now().getYear(),LocalDate.now().getMonth(),1 ),LocalDate.now()));
        }catch (Exception e){
            delays = 0.0;
        }
        Div absencesDiv = createStatsBox("orange",VaadinIcon.CLOSE,"Retard (en moyenne)",delays+"\n H/J par employé");

        HorizontalLayout horizontalLayout = new HorizontalLayout(Div,absencesDiv,overtimeDiv,reclamationDiv);
        return horizontalLayout;
    }

    private ListBox<Reclamation> createReclamations(ReclamationService reclamationService, HorizontalLayout messageLayout){
        ListBox<Reclamation> reclamationListBox = new ListBox<>();
        reclamationListBox.setItems(reclamationService.findAll());
        reclamationListBox.setHeightFull();
        reclamationListBox.setWidth("50%");
        reclamationListBox.setRenderer(new ComponentRenderer<>(reclamation -> {
            HorizontalLayout row = new HorizontalLayout();
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.setClassName("row");
            Avatar avatar = new Avatar();
            avatar.setName(reclamation.getEmployee().getFirstName()+" "+reclamation.getEmployee().getLastName());
            if (reclamation.getEmployee().getPicture() == null){
                if (reclamation.getEmployee().getGender()){
                    avatar.setImage("images/female.png");
                }else {
                    avatar.setImage("images/male.png");
                }
            }else{
                avatar.setImage("images/"+reclamation.getEmployee().getPicture());
            }

            Span name = new Span(reclamation.getEmployee().getFirstName()+" "+reclamation.getEmployee().getLastName());
            Span profession = new Span(reclamation.getEmployee().getPosition()+" @ "+ reclamation.getEmployee().getDepartment().getName()+" Départment");
            Span phone = new Span(String.valueOf(reclamation.getEmployee().getPhone()));
            profession.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");
            phone.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "var(--lumo-font-size-s)");

            VerticalLayout personalDetails = new VerticalLayout(name, profession,phone);
            personalDetails.setPadding(false);
            personalDetails.setSpacing(false);
            personalDetails.setWidth("90%");

            Icon icon = new Icon(VaadinIcon.TRASH);
            icon.setSize("1.75rem");
            Button delete = new Button(icon);
            delete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                reclamationService.delete(reclamation.getId());
                reclamationListBox.setItems(reclamationService.findAll());
                
            });
            VerticalLayout operations = new VerticalLayout(delete);
            operations.setPadding(false);
            operations.setSpacing(false);
            operations.setWidth("auto");

            row.add(avatar, personalDetails, operations);
            row.getStyle().set("line-height", "var(--lumo-line-height-m)");
            row.add(new Hr());

            personalDetails.addClickListener(event -> {
                messageLayout.removeAll();
                messageLayout.add(reclamationListBox);
                messageLayout.add(new H3(String.valueOf(reclamation.getDate())));
                messageLayout.add(new Span(reclamation.getMessage()));
            });
            return row;
        }));
        return reclamationListBox;
    }

    private Double getDelaysPerDepartment(EmployeeService employeeService,Department department){
        List<Employee> employees = employeeService.findAllEmployeesForOneDepartment(department);
        double delayDuration = 0;
        for (Employee employee : employees) {
            for (Delays delay : employee.getDelays()) {
                delayDuration += delay.getDuration();
            }
        }
        return delayDuration / 3600;
    }

    private Double getOvertimePerDepartment(EmployeeService employeeService,Department department){
        List<Employee> employees = employeeService.findAllEmployeesForOneDepartment(department);
        double OvertimeDuration = 0;
        for (Employee employee : employees) {
            for (Overtime Overtime : employee.getOvertime()) {
                OvertimeDuration += Overtime.getDuration();
            }
        }
        return OvertimeDuration / 3600;
    }

    private Double getAbsencesPerDepartment(EmployeeService employeeService,Department department){
        List<Employee> employees = employeeService.findAllEmployeesForOneDepartment(department);
        double AbsencesDuration = 0;
        for (Employee employee : employees) {
            for (Absences Absences : employee.getAbsences()) {
                AbsencesDuration += Absences.getDuration();
            }
        }
        return AbsencesDuration;
    }

    private Div createStatsBox(String color,VaadinIcon vaadinIcon,String informationT,String info){
        Icon icon = new Icon(vaadinIcon);
        icon.setSize("25%");
        icon.setColor(color);
        Span informationTitle = new Span(informationT);
        informationTitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("wdith","100%")
                .set("margin","auto")
                .set("font-size", "var(--lumo-font-size-m)");
        Span information = new Span(info);
        information.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin","auto")
                .set("wdith","100%")
                .set("font-size", "var(--lumo-font-size-xl)");
        VerticalLayout verticalLayout = new VerticalLayout(informationTitle,information);
        verticalLayout.setWidth("75%");
        verticalLayout.setAlignItems(FlexComponent.Alignment.END);
        verticalLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        HorizontalLayout horizontalLayout = new HorizontalLayout(icon,verticalLayout);
        horizontalLayout.setWidthFull();
        Span mois = new Span("Mois : " + LocalDate.now().getMonth().name());
        mois.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin","auto")
                .set("wdith","100%")
                .set("font-size", "var(--lumo-font-size-s)");
        VerticalLayout verticalLayout1 = new VerticalLayout(horizontalLayout,new Hr(),mois);
        verticalLayout1.getStyle().set("width","100%").set(
                "height","100%").set(
                "display","flex").set(
                "flex-flow","wrap");

        Div div = new Div(verticalLayout1);
        div.getStyle().set("padding","0.75rem").set("margin","1rem auto")
                .set("box-shadow","3px 2px 14px -4px "+color+", 5px 5px 15px 5px rgb(0 0 0 / 0%)")
                .set("width","20%");

        return div;
    }
}
