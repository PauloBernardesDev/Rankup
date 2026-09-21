package dev.paulobernardes.rankup.util;

public final class TempoUtil {

    private TempoUtil() {
    }

    public static long horasParaSegundos(double horas) {
        return Math.round(horas * 60L * 60L);
    }

    public static double segundosParaHoras(long segundos) {
        return segundos / 3600.0;
    }

    public static String formatarHoras(long segundos) {

        if (segundos <= 0) {
            return "0h";
        }

        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;

        if (minutos == 0) {
            return horas + "h";
        }

        return horas + "h " + minutos + "min";
    }
}