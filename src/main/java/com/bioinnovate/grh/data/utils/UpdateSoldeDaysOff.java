package com.bioinnovate.grh.data.utils;

import com.bioinnovate.grh.data.entity.Employee;
import com.bioinnovate.grh.data.service.AbsenceService;
import com.bioinnovate.grh.data.service.DelaysService;
import com.bioinnovate.grh.data.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateSoldeDaysOff {
    
    public UpdateSoldeDaysOff(){}

    public static void update(@Autowired DelaysService delaysService, @Autowired AbsenceService absenceService,
                              @Autowired EmployeeService employeeService , @Autowired Employee user){

        List<Integer> workDaysOfEachMonth = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 1; i < today.getMonth().getValue()+1 ; i++){
            LocalDate monthBegin = today.minusMonths(today.getMonthValue()).plusMonths(i).withDayOfMonth(1);
            LocalDate monthEnd = today.minusMonths(today.getMonthValue()).plusMonths(i+1).withDayOfMonth(1).minusDays(1);
            if (i == LocalDate.now().getMonth().getValue()){
                monthEnd = LocalDate.now();
            }
            workDaysOfEachMonth.add(countBusinessDaysBetween(monthBegin,monthEnd));
        }

        List<Double> totalRetardOfEachMonth = new ArrayList<>();
        if (user.getStartWorkDate().getYear() != LocalDate.now().getYear()){
            for (int i = 1; i < LocalDate.now().getMonth().getValue() + 1; i++) {
                try {
                    totalRetardOfEachMonth.add(delaysService.findDelaysByEmployeeAndMonth(user.getId(), i) / (3600 * 24));
                }catch (Exception e){
                    totalRetardOfEachMonth.add(0.0);
                }
            }
        }else {
            for (int i = user.getStartWorkDate().getMonth() + 1; i < LocalDate.now().getMonth().getValue() + 1; i++) {
                try {
                    totalRetardOfEachMonth.add(delaysService.findDelaysByEmployeeAndMonth(user.getId(), i) / (3600 * 24));
                }catch (Exception e){
                    totalRetardOfEachMonth.add(0.0);
                }
            }
        }

        List<Double> totalAbsence = new ArrayList<>();
        if (user.getStartWorkDate().getYear() != LocalDate.now().getYear()){
            for (int i = 1; i < LocalDate.now().getMonth().getValue() + 1; i++) {
                try{
                    totalAbsence.add(absenceService.findAbsencesByEmployeeAndMonth(user.getId(), i) / 1);
                }catch (Exception e){
                    totalAbsence.add(0.0);
                }
            }
        }else{
            for (int i = user.getStartWorkDate().getMonth() + 1; i < LocalDate.now().getMonth().getValue() + 1; i++) {
                try{
                    totalAbsence.add(absenceService.findAbsencesByEmployeeAndMonth(user.getId(), i) / 1);
                }catch (Exception e){
                    totalAbsence.add(0.0);
                }
            }
        }

        Integer totalDays;
        Double totalDaysWorked;
        double totalDaysOffSolde = 0.0;

        if ( ( (user.getStartWorkDate().getMonth()+1 == 1) || (user.getStartWorkDate().getYear() != LocalDate.now().getYear()) )
                && (user.getStartWorkDate().getDate() == 1) ){
            if (user.getStartWorkDate().getMonth()+1 == 1){
                for (int i = 1; i < LocalDate.now().getMonth().getValue() + 1; i++) {
                    totalDays = workDaysOfEachMonth.get(i - 1);
                    totalDaysWorked = totalDays - totalRetardOfEachMonth.get(i - 1) - totalAbsence.get(i - 1);
                    totalDaysOffSolde += 1.5 * (totalDaysWorked / totalDays);
                }
            }else{
                for (int i = user.getStartWorkDate().getMonth()+1; i < LocalDate.now().getMonth().getValue() + 1; i++) {
                    totalDays = workDaysOfEachMonth.get(i - 1);
                    totalDaysWorked = totalDays - totalRetardOfEachMonth.get(i - 1) - totalAbsence.get(i - 1);
                    totalDaysOffSolde += 1.5 * (totalDaysWorked / totalDays);
                }
            }
        }else{
            for (int i = user.getStartWorkDate().getMonth()+1; i < LocalDate.now().getMonth().getValue()+1 ; i++){
                System.out.println(i);
                if (user.getStartWorkDate().getDate() != 1) {
                    totalDays = workDaysOfEachMonth.get(i-1) - countBusinessDaysBetween(new Date(user.getStartWorkDate().getYear(), user.getStartWorkDate().getMonth(), 1).toLocalDate(), user.getStartWorkDate().toLocalDate());
                }else {
                    totalDays = workDaysOfEachMonth.get(i-1) - user.getStartWorkDate().getDate() + 1 ;
                }
                totalDaysWorked = totalDays - totalRetardOfEachMonth.get(i-1) - totalAbsence.get(i-1);
                totalDaysOffSolde += 1.5 * (totalDaysWorked / totalDays);
            }
        }
        user.setDaysOffLeft(Math.round(totalDaysOffSolde*100)/100D);
//        user.setDaysOffLeft(totalDaysOffSolde);
        employeeService.update(user);
    }

    private static int countBusinessDaysBetween(final LocalDate startDate,final LocalDate endDate){
        // Validate method arguments
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween (" + startDate
                            + "," + endDate + ")");
        }

        // Predicate 2: Is a given date is a weekday
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        // Get all days between two dates
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        List<LocalDate> dates = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isWeekend.negate())
                .collect(Collectors.toList());

        return dates.size();
    }
}
