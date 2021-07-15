package Backend;

import java.util.Locale;
import java.util.ResourceBundle;

public class InternationalizationClass {

        /** Atribút bundle predsatvuje súbor, v ktorom sa nachádzajú texty, pre zvolený jazyk. **/
        private static ResourceBundle bundle;

        /** Atribút bundle predsatvuje súbor, v ktorom sa nachádzajú texty, pre zvolený jazyk. **/
        private static ResourceBundle errors;

        /** Atribút locale predstavuje aktuálnu lokalizáciu programu. **/
        private static Locale locale;

        /**
         * Privátny konštruktor triedy {@code InternationalizationClass}.
         */
        private InternationalizationClass() {}

        /**
         * Metóda pre nastavenie súboru, z ktorého sa budú načítavať texty pre grafické rozhranie
         *
         * @param path1 cesta k súboru s jazykovou verziou
         *
         * @param path2 cesta k súboru s jazykovou verziou
         *
         * @param lang jazyk
         *
         * @param country krajina
         */
        public static void setBundle(String path1, String path2, String lang, String country) {
            locale = new Locale(lang, country);
            bundle = ResourceBundle.getBundle(path1, locale, new UTF8Control());
            errors = ResourceBundle.getBundle(path2, locale, new UTF8Control());
        }

        /**
         * Metóda pre získanie aktuálnej jazykovej verzie.
         *
         * @return súbor s aktuálnou jazykovou verziou
         */
        public static ResourceBundle getBundle() {
            return bundle;
        }

        public static ResourceBundle getErrors() {
        return errors;
    }

        /**
         * Metóda pre získanie aktuálnej lokalizácie.
         *
         * @return aktuálna lokalizácia
         */
        public static Locale getLocale() {
            return locale;
        }
}
