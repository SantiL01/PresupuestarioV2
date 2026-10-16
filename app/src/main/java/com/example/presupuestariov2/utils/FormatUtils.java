package com.example.presupuestariov2.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class FormatUtils {

    private FormatUtils() {}

    public static String greetingForHour(int hour) {
        if (hour < 12) return "Buenos días";
        if (hour < 20) return "Buenas tardes";
        return "Buenas noches";
    }

    public static String currentMonthTitle() {
        String mes = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"))
                .format(Calendar.getInstance().getTime());
        if (mes.isEmpty()) return mes;
        return mes.substring(0, 1).toUpperCase(new Locale("es", "ES")) + mes.substring(1);
    }

    public static String money(double value) {
        return String.format(Locale.getDefault(), "$ %.2f", value);
    }

    public static String pendingTotal(double total, int count) {
        return String.format(Locale.getDefault(), "$ %.2f pendiente (%d)", total, count);
    }

    public static String dateText(long millis) {
        try {
            return new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"))
                    .format(new Date(millis));
        } catch (Exception e) {
            return "Hoy";
        }
    }
}
