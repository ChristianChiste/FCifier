package utils;

import java.util.List;

public class ListUtils {

    public static boolean isNotEmpty(final List<?> list) {
        return list != null && !list.isEmpty();
    }

    public static boolean isEmpty(final List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean allNotEmpty(final List<?>... lists) {
        for (final List<?> list : lists) {
            if (isEmpty(list)) {
                return false;
            }
        }
        return true;
    }

}
