package es.gobcan.istac.edatos.external.users.core.util;

import org.apache.commons.lang3.RandomStringUtils;

public final class UrnUtil {

    private static final String REGEX_URN = ":|\\(|\\)";
    private static final String EQUALS_SPLIT = "=";

    private UrnUtil() {
    }

    public static String getAgency(String urn) {
        String[] splittedUrn = urn.split(EQUALS_SPLIT);
        String[] resourceUrn = splittedUrn[1].split(REGEX_URN);

        return resourceUrn[0];
    }

    public static String getResourceId(String urn) {
        String[] splittedUrn = urn.split(EQUALS_SPLIT);
        String[] resourceUrn = splittedUrn[1].split(REGEX_URN);

        return resourceUrn[1];
    }

    public static String getVersion(String urn) {
        String[] splittedUrn = urn.split(EQUALS_SPLIT);
        String[] resourceUrn = splittedUrn[1].split(REGEX_URN);

        return resourceUrn[2];
    }
}
